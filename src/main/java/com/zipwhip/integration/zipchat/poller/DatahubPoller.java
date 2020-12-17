package com.zipwhip.integration.zipchat.poller;

import com.google.common.util.concurrent.RateLimiter;
import com.zipwhip.integration.zipchat.entities.OrgConfig;
import com.zipwhip.integration.zipchat.repository.OrgConfigRepository;
import com.zipwhip.integration.zipchat.service.MessageProcessor;
import com.zipwhip.kafka.poller.AbstractPoller;
import com.zipwhip.logging.MDC;
import com.zipwhip.logging.MDCFields;
import com.zipwhip.message.domain.InboundBase;
import com.zipwhip.message.domain.InboundContact;
import com.zipwhip.message.domain.InboundMessage;
import com.zipwhip.subscription.domain.SubscriptionInfo.SubscriptionRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Poller which will poll Kafka for records (messages, contacts) to be synced or otherwise
 * processed
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DatahubPoller extends AbstractPoller<DatahubPoller.ZipChatExecutionContext> {

  /**
   * A repository holding the configuration data for orgCustomers
   */
  private final OrgConfigRepository orgConfigRepository;

  private final MessageProcessor messageProcessor;

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
      records.forEach(this::processRecord);
      context.commit();
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
      processRecord(record);
    } else {
      log.warn("No org config found for {}; cannot process message", orgCustomerId);
    }

  }

  /**
   * Processes a single record retrieved from kafka
   *
   * @param record The record to process
   */
  private void processRecord(ConsumerRecord<String, InboundBase<?>> record) {

    //Only perform processing if sync is enabled
    InboundBase<?> base = record.value();

    //Process as message
    if (base instanceof InboundMessage) {

      InboundMessage message = (InboundMessage) base;

      //Log message
      log.debug("Processing message {}", message);

      try {

        //Process message
        messageProcessor.process(message);

      } catch (Exception e) {
        log.error("Failed to process message {}; {}", message, e.getMessage(), e);
      }
    } else if (base instanceof InboundContact && ("save".equals(base.getAction()) || "new"
        .equals(base.getAction()))) {

      // TODO - warn since not supported
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
