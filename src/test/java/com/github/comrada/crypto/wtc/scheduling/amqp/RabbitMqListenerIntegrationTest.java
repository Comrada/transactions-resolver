package com.github.comrada.crypto.wtc.scheduling.amqp;

import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.github.comrada.crypto.wtc.events.NewAlertEvent;
import com.github.comrada.crypto.wtc.model.WhaleAlert;
import com.github.comrada.crypto.wtc.model.WhaleAlert.ProcessingStatus;
import com.github.comrada.crypto.wtc.repository.WhaleAlertRepository;
import com.github.comrada.crypto.wtc.scheduling.ExecutionException;
import com.github.comrada.crypto.wtc.scheduling.TaskExecutor;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RabbitMqListenerIntegrationTest {

  private TaskExecutor<WhaleAlert> taskExecutor;
  private WhaleAlertRepository alertRepository;
  private RabbitMqListener testListener;

  @BeforeEach
  void configureBean() {
    taskExecutor = mock(TaskExecutor.class);
    alertRepository = mock(WhaleAlertRepository.class);
    testListener = new RabbitMqListener(taskExecutor, alertRepository);
  }

  @Test
  void whenAlertsHaveNoLinks_thenTheyAreNotProcessed() {
    NewAlertEvent event = new NewAlertEvent(
        1L,
        "fake message",
        null,
        ZonedDateTime.now(),
        "BTC",
        BigDecimal.valueOf(100)
    );
    testListener.listen(singletonList(event));

    verifyNoInteractions(alertRepository);
    verifyNoInteractions(taskExecutor);
  }

  @Test
  void whenAlertsAreAlreadyDone_thenTheyAreNotProcessed() {
    NewAlertEvent event = new NewAlertEvent(
        1L,
        "fake message",
        "http://fake-url",
        ZonedDateTime.now(),
        "BTC",
        BigDecimal.valueOf(100)
    );
    testListener.listen(singletonList(event));

    verify(alertRepository, times(1)).selectForExecution(event.id());
    verifyNoInteractions(taskExecutor);
  }

  @Test
  void whenAlertsAreNew_thenTheyAreProcessed() {
    WhaleAlert alert = new WhaleAlert();
    alert.setId(1L);
    alert.setProcessStatus(ProcessingStatus.NEW);
    when(alertRepository.selectForExecution(1L)).thenReturn(Optional.of(alert));
    NewAlertEvent event = new NewAlertEvent(
        1L,
        "fake message",
        "http://fake-url",
        ZonedDateTime.now(),
        "BTC",
        BigDecimal.valueOf(100)
    );
    testListener.listen(singletonList(event));

    verify(alertRepository, times(1)).selectForExecution(event.id());
    verify(taskExecutor, times(1)).submit(alert);
    verify(alertRepository, times(1)).done(eq(event.id()), any());
  }

  @Test
  void whenExecutionFailed_thenTheyAlertSavedAsFailed() {
    WhaleAlert alert = new WhaleAlert();
    alert.setId(1L);
    alert.setProcessStatus(ProcessingStatus.NEW);
    when(alertRepository.selectForExecution(1L)).thenReturn(Optional.of(alert));
    doThrow(new ExecutionException()).when(taskExecutor).submit(alert);
    NewAlertEvent event = new NewAlertEvent(
        1L,
        "fake message",
        "http://fake-url",
        ZonedDateTime.now(),
        "BTC",
        BigDecimal.valueOf(100)
    );
    testListener.listen(singletonList(event));

    verify(alertRepository, times(1)).selectForExecution(event.id());
    verify(taskExecutor, times(1)).submit(alert);
    verify(alertRepository, times(1)).fail(eq(event.id()), any());
  }
}