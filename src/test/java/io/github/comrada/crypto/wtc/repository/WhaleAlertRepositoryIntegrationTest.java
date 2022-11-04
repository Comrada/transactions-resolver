package io.github.comrada.crypto.wtc.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.comrada.crypto.wtc.model.WhaleAlert;
import io.github.comrada.crypto.wtc.model.WhaleAlert.ProcessingStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
class WhaleAlertRepositoryIntegrationTest {

  @Autowired
  WhaleAlertRepository whaleAlertRepository;

  @Test
  @Sql("whale_alerts.sql")
  void whenRequestingUnprocessedAlertsWithStatusNew_thenGetThem() {
    List<WhaleAlert> alerts = whaleAlertRepository.findNewAlerts(1);
    assertFalse(alerts.isEmpty());
    assertEquals(1, alerts.size());

    WhaleAlert alert = alerts.stream().findFirst().get();
    assertEquals(1, alert.getId());
    Assertions.assertEquals(ProcessingStatus.NEW, alert.getProcessStatus());
  }

  @Test
  @Sql("whale_alerts.sql")
  void whenRequestingAlertsWithStatusFailed_thenGetThem() {
    List<WhaleAlert> alerts = whaleAlertRepository.findFailedAlerts(1);
    assertFalse(alerts.isEmpty());
    assertEquals(1, alerts.size());

    WhaleAlert alert = alerts.stream().findFirst().get();
    assertEquals(2, alert.getId());
    assertEquals(ProcessingStatus.FAILED, alert.getProcessStatus());
  }

  @Test
  @Sql("whale_alerts.sql")
  void whenRequestingAlertsWithStatusDone_thenGetThem() {
    List<WhaleAlert> alerts = whaleAlertRepository.findProcessedAlerts(1);
    assertFalse(alerts.isEmpty());
    assertEquals(1, alerts.size());

    WhaleAlert alert = alerts.stream().findFirst().get();
    assertEquals(3, alert.getId());
    assertEquals(ProcessingStatus.DONE, alert.getProcessStatus());
  }

  @Test
  @Sql("whale_alerts.sql")
  void fail() {
    whaleAlertRepository.fail(1, Instant.now());
    Optional<WhaleAlert> found = whaleAlertRepository.findById(1L);
    assertTrue(found.isPresent());
    WhaleAlert alert = found.get();
    assertEquals(ProcessingStatus.FAILED, alert.getProcessStatus());
  }

  @Test
  @Sql("whale_alerts.sql")
  void done() {
    whaleAlertRepository.done(1, Instant.now());
    Optional<WhaleAlert> found = whaleAlertRepository.findById(1L);
    assertTrue(found.isPresent());
    WhaleAlert alert = found.get();
    assertEquals(ProcessingStatus.DONE, alert.getProcessStatus());
  }

  @Test
  @Sql("whale_alerts.sql")
  void findByIdAndStatusNew() {
    Optional<WhaleAlert> found = whaleAlertRepository.findByIdAndStatus(1, ProcessingStatus.NEW);
    assertTrue(found.isPresent());
    WhaleAlert alert = found.get();
    assertEquals(1, alert.getId());
    assertEquals(ProcessingStatus.NEW, alert.getProcessStatus());
  }

  @Test
  @Sql("whale_alerts.sql")
  void findByIdAndStatusInProgress() {
    Optional<WhaleAlert> found = whaleAlertRepository.findByIdAndStatus(4, ProcessingStatus.IN_PROGRESS);
    assertTrue(found.isPresent());
    WhaleAlert alert = found.get();
    assertEquals(4, alert.getId());
    assertEquals(ProcessingStatus.IN_PROGRESS, alert.getProcessStatus());
  }

  @Test
  @Sql("whale_alerts.sql")
  void findByIdAndStatusDone() {
    Optional<WhaleAlert> found = whaleAlertRepository.findByIdAndStatus(3, ProcessingStatus.DONE);
    assertTrue(found.isPresent());
    WhaleAlert alert = found.get();
    assertEquals(3, alert.getId());
    assertEquals(ProcessingStatus.DONE, alert.getProcessStatus());
  }

  @Test
  @Sql("whale_alerts.sql")
  void findByIdAndStatusFailed() {
    Optional<WhaleAlert> found = whaleAlertRepository.findByIdAndStatus(2, ProcessingStatus.FAILED);
    assertTrue(found.isPresent());
    WhaleAlert alert = found.get();
    assertEquals(2, alert.getId());
    assertEquals(ProcessingStatus.FAILED, alert.getProcessStatus());
  }
}