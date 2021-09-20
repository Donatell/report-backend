package com.kvm.reportbackend.specify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kvm.reportbackend.dao.GenderRepository;
import com.kvm.reportbackend.dao.PatientListRepository;
import com.kvm.reportbackend.dao.PatientRepository;
import com.kvm.reportbackend.entity.Gender;
import com.kvm.reportbackend.entity.Patient;
import com.kvm.reportbackend.entity.PatientList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SpecifyGenderController {
	private final PatientRepository patientRepository;
	
	private final GenderRepository genderRepository;
	
	private final PatientListRepository patientListRepository;
	
	@Autowired
	public SpecifyGenderController(PatientRepository patientRepository, GenderRepository genderRepository, PatientListRepository patientListRepository) {
		this.patientRepository = patientRepository;
		this.genderRepository = genderRepository;
		this.patientListRepository = patientListRepository;
	}
	
	@GetMapping("/specify-gender/{id}")
	public List<Patient> getPatientsWithUnidentifiedGender(@PathVariable("id") long patientListId) {
		return patientRepository.findAllByGender_IdAndPatientListId(3, patientListId);
	}
	
	@PostMapping("/specify-gender")
	public HashMap<String, String> setPatientsWithUnidentifiedGender(@RequestParam("patientData") String patientGenderDataString, @RequestParam("patientListId") long patientListId) {
		HashMap<String, String> response = new HashMap<>();
		Gender female = genderRepository.getById(1);
		Gender male = genderRepository.getById(2);
		List<PatientGenderData> patientGenderDataList;
		List<Patient> patients = new ArrayList<>();
		
		PatientList patientList = patientListRepository.getById(patientListId);
		try {
			// convert json to patient data array
			patientGenderDataList = new ObjectMapper().readValue(
					patientGenderDataString,
					new TypeReference<>() {
					});
			
			// fetch price data in case quantity changes
			List<PriceData> priceDataList = patientListRepository.getById(patientListId).getPricesAsList();
			
			// for each data entry fetch patient by id, update and add to array
			for (PatientGenderData patientGenderDatum :
					patientGenderDataList) {
				Patient patient = patientRepository.findById(patientGenderDatum.patientId);
				patient.setGender(patientGenderDatum.genderId == 1 ? female : male);
				patient.setFullName(patientGenderDatum.fullName);
				
				// increment service quantity for women services and add to services list if current patient is female
				if (patient.getGender().getId() == 1) {
					priceDataList.stream()
							.filter(priceData -> priceData.getServiceId() == 7 ||
									priceData.getServiceId() == 72 ||
									priceData.getServiceId() == 80 ||
									priceData.getServiceId() == 81)
							.forEach(PriceData::incrementQuantity);
					
					List<Integer> patientServiceIdList = patient.getServiceIdAsList();
					patientServiceIdList.addAll(Arrays.asList(7, 72, 80, 81));
					if (patient.getBirthDate().isBefore(LocalDate.now().minusYears(40))) {
						priceDataList.stream()
								.filter(priceData -> priceData.getServiceId() == 40)
								.findFirst()
								.get()
								.incrementQuantity();
						patientServiceIdList.add(40);
					}
					patient.setServiceIdAsList(patientServiceIdList);
				}
				
				patients.add(patient);
			}
			patientRepository.saveAll(patients);
			
			// filter out services with zero quantity
			priceDataList = priceDataList.stream()
					.filter(priceData -> priceData.getQuantity() != 0)
					.collect(Collectors.toList());
			
			patientList.setPricesAsList(priceDataList);
			
			// Transneft module doesn't require specifying prices, skip this step if it's transneft
			if (patientList.getModuleId() == 2) {
				if (patientRepository.findAllByPatientListIdAndTransneftPriceCategoryIsNull(patientList.getId())
						.size() > 0) {
					patientList.setProcessStepId(5);
				} else {
					patientList.setProcessStepId(4);
				}
			} else {
				patientList.setProcessStepId(3);
			}
			patientListRepository.save(patientList);
			
			response.put("message", "Данные обновлены");
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			response.put("message", e.getMessage());
		}
		
		return response;
	}
}
