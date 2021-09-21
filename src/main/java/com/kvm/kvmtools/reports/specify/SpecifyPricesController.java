package com.kvm.kvmtools.reports.specify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kvm.kvmtools.reports.dao.FactorRepository;
import com.kvm.kvmtools.reports.dao.PatientListRepository;
import com.kvm.kvmtools.reports.dao.PatientRepository;
import com.kvm.kvmtools.reports.dao.ServiceRepository;
import com.kvm.kvmtools.reports.entity.Patient;
import com.kvm.kvmtools.reports.entity.PatientList;
import com.kvm.kvmtools.reports.upload_download.ReportWriter;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SpecifyPricesController {
	private final PatientListRepository patientListRepository;
	
	private final ServiceRepository serviceRepository;
	
	private final PatientRepository patientRepository;
	
	private final ReportWriter reportWriter;
	
	public SpecifyPricesController(PatientListRepository patientListRepository, FactorRepository factorRepository, ServiceRepository serviceRepository, PatientRepository patientRepository, ReportWriter reportWriter) {
		this.patientListRepository = patientListRepository;
		this.serviceRepository = serviceRepository;
		this.patientRepository = patientRepository;
		this.reportWriter = reportWriter;
	}
	
	@GetMapping("/specify-prices/{id}")
	public List<PriceData> getPricesTemplate(@PathVariable("id") long patientListId) {
		PatientList patientList = patientListRepository.getById(patientListId);
		return patientList.getPricesAsListWithTitles(serviceRepository);
	}
	
	@PostMapping("/specify-prices/{id}")
	public HashMap<String, String> specifyPrices(@PathVariable("id") long patientListId, @RequestBody String priceDataJSONString) throws JsonProcessingException {
		HashMap<String, String> response = new HashMap<>();
		List<PriceData> requestPriceDataList = new ObjectMapper().readValue(priceDataJSONString, new TypeReference<>() {
		});
		PatientList patientList = patientListRepository.getById(patientListId);
		
		// if any service has been deleted, identify deleted services and remove them from all patients
		List<PriceData> patientPriceDataList = patientList.getPricesAsList();
		if (requestPriceDataList.size() != patientPriceDataList.size()) {
			patientPriceDataList.removeAll(requestPriceDataList);
			List<Integer> removedServiceIdList = patientPriceDataList.stream()
					.map(PriceData::getServiceId)
					.collect(Collectors.toList());
			
			List<Patient> patients = patientRepository.findAllByPatientListId(patientListId);
			for (Patient patient : patients) {
				List<Integer> serviceIdList = patient.getServiceIdAsList();
				serviceIdList.removeAll(removedServiceIdList);
				patient.setServiceIdAsList(serviceIdList);
			}
			patientRepository.saveAll(patients);
		}
		
		patientList.setPricesAsList(requestPriceDataList);
		patientList.setProcessStepId(4);
		patientListRepository.save(patientList);
		response.put("message", "Цены отправлены успешно");
		
		try {
			reportWriter.writeReports(patientListId, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return response;
	}
}

