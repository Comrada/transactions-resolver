package io.github.comrada.crypto.wtc.scheduling.database;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.RepeatedTest;

class DelayGeneratorTest {

  @RepeatedTest(10)
  void getDelay() {
    DelayGenerator delayGenerator = new DelayGenerator(1, 10);
    long delay = delayGenerator.getDelay();
    assertTrue(delay >= 1);
    assertTrue(delay < 10);
  }
}