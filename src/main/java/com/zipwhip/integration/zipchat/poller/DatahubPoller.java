package com.zipwhip.integration.zipchat.poller;

import com.google.common.util.concurrent.RateLimiter;
import com.zipwhip.integration.zipchat.entities.OrgConfig;
import com.zipwhip.integration.zipchat.repository.OrgConfigRepository;
import com.zipwhip.integration.zipchat.service.SyncService;
import com.zipwhip.kafka.poller.AbstractPoller;
import com.zipwhip.logging.*;
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
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static com.zipwhip.service.MessageProcessingRecorder.ProcessingState.FAILED_TO_PROCESS;
import static com.zipwhip.service.MessageProcessingRecorder.ProcessingState.RECORD_PROCESSED;
import static com.zipwhip.service.MessageProcessingRecorder.RecordType.CONTACT;
import static com.zipwhip.service.MessageProcessingRecorder.RecordType.MESSAGE;

/**
 * Poller which will poll Kafka for records (messages, contacts) to be synced or otherwise
 * processed
 */
@Profile("kafka")
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
   * The name of the application
   */
  @Value("${spring.application.name:Guidewire}")
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


  }

  /**
   * Forces the system to process a message as if it had come in via kafka (however this bypasses
   * kafka and subscription polling)
   *
   * @param toProcess     The message or contact to process
   * @param orgCustomerId The orgCustomerId processing the message
   */
  public void forceProcessRecord(InboundBase<?> toProcess, Long orgCustomerId) {

  }

  /**
   * Processes a single record retrieved from kafka
   *
   * @param record  The record to process
   * @param context The context of the executing thread
   */
  private void processRecord(ConsumerRecord<String, InboundBase<?>> record,
                             ZipChatExecutionContext context) {

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
   * A Guidewire specific execution context which can be used to store state for a given poller
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
