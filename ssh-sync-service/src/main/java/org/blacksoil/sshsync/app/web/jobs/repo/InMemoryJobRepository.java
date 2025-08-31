package org.blacksoil.sshsync.app.web.jobs.repo;

import org.blacksoil.sshsync.app.web.jobs.model.Job;
import org.blacksoil.sshsync.app.web.jobs.model.JobState;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryJobRepository implements JobRepository {

    private final Map<String, Job> jobs = new ConcurrentHashMap<>();

    @Override public Job create() {
        var id = UUID.randomUUID().toString();
        var job = new Job(id, Instant.now());
        jobs.put(id, job);
        return job;
    }

    @Override public Job get(String id) {
        var j = jobs.get(id);
        if (j == null) throw new NoSuchElementException("Job not found: " + id);
        return j;
    }

    @Override public void setState(String id, JobState state) { get(id).setState(state); }

    @Override public void setProgress(String id, int progress, String message) { get(id).setProgress(progress, message); }

    @Override public boolean cancel(String id) { return get(id).cancel(); }

    @Override public Collection<Job> findAll() { return jobs.values(); }

    @Override public int deleteExpired(long ttlMs) {
        var now = Instant.now();
        int[] cnt = {0};
        jobs.values().removeIf(j -> {
            boolean done = j.state() == JobState.COMPLETED || j.state() == JobState.FAILED || j.state() == JobState.CANCELED;
            boolean expired = done && j.updatedAt().isBefore(now.minusMillis(ttlMs));
            if (expired) cnt[0]++;
            return expired;
        });
        return cnt[0];
    }
}
