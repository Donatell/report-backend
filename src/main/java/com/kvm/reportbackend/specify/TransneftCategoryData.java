package com.kvm.reportbackend.specify;

import com.kvm.reportbackend.entity.TransneftBase;
import com.kvm.reportbackend.entity.TransneftPriceCategory;
import lombok.Data;

@Data
public class TransneftCategoryData {
	private long patientId;
	
	private String fullName;
	
	private TransneftBase transneftBase;
	
	private TransneftPriceCategory transneftPriceCategory;
	
	public TransneftCategoryData(long patientId, String fullName) {
		this.patientId = patientId;
		this.fullName = fullName;
	}
}
