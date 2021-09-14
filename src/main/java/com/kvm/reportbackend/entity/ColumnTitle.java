package com.kvm.reportbackend.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "column_title")
@Data
public class ColumnTitle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "title")
	private String title;

	@Column(name = "patient_column")
	private String patientColumn;
}
