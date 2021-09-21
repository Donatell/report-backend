package com.kvm.kvmtools.reports.dao;

import com.kvm.kvmtools.reports.entity.PatientList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientListRepository
		extends JpaRepository<PatientList, Long> {
}
