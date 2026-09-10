package com.telehealth.screening.Repositories;


import java.util.List;
import java.util.Optional;
import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;

import com.telehealth.screening.entity.Screening;
import com.telehealth.screening.enums.ScreeningStatus;

public interface ScreeningRepository extends JpaRepository<Screening, Long>{
	Optional<Screening> findByIdAndStatus(Long id, ScreeningStatus status);
	List<Screening> findByNurseIdOrderByCreatedAtDesc(Long nurseId);
	Optional<Screening> findByImageHash(String imageHash);
	boolean existsByPatientIdAndNurseIdAndStatusIn(
	        Long patientId, 
	        Long nurseId, 
	        Collection<ScreeningStatus> statuses
	        );
}