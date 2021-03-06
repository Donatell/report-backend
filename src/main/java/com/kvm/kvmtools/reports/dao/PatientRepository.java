package com.kvm.kvmtools.reports.dao;

import com.kvm.kvmtools.reports.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {
	
	List<Patient> findAllByGender_IdAndPatientListId(int genderId, long patientListId);
	
	int countAllByPatientListId(long patientListId);
	
	int countAllByGender_IdAndPatientListId(int gender_id, long patientListId);
	
	Patient findById(long patientId);
	
	List<Patient> findAllByPatientListId(long patientListId);
	
	void deleteAllByPatientListId(long patientListId);
	
	List<Patient> findAllByPatientListIdAndTransneftPriceCategoryIsNull(long patientListId);
}
