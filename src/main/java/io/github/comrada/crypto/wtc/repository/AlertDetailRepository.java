package io.github.comrada.crypto.wtc.repository;

import io.github.comrada.crypto.wtc.model.AlertDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertDetailRepository extends JpaRepository<AlertDetail, Long> {

}
