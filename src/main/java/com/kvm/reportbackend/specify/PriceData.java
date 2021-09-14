package com.kvm.reportbackend.specify;

import com.kvm.reportbackend.dao.ServiceRepository;
import lombok.Data;

import java.util.Objects;

@Data
public class PriceData {
	private int serviceId;
	
	private String serviceTitle;
	
	private Double price;
	
	private int quantity;
	
	public PriceData(String serviceId) {
		this.serviceId = Integer.parseInt(serviceId);
		this.price = 0d;
		this.quantity = 0;
	}
	
	public PriceData() {
	}
	
	public PriceData(int serviceId) {
		this.serviceId = serviceId;
		this.price = 0d;
		this.quantity = 0;
	}
	
	public PriceData(String serviceId, String price, String quantity) {
		this.serviceId = Integer.parseInt(serviceId);
		this.price = Double.parseDouble(price);
		this.quantity = Integer.parseInt(quantity);
	}
	
	public PriceData(String serviceId, String price, String quantity, ServiceRepository serviceRepository) {
		this.serviceId = Integer.parseInt(serviceId);
		this.price = Double.parseDouble(price);
		this.quantity = Integer.parseInt(quantity);
		this.serviceTitle = serviceRepository.getById(this.serviceId).getTitle();
	}
	
	public String toDBFormat() {
		return String.format("%d:%s:%d", serviceId, price, quantity);
	}
	
	public void incrementQuantity() {
		this.quantity++;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(serviceId);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PriceData priceData = (PriceData) o;
		return serviceId == priceData.serviceId;
	}
}
