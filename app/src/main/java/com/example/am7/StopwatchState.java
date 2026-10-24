package com.example.am7;

import java.util.concurrent.Future;

public class StopwatchState {

    private volatile long startedAt;
    private volatile long elapsed;
    private volatile int generation;
    private volatile boolean running;
    private volatile Future<?> task;

    public long getStartedAt() {
        return startedAt;
    }

    public long getElapsed() {
        return elapsed;
    }

    public int getGeneration() {
        return generation;
    }

    public boolean isRunning() {
        return running;
    }

    public Future<?> getTask() {
        return task;
    }

    public void setStartedAt(long startedAt) {
        this.startedAt = startedAt;
    }

    public void setElapsed(long elapsed) {
        this.elapsed = elapsed;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void setTask(Future<?> task) {
        this.task = task;
    }
}
