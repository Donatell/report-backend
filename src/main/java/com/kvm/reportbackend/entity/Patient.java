package com.kvm.reportbackend.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "patient")
@Data
public class Patient {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "patient_id")
	private long id;
	
	@Column(name = "full_name")
	private String fullName;
	
	@Column(name = "birth_date")
	private LocalDate birthDate;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "gender_id", nullable = false)
	private Gender gender;
	
	@Column(name = "factor_codes")
	private String factorCodes;
	
	@Column(name = "factor_id")
	private String factorId;
	
	@Column(name = "profession")
	private String profession;
	
	@Column(name = "department")
	private String department;
	
	@ManyToOne()
	@JoinColumn(name = "patient_list_id")
	private PatientList patientList;
	
	@Column(name = "service_id")
	private String serviceId;
	
	public List<Integer> getServiceIdAsList() {
		return Arrays.stream(serviceId.split(";")).map(Integer::parseInt).collect(Collectors.toList());
	}
	
	/**
	 * Sorts the list, joins with ";" and replaces the {@code serviceId} field
	 */
	public void setServiceIdAsList(List<Integer> serviceIdList) {
		this.serviceId = serviceIdList.stream().sorted().map(String::valueOf).collect(Collectors.joining(";"));
	}
	
	public List<Integer> getFactorIdAsList() {
		if (this.factorId == null || this.factorId.isEmpty() || this.factorId.isBlank()) {
			return new ArrayList<>();
		} else {
			return Arrays.stream(factorId.split(";")).map(Integer::parseInt).collect(Collectors.toList());
		}
	}
	
	public void setFactorIdAsList(List<Integer> factorIdList) {
		this.factorId = factorIdList.stream().map(String::valueOf).collect(Collectors.joining(";"));
	}
	
	public void setFullName(String fullName) {// format full name
		if (fullName.isEmpty() || fullName.isBlank()) {
			this.fullName = "";
		} else {
			this.fullName = Arrays.stream(fullName.trim()
					.split(" "))
					.map(word -> word.substring(0, 1)
							.toUpperCase() +
							word.substring(1)
									.toLowerCase())
					.collect(Collectors.joining(" "));
		}
	}
}
