package com.zipwhip.integration.zipchat.poller;

import static com.zipwhip.service.MessageProcessingRecorder.ProcessingState.FAILED_TO_PROCESS;
import static com.zipwhip.service.MessageProcessingRecorder.ProcessingState.RECORD_PROCESSED;
import static com.zipwhip.service.MessageProcessingRecorder.RecordType.CONTACT;
import static com.zipwhip.service.MessageProcessingRecorder.RecordType.MESSAGE;

import com.google.common.util.concurrent.RateLimiter;
import com.zipwhip.integration.zipchat.entities.OrgConfig;
import com.zipwhip.integration.zipchat.repository.OrgConfigRepository;
import com.zipwhip.integration.zipchat.service.SyncService;
import com.zipwhip.kafka.poller.AbstractPoller;
import com.zipwhip.logging.CompletionCode;
import com.zipwhip.logging.IntegrationFeature;
import com.zipwhip.logging.MDC;
import com.zipwhip.logging.MDCFields;
import com.zipwhip.logging.MDCUtil;
import com.zipwhip.message.domain.InboundBase;
import com.zipwhip.message.domain.InboundContact;
import com.zipwhip.message.domain.InboundMessage;
import com.zipwhip.message.utils.MessageUtils;
import com.zipwhip.service.MessageProcessingRecorder;
import com.zipwhip.subscription.domain.SubscriptionInfo.SubscriptionRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Poller which will poll Kafka for records (messages, contacts) to be synced or otherwise
 * processed
 */
@Component
@Data
@Slf4j
public class DatahubPoller extends AbstractPoller<DatahubPoller.ZipChatExecutionContext> {

  /**
   * A repository holding the configuration data for orgCustomers
   */
  @Autowired
  private OrgConfigRepository orgConfigRepository;

  /**
   * A service to process the syncing of messages
   */
  @Autowired
  private SyncService syncService;

  /**
   * A service for recording state of sync messages and contacts
   */
  @Autowired
  private MessageProcessingRecorder messageProcessingRecorder;

  /**
   * Flag indicating if any sync is enabled on this service
   */
  @Value("${features.sync.enabled:true}")
  private boolean syncEnabled;

  /**
   * Flag indicating if this service is configured for processing of contacts
   */
  @Value("${features.sync.contact.enabled:false}")
  private boolean contactSyncEnabled;

  /**
   * The name of the application
   */
  @Value("${spring.application.name:ZipChat}")
  private String appName;

  /**
   * Processes the consumption of a set of records from Kafka (invoked by parent class)
   *
   * @param records     The records to process
   * @param context     The context of this execution thread
   * @param rateLimited A rate limiter which may slow processing to prevent overloading of an
   *                    orgCustomers CRM
   */
  @Override
  protected void consume(ConsumerRecords<String, InboundBase<?>> records,
    ZipChatExecutionContext context,
    RateLimiter rateLimited) {

    try {
      MDC.put(MDCFields.ORG_CUSTOMER_ID, context.getOrgConfig().getOrgCustomerId());
      //If sync is enabled, process the message
      if (syncEnabled) {
        records.forEach(x -> processRecord(x, context));
        context.commit();
      } else {
        log.warn("Sync feature disabled at service level, skipping");
      }
    } catch (Exception e) {
      log.error("Unhandled error", e);
      throw e;
    } finally {
      MDC.clear();
    }

  }

  /**
   * Forces the system to process a message as if it had come in via kafka (however this bypasses
   * kafka and subscription polling)
   *
   * @param toProcess     The message or contact to process
   * @param orgCustomerId The orgCustomerId processing the message
   */
  public void forceProcessRecord(InboundBase<?> toProcess, Long orgCustomerId) {
    OrgConfig orgConfig = orgConfigRepository.findById(orgCustomerId).orElse(null);
    if (orgConfig != null) {
      ZipChatExecutionContext context = new ZipChatExecutionContext(orgConfig);
      ConsumerRecord<String, InboundBase<?>> record = new ConsumerRecord<>("forced", 0, 0, null,
        toProcess);
      processRecord(record, context);
    } else {
      log.warn("No org config found for {}; cannot process message", orgCustomerId);
    }

  }

  /**
   * Processes a single record retrieved from kafka
   *
   * @param record  The record to process
   * @param context The context of the executing thread
   */
  private void processRecord(ConsumerRecord<String, InboundBase<?>> record,
    ZipChatExecutionContext context) {

    boolean success = false;
    OrgConfig orgConfig = context.getOrgConfig();
    Long orgId = context.getOrgConfig().getOrgCustomerId();
    MDC.put(MDCFields.ORG_CUSTOMER_ID, orgId);

    //Only perform processing if sync is enabled
    if (syncEnabled) {
      try {
        InboundBase<?> base = record.value();

        //Process as message
        if (base instanceof InboundMessage) {

          //Extract critical logging info
          MDCUtil.startFeature(IntegrationFeature.MESSAGE_TO_EXTERNAL);
          InboundMessage message = (InboundMessage) base;
          Long messageId = message.getPayload().getId();
          if (messageProcessingRecorder.startProcessing(MESSAGE, messageId, appName)) {
            MDC.put(MDCFields.MSG_ID, messageId);
            try {
              MDC.put(MDCFields.LANDLINE, MessageUtils.getLandline(message));
              MDC.put(MDCFields.MOBILE, MessageUtils.getPhone(message));
            } catch (NullPointerException e) {
              log
                .warn("Failed to setup MDC fields for message: {}; {}", message, e.getMessage(), e);
            }

            //Log message
            log.info("Processing message ID {}. Org ID {}", messageId, orgId);

            try {

              //Process message
              syncService.processMessage(message, orgConfig);

              log.info("Processing message ID {}. Org ID {}. Done", messageId, orgId);
              success = true;

            } catch (Exception e) {
              log.error("Failed to process message {}; {}", message, e.getMessage(), e);
            } finally {
              messageProcessingRecorder
                .completeProcessing(success ? RECORD_PROCESSED : FAILED_TO_PROCESS, MESSAGE,
                  messageId, appName);
            }
          }

        } else if (base instanceof InboundContact && ("save".equals(base.getAction()) || "new"
          .equals(base.getAction()))) {

          //Process as message
          if (contactSyncEnabled) {

            //Extract critical logging info
            MDCUtil.startFeature(IntegrationFeature.CONTACT_TO_EXTERNAL);
            InboundContact contact = (InboundContact) base;
            Long contactId = contact.getPayload().getId();

            //Log contact
            log.info("Processing contact ID {}. Org ID {}", contactId, orgId);

            try {
              messageProcessingRecorder.startProcessing(CONTACT, contactId, appName);

              log.info("Processing contact ID {}. Org ID {}. Done", contactId, orgId);
              success = true;

            } catch (Exception e) {
              log.error("Failed to process contact {}; {}", contact, e.getMessage(), e);
            } finally {
              messageProcessingRecorder
                .completeProcessing(success ? RECORD_PROCESSED : FAILED_TO_PROCESS, CONTACT,
                  contactId, appName);
            }

          } else {
            log.debug("Contact Sync feature disabled at service level, skipping");
          }

        }
      } finally {
        MDCUtil.endFeature(success ? CompletionCode.SUCCESS : CompletionCode.FAILURE);
        MDC.clear();
      }
    } else {
      log.warn("Sync feature disabled at service level, skipping");
    }
  }

  /**
   * Creates an execution context for the given install of this integration
   *
   * @param record The installation to create a poller execution for
   * @return A context which can drive and maintain state for the poller for a given install
   */
  @Override
  protected ZipChatExecutionContext createContext(SubscriptionRecord record) {
    return new ZipChatExecutionContext(
      orgConfigRepository.findById(record.getCustomerId()).orElse(null));
  }

  /**
   * Updates the in memory loaded orgConfig for the given execution context
   *
   * @param context The context for the poller scanning kafka
   * @param record  The install record to update the config for
   */
  @Override
  protected void refreshSubscription(ZipChatExecutionContext context, SubscriptionRecord record) {
    context.orgConfig = orgConfigRepository.findById(record.getCustomerId()).orElse(null);
  }

  /**
   * A ZipChat specific execution context which can be used to store state for a given poller
   * thread
   */
  @EqualsAndHashCode(callSuper = false)
  @AllArgsConstructor
  @Data
  static class ZipChatExecutionContext extends AbstractPoller.ExecutionContext {

    /**
     * The configuration for the given orgCustomer's installation of this integration
     */
    OrgConfig orgConfig;
  }

}
