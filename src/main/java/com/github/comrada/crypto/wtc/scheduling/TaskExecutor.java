package com.github.comrada.crypto.wtc.scheduling;

public interface TaskExecutor<T> {

  void submit(T task);
}
