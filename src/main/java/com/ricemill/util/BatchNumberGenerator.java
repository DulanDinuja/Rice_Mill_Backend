package com.ricemill.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class BatchNumberGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final AtomicInteger paddyCounter = new AtomicInteger(1);
    private final AtomicInteger riceCounter = new AtomicInteger(1);
    private String lastPaddyDate = "";
    private String lastRiceDate = "";

    /**
     * Generate batch number for paddy stock
     * Format: PADDY-YYYYMMDD-XXXX
     */
    public synchronized String generatePaddyBatchNumber() {
        String currentDate = LocalDate.now().format(DATE_FORMATTER);

        // Reset counter if date changed
        if (!currentDate.equals(lastPaddyDate)) {
            paddyCounter.set(1);
            lastPaddyDate = currentDate;
        }

        int sequence = paddyCounter.getAndIncrement();
        return String.format("PADDY-%s-%04d", currentDate, sequence);
    }

    /**
     * Generate batch number for rice stock
     * Format: RICE-YYYYMMDD-XXXX
     */
    public synchronized String generateRiceBatchNumber() {
        String currentDate = LocalDate.now().format(DATE_FORMATTER);

        // Reset counter if date changed
        if (!currentDate.equals(lastRiceDate)) {
            riceCounter.set(1);
            lastRiceDate = currentDate;
        }

        int sequence = riceCounter.getAndIncrement();
        return String.format("RICE-%s-%04d", currentDate, sequence);
    }
}

