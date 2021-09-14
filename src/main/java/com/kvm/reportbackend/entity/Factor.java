package com.kvm.reportbackend.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "factor")
@Data
public class Factor {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@Column(name = "factor_code")
	private String factorCode;
	
	// Contains a string of semicolon-separated ids of services
	@Column(name = "service_id")
	private String serviceId;
	
	public List<Integer> getServiceIdAsList() {
		return Arrays.stream(serviceId.split(";")).map(Integer::parseInt).collect(Collectors.toList());
	}
	
	public void setServiceIdAsList(List<Integer> serviceIdList) {
		this.serviceId = serviceIdList.stream().map(String::valueOf).collect(Collectors.joining(";"));
	}
}
