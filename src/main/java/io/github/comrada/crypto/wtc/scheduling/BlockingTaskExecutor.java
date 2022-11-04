package io.github.comrada.crypto.wtc.scheduling;

import static java.util.Objects.requireNonNull;

import java.util.function.Consumer;

public class BlockingTaskExecutor<T> implements TaskExecutor<T> {

  private final Consumer<T> taskConsumer;

  public BlockingTaskExecutor(Consumer<T> task) {
    this.taskConsumer = requireNonNull(task);
  }

  @Override
  public void submit(T task) {
    try {
      taskConsumer.accept(task);
    } catch (Throwable t) {
      throw new ExecutionException(t);
    }
  }
}
