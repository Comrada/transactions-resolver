package com.github.comrada.wa.repository;

import com.github.comrada.wa.entity.AlertDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertDetailRepository extends JpaRepository<AlertDetail, Long> {

}
