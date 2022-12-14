package io.pomatti.azure.servicebus.benchmark;

import java.io.Closeable;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusErrorContext;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;

public class ServiceBusConsumer implements Closeable {

  Logger logger = LoggerFactory.getLogger(getClass());

  private ServiceBusProcessorClient processorClient;

  private String connectionString;
  private String queue;
  private Integer maxConcurrentCalls;
  private Integer prefetchCount;

  public ServiceBusConsumer() {
    connectionString = Config.getProperty("app.servicebus.connection_string");
    queue = Config.getProperty("app.servicebus.queue");
    maxConcurrentCalls = Integer.parseInt(Config.getProperty("app.servicebus.max_concurrent_calls"));
    prefetchCount = Integer.parseInt(Config.getProperty("app.servicebus.prefetch_count"));
  }

  public void start() {
    Consumer<ServiceBusReceivedMessageContext> processMessage = messageContext -> {
      try {
        messageContext.getMessage().getBody().toString();
        messageContext.complete();
      } catch (Exception ex) {
        logger.error("Error processing message", ex);
        messageContext.abandon();
      }
    };

    Consumer<ServiceBusErrorContext> processError = errorContext -> {
      logger.error("Error occurred while receiving message", errorContext.getException());
    };

    processorClient = new ServiceBusClientBuilder()
        .connectionString(connectionString)
        .processor()
        .maxConcurrentCalls(maxConcurrentCalls)
        .prefetchCount(prefetchCount)
        .queueName(queue)
        .processMessage(processMessage)
        .processError(processError)
        .disableAutoComplete()
        .buildProcessorClient();

    processorClient.start();
  }

  public void close() {
    processorClient.close();
  }

}
