package com.github.comrada.wa.scheduling;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.execution")
public class ExecutionProperties {

  private Duration initialDelay;
  private int maxRetryAttempts;
  private DatabaseProperties database;

  public Duration getInitialDelay() {
    return initialDelay;
  }

  public int getMaxRetryAttempts() {
    return maxRetryAttempts;
  }

  public void setMaxRetryAttempts(int maxRetryAttempts) {
    this.maxRetryAttempts = maxRetryAttempts;
  }

  public void setInitialDelay(Duration initialDelay) {
    this.initialDelay = initialDelay;
  }

  public DatabaseProperties getDatabase() {
    return database;
  }

  public void setDatabase(DatabaseProperties database) {
    this.database = database;
  }

  public static final class DatabaseProperties {

    private boolean poll;
    private Duration initialDelay;
    private Duration noJobDelay;

    public boolean isPoll() {
      return poll;
    }

    public void setPoll(boolean poll) {
      this.poll = poll;
    }

    public Duration getInitialDelay() {
      return initialDelay;
    }

    public void setInitialDelay(Duration initialDelay) {
      this.initialDelay = initialDelay;
    }

    public Duration getNoJobDelay() {
      return noJobDelay;
    }

    public void setNoJobDelay(Duration noJobDelay) {
      this.noJobDelay = noJobDelay;
    }
  }
}
