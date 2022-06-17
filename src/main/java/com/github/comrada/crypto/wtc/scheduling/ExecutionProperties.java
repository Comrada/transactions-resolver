package com.github.comrada.crypto.wtc.scheduling;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.execution")
public class ExecutionProperties {

  private int maxRetryAttempts;
  private DatabaseProperties database;
  private AmqpProperties amqp;

  public int getMaxRetryAttempts() {
    return maxRetryAttempts;
  }

  public void setMaxRetryAttempts(int maxRetryAttempts) {
    this.maxRetryAttempts = maxRetryAttempts;
  }

  public DatabaseProperties getDatabase() {
    return database;
  }

  public void setDatabase(DatabaseProperties database) {
    this.database = database;
  }

  public AmqpProperties getAmqp() {
    return amqp;
  }

  public void setAmqp(AmqpProperties amqp) {
    this.amqp = amqp;
  }

  public static final class DatabaseProperties {

    private boolean poll;
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration initialDelay;
    @DurationUnit(ChronoUnit.SECONDS)
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

  public static final class AmqpProperties {

    private String exchange;
    private String routingKey;

    public String getExchange() {
      return exchange;
    }

    public void setExchange(String exchange) {
      this.exchange = exchange;
    }

    public String getRoutingKey() {
      return routingKey;
    }

    public void setRoutingKey(String routingKey) {
      this.routingKey = routingKey;
    }
  }
}
