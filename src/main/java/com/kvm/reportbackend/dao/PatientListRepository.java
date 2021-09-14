package com.kvm.reportbackend.dao;

import com.kvm.reportbackend.entity.PatientList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientListRepository
		extends JpaRepository<PatientList, Long> {
	
}
