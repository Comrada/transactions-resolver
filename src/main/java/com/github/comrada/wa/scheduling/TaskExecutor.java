package com.github.comrada.wa.scheduling;

public interface TaskExecutor<T> {

  void submit(T task);
}
