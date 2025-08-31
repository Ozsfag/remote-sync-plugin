package org.blacksoil.sshsync.app.web.jobs.model;


import lombok.Getter;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public final class Job {
  private final String id;
  private final Instant createdAt;
  private volatile Instant updatedAt;

  private volatile JobState state;
  private final AtomicInteger progress = new AtomicInteger(0);
  private volatile String message = "";
  private final AtomicBoolean canceled = new AtomicBoolean(false);

  public Job(String id, Instant now) {
    this.id = id;
    this.createdAt = now;
    this.updatedAt = now;
    this.state = JobState.QUEUED;
  }

  public void setState(JobState state) {
    this.state = state;
    this.updatedAt = Instant.now();
  }

  public void setProgress(int p, String msg) {
    int v = Math.max(0, Math.min(100, p));
    this.progress.set(v);
    this.message = msg == null ? "" : msg;
    this.updatedAt = Instant.now();
  }

  public boolean cancel() {
    boolean r = this.canceled.compareAndSet(false, true);
    if (r) this.updatedAt = Instant.now();
    return r;
  }
}
