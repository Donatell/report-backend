package com.kvm.reportbackend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.kvm.reportbackend.dao.ServiceRepository;
import com.kvm.reportbackend.specify.PriceData;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "patient_list")
@Data
public class PatientList {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "patient_list_id")
	private long id;
	
	@Column(name = "company_name")
	private String companyName;
	
	// Contains a string of semicolon-separated extra services
	// from the user input
	@Column(name = "extra_service")
	private String extraService;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true, mappedBy = "patientList")
	@JsonManagedReference
	private List<Patient> patients;
	
	@Column(name = "creation_date")
	private Date creationDate;
	
	@Column(name = "patient_quantity")
	private int patientQuantity;
	
	@Column(name = "process_step_id")
	private int processStepId;
	
	/**
	 * serviceId:price:quantity;serviceId:price:quantity;...
	 */
	@Column(name = "prices")
	private String prices;
	
	public PatientList(String companyName) {
		this.companyName = companyName;
		this.extraService = "";
		this.prices = "";
	}
	
	public PatientList() {
	
	}
	
	/**
	 * @return {@code List} of {@code PriceData} without service titles created by splitting the prices string
	 */
	public List<PriceData> getPricesAsList() {
		if (this.prices == null || this.prices.isBlank() || this.prices.isEmpty()) {
			return new ArrayList<>();
		} else {
			return Arrays.stream(prices.split(";"))
					.map(s -> s.split(":"))
					.map(strings -> new PriceData(strings[0], strings[1], strings[2]))
					.collect(Collectors.toList());
		}
	}
	
	
	/**
	 * Replaces the {@code prices} field of the {@code Patient}
	 */
	public void setPricesAsList(List<PriceData> priceDataList) {
		this.prices = priceDataList.stream().map(PriceData::toDBFormat).collect(Collectors.joining(";"));
	}
	
	/**
	 * @param serviceRepository used to fetch service titles
	 * @return {@code List} of {@code PriceData} with service titles created by splitting the {@code prices} string
	 */
	public List<PriceData> getPricesAsListWithTitles(ServiceRepository serviceRepository) {
		if (this.prices == null || this.prices.isBlank() || this.prices.isEmpty()) {
			return new ArrayList<>();
		} else {
			return Arrays.stream(prices.split(";"))
					.map(s -> s.split(":"))
					.map(strings -> new PriceData(strings[0], strings[1], strings[2], serviceRepository))
					.collect(Collectors.toList());
		}
	}
}
