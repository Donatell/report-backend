package com.kvm.reportbackend.specify;

import com.kvm.reportbackend.dao.PatientListRepository;
import com.kvm.reportbackend.dao.PatientRepository;
import com.kvm.reportbackend.dao.TransneftPriceCategoryRepository;
import com.kvm.reportbackend.entity.Patient;
import com.kvm.reportbackend.entity.PatientList;
import com.kvm.reportbackend.entity.TransneftPriceCategory;
import com.kvm.reportbackend.upload_download.ReportWriter;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SpecifyDepartmentController {
	private final PatientRepository patientRepository;
	
	private final TransneftPriceCategoryRepository transneftPriceCategoryRepository;
	
	private final PatientListRepository patientListRepository;
	
	private final ReportWriter reportWriter;
	
	public SpecifyDepartmentController(PatientRepository patientRepository, TransneftPriceCategoryRepository transneftPriceCategoryRepository, PatientListRepository patientListRepository, ReportWriter reportWriter) {
		this.patientRepository = patientRepository;
		this.transneftPriceCategoryRepository = transneftPriceCategoryRepository;
		this.patientListRepository = patientListRepository;
		this.reportWriter = reportWriter;
	}
	
	@GetMapping("/specify-department/{patientListId}")
	public List<TransneftCategoryData> specifyDepartment(@PathVariable("patientListId") long patientListId) {
		List<Patient> patients = this.patientRepository.findAllByPatientListIdAndTransneftPriceCategoryIsNull(patientListId);
		
		return patients.stream()
				.map(patient -> new TransneftCategoryData(patient.getId(), patient.getFullName()))
				.collect(Collectors.toList());
	}
	
	@GetMapping("/transneft-price-categories")
	public List<TransneftPriceCategory> getTransneftPriceCategories() {
		return this.transneftPriceCategoryRepository.findAll();
	}
	
	@PostMapping("/specify-department/")
	public HashMap<String, String> specifyDepartment(@RequestBody List<TransneftCategoryData> categoryData) throws IOException {
		HashMap<String, String> response = new HashMap<>();
		List<Patient> patients = new ArrayList<>();
		for (TransneftCategoryData categoryDatum : categoryData) {
			Patient patient = patientRepository.findById(categoryDatum.getPatientId());
			patient.setTransneftPriceCategory(categoryDatum.getTransneftPriceCategory());
			patient.setTransneftBase(categoryDatum.getTransneftBase());
			patients.add(patient);
		}
		patientRepository.saveAll(patients);
		
		PatientList patientList = patients.get(0).getPatientList();
		patientList.setProcessStepId(4);
		patientListRepository.save(patientList);
		
		this.reportWriter.writeReports(patientList.getId(), patientList.getModuleId());
		
		response.put("message", "Данные обновлены");
		return response;
	}
}
