package com.kvm.reportbackend.upload_download;

import lombok.Data;

import java.util.Arrays;

@Data
public class SelectedColumns {
	public SelectedColumn[] columns;
	
	@Override
	public String toString() {
		return "SelectedColumns{" +
				"columns=" + Arrays.toString(columns) +
				'}';
	}
}
