package com.kvm.kvmtools.reports.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "service")
@Data
public class Service {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@Column(name = "title")
	private String title;
}
