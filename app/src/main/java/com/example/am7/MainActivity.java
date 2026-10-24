package com.example.am7;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final int STOPWATCH_COUNT = 3;
    private final StopwatchState[] stopwatches = new StopwatchState[STOPWATCH_COUNT];

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService executorService = Executors.newFixedThreadPool(STOPWATCH_COUNT);

    private final TextView[] displays = new TextView[STOPWATCH_COUNT];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for (int i = 0; i < STOPWATCH_COUNT; i++) {
            stopwatches[i] = new StopwatchState();
            int number = i + 1;

            displays[i] = findViewById(getResources().getIdentifier("time_" + number, "id", getPackageName()));
            Button start = findViewById(getResources().getIdentifier("start_" + number, "id", getPackageName()));
            Button stop = findViewById(getResources().getIdentifier("stop_" + number, "id", getPackageName()));
            Button clear = findViewById(getResources().getIdentifier("clear_" + number, "id", getPackageName()));

            int index = i;

            start.setOnClickListener(view -> startStopwatch(index));
            stop.setOnClickListener(view -> stopStopwatch(index));
            clear.setOnClickListener(view -> clearStopwatch(index));
        }

    }

    private void startStopwatch(int index) {
       StopwatchState stopwatch = stopwatches[index];
       if (stopwatch.isRunning()) {
           return;
       }
       stopwatch.setRunning(true);
       stopwatch.setStartedAt(SystemClock.elapsedRealtime() - stopwatch.getElapsed());
        stopwatch.setGeneration(stopwatch.getGeneration() + 1);

        int generation = stopwatch.getGeneration();
        stopwatch.setTask(executorService.submit(() -> runStopwatch(index, generation)));
    }

    private void runStopwatch(int index, int generation) {
        StopwatchState stopwatch = stopwatches[index];
        try {
            while (stopwatch.isRunning() && !Thread.currentThread().isInterrupted()) {
                long elapsed = SystemClock.elapsedRealtime() - stopwatch.getStartedAt();
                mainHandler.post(() -> {
                    if (stopwatch.isRunning() && stopwatch.getGeneration() == generation) {
                        displays[index].setText(formatTime(elapsed));
                    }
                });
                Thread.sleep(50);
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    private void stopStopwatch(int index) {
        StopwatchState stopwatch = stopwatches[index];
        if (!stopwatch.isRunning()) {
            return;
        }
        stopwatch.setElapsed(SystemClock.elapsedRealtime() - stopwatch.getStartedAt());
        stopwatch.setRunning(false);
        stopwatch.setGeneration(stopwatch.getGeneration() + 1);
        if (stopwatch.getTask() != null) {
            stopwatch.getTask().cancel(true);
        }
        displays[index].setText(formatTime(stopwatch.getElapsed()));
    }

    private void clearStopwatch(int index) {
        stopStopwatch(index);
        StopwatchState stopwatch = stopwatches[index];
        stopwatch.setElapsed(0);
        displays[index].setText(formatTime(0));
    }

    @Override
    protected void onDestroy() {
        for (int i = 0; i < STOPWATCH_COUNT; i++) {
            if (stopwatches[i] != null) {
                stopStopwatch(i);
            }
        }
        executorService.shutdownNow();
        super.onDestroy();
    }

    private String formatTime(long milliseconds) {
        long minutes = milliseconds / 60000;
        long seconds = (milliseconds / 1000) % 60;
        long hundredths = (milliseconds % 1000) / 10;
        return String.format(Locale.getDefault(), "%02d:%02d.%02d", minutes, seconds, hundredths);
    }
}
