package io.github.comrada.crypto.wtc.scheduling.database;

import static java.util.Objects.requireNonNull;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

import io.github.comrada.crypto.wtc.model.WhaleAlert;
import io.github.comrada.crypto.wtc.repository.WhaleAlertRepository;
import io.github.comrada.crypto.wtc.scheduling.ExecutionException;
import io.github.comrada.crypto.wtc.scheduling.TaskExecutor;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabasePoller {

  private static final Logger LOGGER = LoggerFactory.getLogger(DatabasePoller.class);
  private final WhaleAlertRepository alertRepository;
  private final long noJobDelay;
  private final ScheduledExecutorService scheduler = newSingleThreadScheduledExecutor();
  private final TaskExecutor<WhaleAlert> taskExecutor;
  private final DelayGenerator delayGenerator;

  public DatabasePoller(WhaleAlertRepository alertRepository, long initialDelay, long noJobDelay,
      TaskExecutor<WhaleAlert> taskExecutor, DelayGenerator delayGenerator) {
    this.alertRepository = requireNonNull(alertRepository);
    this.taskExecutor = requireNonNull(taskExecutor);
    this.noJobDelay = noJobDelay;
    this.delayGenerator = requireNonNull(delayGenerator);
    pollWithDelay(initialDelay);
    LOGGER.info("Database poller initialized. Initial delay: {}s, no-job delay: {}s", initialDelay,
        noJobDelay);
  }

  private void poll() {
    Optional<WhaleAlert> foundAlert = alertRepository.selectForExecution();
    if (foundAlert.isPresent() && isLinkPresent(foundAlert.get())) {
      WhaleAlert whaleAlert = foundAlert.get();
      LOGGER.info("Start processing alert id: {}, asset: {}", whaleAlert.getId(),
          whaleAlert.getAsset());
      doExecution(whaleAlert);
      long jobDoneDelay = delayGenerator.getDelay();
      LOGGER.info("Finished processing alert: {}, waiting for {}s  before next alert...",
          whaleAlert.getId(), jobDoneDelay);
      pollWithDelay(jobDoneDelay);
    } else {
      LOGGER.debug("No new jobs, waiting for {}s...", noJobDelay);
      pollWithDelay(noJobDelay);
    }
  }

  private boolean isLinkPresent(WhaleAlert alert) {
    return alert.getLink() != null && alert.getLink().startsWith("https://");
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

  private void pollWithDelay(long delay) {
    LOGGER.debug("Scheduling a poll task with delay {}s", delay);
    scheduler.schedule(this::poll, delay, TimeUnit.SECONDS);
  }
}
