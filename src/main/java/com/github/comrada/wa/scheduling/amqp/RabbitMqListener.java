package com.github.comrada.wa.scheduling.amqp;

import static java.util.Objects.requireNonNull;

import com.github.comrada.wa.events.NewAlertEvent;
import com.github.comrada.wa.model.WhaleAlert;
import com.github.comrada.wa.model.WhaleAlert.ProcessingStatus;
import com.github.comrada.wa.repository.WhaleAlertRepository;
import com.github.comrada.wa.scheduling.ExecutionException;
import com.github.comrada.wa.scheduling.TaskExecutor;
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
        .map(this::convert)
        .filter(alert -> StringUtils.hasText(alert.getLink()))
        .forEach(alert -> {
          LOGGER.info("New alert with id: {} received, asset: {}", alert.getId(), alert.getAsset());
          doExecution(alert);
          LOGGER.info("Finished processing alert: {}", alert.getId());
        });
  }

  private void doExecution(WhaleAlert whaleAlert) {
    try {
      taskExecutor.submit(whaleAlert);
      alertRepository.done(whaleAlert.getId(), Instant.now());
    } catch (ExecutionException e) {
      alertRepository.fail(whaleAlert.getId(), Instant.now());
      LOGGER.error("Alert {} processing failed. Reason: {}", whaleAlert.getId(), e.getMessage());
    }
  }

  private WhaleAlert convert(NewAlertEvent event) {
    WhaleAlert whaleAlert = new WhaleAlert();
    whaleAlert.setId(event.id());
    whaleAlert.setAmount(event.amount());
    whaleAlert.setAsset(event.asset());
    whaleAlert.setLink(event.link());
    whaleAlert.setMessage(event.message());
    whaleAlert.setPostedAt(event.postedAt().toInstant());
    whaleAlert.setProcessStatus(ProcessingStatus.NEW);
    return whaleAlert;
  }
}
