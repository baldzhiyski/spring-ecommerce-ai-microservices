package org.baldzhiyski.product.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.baldzhiyski.product.model.ProductReservation;
import org.baldzhiyski.product.repository.ProductReservationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationCleanupJob {

    private final ProductReservationRepository reservations;

    /** Keep CONFIRMED rows this many days before purging (default 180). */
    @Value("${reservations.retention.confirmed-days:180}")
    private int confirmedRetentionDays;

    /**
     * Runs weekly on Sunday at 03:15 (server time).
     * Cron: sec min hour day-of-month month day-of-week
     */
    @Scheduled(cron = "0 15 3 ? * SUN")
    @Transactional
    public void purgeOldConfirmedReservations() {
        OffsetDateTime cutoff = OffsetDateTime.now(ZoneOffset.UTC).minusDays(confirmedRetentionDays);

        long candidates = reservations.countConfirmedOlderThan(cutoff);
        int deleted = (candidates == 0) ? 0 : reservations.deleteConfirmedOlderThan(cutoff);

        log.info("ReservationCleanupJob: retention={} days, candidates={}, deleted={}",
                confirmedRetentionDays, candidates, deleted);
    }
}