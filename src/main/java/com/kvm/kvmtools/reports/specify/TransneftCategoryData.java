package com.kvm.kvmtools.reports.specify;

import com.kvm.kvmtools.reports.entity.TransneftBase;
import com.kvm.kvmtools.reports.entity.TransneftPriceCategory;
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
