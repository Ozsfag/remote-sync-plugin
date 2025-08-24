package org.blacksoil.remotesync.ui.util;

import javax.swing.*;

/**
 * Swing‑дебаунсер: откладывает выполнение Runnable на delayMs и перезапускает таймер при каждом
 * новом submit(). По умолчанию исполняет задачу на EDT.
 */
public final class Debouncer {

  private final int delayMs;
  private final boolean runOnEdt;
  private Timer timer;
  private Runnable pending;

  public Debouncer(int delayMs) {
    this(delayMs, true);
  }

  /**
   * @param delayMs задержка перед выполнением
   * @param runOnEdt если true — задача исполняется на EDT
   */
  public Debouncer(int delayMs, boolean runOnEdt) {
    this.delayMs = delayMs;
    this.runOnEdt = runOnEdt;
  }

  /** Переустанавливает таймер и запоминает последнюю задачу. */
  public synchronized void submit(Runnable r) {
    pending = r;
    if (timer != null) timer.stop();
    timer = new Timer(delayMs, e -> fire());
    timer.setRepeats(false);
    timer.start();
  }

  /** Немедленно выполняет последнюю отложенную задачу (если есть). */
  public void flush() {
    if (runOnEdt) {
      SwingUtilities.invokeLater(this::fire);
    } else {
      fire();
    }
  }

  /** Отменяет запланированное выполнение. */
  public synchronized void cancel() {
    pending = null;
    if (timer != null) {
      timer.stop();
      timer = null;
    }
  }

  /** Есть ли задача, ожидающая выполнения. */
  public synchronized boolean hasPending() {
    return pending != null;
  }

  // ---- internal ----
  private void fire() {
    Runnable task;
    synchronized (this) {
      task = pending;
      pending = null;
      if (timer != null) {
        timer.stop();
        timer = null;
      }
    }
    if (task == null) return;

    if (runOnEdt) {
      if (SwingUtilities.isEventDispatchThread()) task.run();
      else SwingUtilities.invokeLater(task);
    } else {
      task.run();
    }
  }
}
