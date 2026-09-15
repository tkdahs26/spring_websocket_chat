package com.example.chat_server;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@Component
public class DbSaveMetrics {

    private final List<Double> saveTimes =
            Collections.synchronizedList(new ArrayList<>());

    private static final int TEST_MESSAGE_COUNT = 2000;

    public void record(double timeMs) {

        synchronized (saveTimes) {

            saveTimes.add(timeMs);

            if (saveTimes.size() == TEST_MESSAGE_COUNT) {
                printResult();
                saveTimes.clear();
            }
        }
    }

    private void printResult() {

        List<Double> sorted = new ArrayList<>(saveTimes);

        Collections.sort(sorted);

        int count = sorted.size();

        double avg = sorted.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);

        double min = sorted.get(0);

        double med;

        if (count % 2 == 0) {
            med = (sorted.get(count / 2 - 1)
                    + sorted.get(count / 2)) / 2.0;
        } else {
            med = sorted.get(count / 2);
        }

        double max = sorted.get(count - 1);

        double p90 =
                sorted.get((int) Math.ceil(count * 0.90) - 1);

        double p95 =
                sorted.get((int) Math.ceil(count * 0.95) - 1);

        System.out.println();
        System.out.println("===== DB SAVE RESULTS =====");

        System.out.printf(
                "db_save_count................: %d%n",
                count
        );

        System.out.printf(
                "db_save_duration.............: avg=%.2fms min=%.2fms med=%.2fms max=%.2fms p(90)=%.2fms p(95)=%.2fms%n",
                avg, min, med, max, p90, p95
        );

        System.out.println();
    }
}