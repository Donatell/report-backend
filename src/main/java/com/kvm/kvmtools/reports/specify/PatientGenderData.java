package com.kvm.kvmtools.reports.specify;

import lombok.Data;

@Data
public class PatientGenderData {
	long patientId;
	
	String fullName;
	
	int genderId;
}
