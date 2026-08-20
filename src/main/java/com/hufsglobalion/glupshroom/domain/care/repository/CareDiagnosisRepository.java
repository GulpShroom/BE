package com.hufsglobalion.glupshroom.domain.care.repository;

import com.hufsglobalion.glupshroom.domain.care.entity.CareDiagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareDiagnosisRepository extends JpaRepository<CareDiagnosis, Long> {
}
