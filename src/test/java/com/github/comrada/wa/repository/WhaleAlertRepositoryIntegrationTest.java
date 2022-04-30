package com.github.comrada.wa.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.comrada.wa.model.WhaleAlert;
import com.github.comrada.wa.model.WhaleAlert.ProcessingStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
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
    assertEquals(ProcessingStatus.NEW, alert.getProcessStatus());
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
}