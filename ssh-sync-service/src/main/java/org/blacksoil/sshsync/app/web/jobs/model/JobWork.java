package org.blacksoil.sshsync.app.web.jobs.model;

@FunctionalInterface
public interface JobWork {
  void run(ProgressSink sink) throws Exception;

  interface ProgressSink {
    void update(int progress, String message);

    boolean isCanceled();
  }
}
