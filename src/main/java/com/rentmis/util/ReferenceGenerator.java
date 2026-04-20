package com.rentmis.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class ReferenceGenerator {

    private static final AtomicInteger counter = new AtomicInteger(1000);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static String generatePaymentRef() {
        return "PAY-" + LocalDateTime.now().format(FMT) + "-" + counter.incrementAndGet();
    }

    public static String generateInvoiceNumber() {
        return "INV-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM")) +
               "-" + String.format("%06d", counter.incrementAndGet());
    }

    public static String generateContractNumber() {
        return "CON-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM")) +
               "-" + String.format("%05d", counter.incrementAndGet());
    }
}
