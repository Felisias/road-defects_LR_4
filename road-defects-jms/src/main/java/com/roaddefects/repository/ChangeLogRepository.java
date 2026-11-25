package com.roaddefects.repository;

import com.roaddefects.entity.ChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {

    List<ChangeLog> findTop100ByOrderByIdDesc();
}
