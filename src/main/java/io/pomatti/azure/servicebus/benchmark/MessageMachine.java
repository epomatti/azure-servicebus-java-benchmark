package io.pomatti.azure.servicebus.benchmark;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageMachine {

  Logger logger = LoggerFactory.getLogger(getClass());

  ServiceBusSender sender;

  public MessageMachine(ServiceBusSender sender) {
    this.sender = sender;
  }

  public void start() throws RuntimeException {
    Set<Integer> dataset = getLargeDataset();
    var messageQuantity = dataset.size();
    var messageBodyBytes = Integer.parseInt(Config.getProperty("app.message_body_bytes"));

    final String body = "8".repeat(messageBodyBytes);

    int size = Integer.parseInt(Config.getProperty("app.sender_threads"));
    ForkJoinPool pool = new ForkJoinPool(size);

    Instant starts = Instant.now();
    try {
      pool.submit(() -> dataset.stream().parallel().forEach(i -> {
        sender.send(body);
      })).get();
      Instant ends = Instant.now();

      logger.info(String.format("Total messages sent: %s", messageQuantity));
      logger.info(String.format("Duration: %s ms", Duration.between(starts, ends).toMillis()));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  private Set<Integer> getLargeDataset() {
    logger.info("Started building datasource");
    Set<Integer> counter = new HashSet<>();
    Integer qty = Integer.parseInt(Config.getProperty("app.message_quantity"));
    for (int i = 0; i < qty; i++) {
      counter.add(i);
    }
    logger.info(counter.size() + "");
    logger.info("Finished building datasource");
    return counter;
  }

}