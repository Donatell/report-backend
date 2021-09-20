package com.kvm.reportbackend.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "transneft_base")
public class TransneftBase {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@Column(name = "title")
	private String title;
}
