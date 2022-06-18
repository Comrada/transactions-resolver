package com.github.comrada.crypto.wtc.scheduling.amqp;

import static java.util.Objects.requireNonNull;

import com.github.comrada.crypto.wtc.events.NewAlertEvent;
import com.github.comrada.crypto.wtc.model.WhaleAlert;
import com.github.comrada.crypto.wtc.repository.WhaleAlertRepository;
import com.github.comrada.crypto.wtc.scheduling.ExecutionException;
import com.github.comrada.crypto.wtc.scheduling.TaskExecutor;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.util.StringUtils;

public final class RabbitMqListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqListener.class);
  private final TaskExecutor<WhaleAlert> taskExecutor;
  private final WhaleAlertRepository alertRepository;

  public RabbitMqListener(TaskExecutor<WhaleAlert> taskExecutor, WhaleAlertRepository alertRepository) {
    this.taskExecutor = requireNonNull(taskExecutor);
    this.alertRepository = requireNonNull(alertRepository);
  }

  @RabbitListener(queues = {"#{whalesQueue.getName()}"})
  public void listen(List<NewAlertEvent> event) {
    event.stream()
        .filter(alert -> StringUtils.hasText(alert.link()))
        .forEach(alert -> {
          LOGGER.info("New alert with id: {} received, asset: {}", alert.id(), alert.asset());
          doExecution(alert);
          LOGGER.info("Finished processing alert: {}", alert.id());
        });
  }

  private void doExecution(NewAlertEvent whaleAlert) {
    try {
      alertRepository
          .selectForExecution(whaleAlert.id())
          .ifPresent(existingRecord -> {
            taskExecutor.submit(existingRecord);
            alertRepository.done(existingRecord.getId(), Instant.now());
          });
    } catch (ExecutionException e) {
      alertRepository.fail(whaleAlert.id(), Instant.now());
      LOGGER.error("Alert {} processing failed. Reason: {}", whaleAlert.id(), e.getMessage());
    }
  }
}
