package com.kvm.reportbackend.specify;

import lombok.Data;

@Data
public class PatientGenderData {
	long patientId;
	String fullName;
	int genderId;
}
