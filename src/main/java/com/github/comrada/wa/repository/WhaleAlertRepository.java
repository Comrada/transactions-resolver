package com.github.comrada.wa.repository;

import com.github.comrada.wa.entity.WhaleAlert;
import com.github.comrada.wa.entity.WhaleAlert.ProcessingStatus;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface WhaleAlertRepository extends JpaRepository<WhaleAlert, Long> {

  @Query("select wa from WhaleAlert wa where wa.processStatus = :status order by wa.id")
  List<WhaleAlert> findByStatus(ProcessingStatus status, Pageable pageable);

  @Transactional
  @Modifying(flushAutomatically = true)
  @Query("""
          update WhaleAlert wa set wa.processedAt = :processedAt,
          wa.processAttempts = wa.processAttempts + 1 where wa.id = :id
      """)
  void incrementAttempt(long id, Instant processedAt);

  @Transactional
  @Modifying(flushAutomatically = true)
  @Query("""
          update WhaleAlert wa set wa.processedAt = :processedAt, wa.processStatus = :status,
          wa.processAttempts = wa.processAttempts + 1 where wa.id = :id
      """)
  void setStatus(long id, ProcessingStatus status, Instant processedAt);

  default List<WhaleAlert> findNewAlerts(int limit) {
    return findByStatus(ProcessingStatus.NEW, PageRequest.of(0, limit));
  }

  default List<WhaleAlert> findFailedAlerts(int limit) {
    return findByStatus(ProcessingStatus.FAILED, PageRequest.of(0, limit));
  }

  default List<WhaleAlert> findProcessedAlerts(int limit) {
    return findByStatus(ProcessingStatus.DONE, PageRequest.of(0, limit));
  }

  default void done(long id, Instant processedAt) {
    setStatus(id, ProcessingStatus.DONE, processedAt);
  }

  default void fail(long id, Instant processedAt) {
    setStatus(id, ProcessingStatus.FAILED, processedAt);
  }
}
