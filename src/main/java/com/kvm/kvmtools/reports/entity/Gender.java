package com.kvm.kvmtools.reports.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "gender")
@Data
public class Gender {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@Column(name = "title")
	private String title;
}
