package com.insurance.maintenance.repository;

import com.insurance.maintenance.domain.LapseHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LapseHistoryRepository extends JpaRepository<LapseHistory, Long> {
}
