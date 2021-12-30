package com.github.comrada.wa.repository;

import com.github.comrada.wa.entity.WhaleAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WhaleAlertRepository extends JpaRepository<WhaleAlert, Long> {

}
