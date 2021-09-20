package com.kvm.reportbackend.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "module")
public class Module {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "title")
	private String title;
	
	@Column(name = "description")
	private String description;
}
