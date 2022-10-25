package com.github.comrada.crypto.wtc.scheduling.database;

import java.util.Random;

public final class DelayGenerator {

  private final long minDelay;
  private final long maxDelay;
  private final Random random;

  public DelayGenerator(long minDelay, long maxDelay) {
    this.minDelay = minDelay;
    this.maxDelay = maxDelay;
    this.random = new Random();
  }

  public long getDelay() {
    return random.nextLong(minDelay, maxDelay);
  }
}
