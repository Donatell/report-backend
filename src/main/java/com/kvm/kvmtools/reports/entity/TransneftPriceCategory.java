package com.kvm.kvmtools.reports.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kvm.kvmtools.reports.specify.PriceData;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data
@Table(name = "transneft_price_category")
public class TransneftPriceCategory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne()
	@JoinColumn(name = "transneft_base_id")
	private TransneftBase transneftBase;
	
	@Column(name = "keywords")
	private String keywords;
	
	/**
	 * service_id:price;...
	 */
	@Column(name = "prices")
	private String prices;
	
	@Column(name = "description")
	private String description;
	
	/**
	 * @return {@code List} of {@code PriceData} without service titles created by splitting the prices string
	 */
	@JsonIgnore
	public List<PriceData> getPricesAsList() {
		if (this.prices == null || this.prices.isBlank() || this.prices.isEmpty()) {
			return new ArrayList<>();
		} else {
			return Arrays.stream(prices.split(";"))
					.map(s -> s.split(":"))
					.map(strings -> new PriceData(strings[0], strings[1]))
					.collect(Collectors.toList());
		}
	}
	
	/**
	 * Replaces the {@code prices} field of the {@code TransneftPriceCategory}
	 */
	public void setPricesAsList(List<PriceData> priceDataList) {
		this.prices = priceDataList.stream().map(PriceData::toDBFormatWithoutQuantity).collect(Collectors.joining(";"));
	}
}
