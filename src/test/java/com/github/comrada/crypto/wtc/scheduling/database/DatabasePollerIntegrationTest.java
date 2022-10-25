package com.github.comrada.crypto.wtc.scheduling.database;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.comrada.crypto.wtc.repository.WhaleAlertRepository;
import com.github.comrada.crypto.wtc.scheduling.TaskExecutor;
import com.github.comrada.crypto.wtc.model.WhaleAlert;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class DatabasePollerIntegrationTest {

  @MockBean
  WhaleAlertRepository repository;
  @MockBean
  TaskExecutor<WhaleAlert> taskExecutor;

  @Test
  void test() {
    WhaleAlert whaleAlert = new WhaleAlert();
    whaleAlert.setId(1L);
    when(repository.selectForExecution()).thenReturn(Optional.of(whaleAlert));
    DatabasePoller databasePoller = new DatabasePoller(repository, 0, 0, taskExecutor);

    await()
        .atMost(5, TimeUnit.SECONDS)
        .untilAsserted(() -> verify(repository, times(1)).selectForExecution());

    await()
        .atMost(5, TimeUnit.SECONDS)
        .untilAsserted(() -> verify(taskExecutor, times(1)).submit(whaleAlert));
  }
}
