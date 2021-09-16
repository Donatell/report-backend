package com.kvm.reportbackend.parse;

import com.kvm.reportbackend.dao.*;
import com.kvm.reportbackend.entity.*;
import com.kvm.reportbackend.specify.PriceData;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class PatientListParser {
	private final List<String> headerKeywords = new ArrayList<>(Arrays.asList("фио", "проф", "профессия", "номер", "дата рождения", "н", "год"));
	
	private GenderRepository genderRepository;
	
	private FactorRepository factorRepository;
	
	private PatientRepository patientRepository;
	
	private PatientListRepository patientListRepository;
	
	private ServiceRepository serviceRepository;
	
	@Autowired
	public PatientListParser(GenderRepository genderRepository, FactorRepository factorRepository, PatientRepository patientRepository, PatientListRepository patientListRepository, ServiceRepository serviceRepository) {
		this.genderRepository = genderRepository;
		this.factorRepository = factorRepository;
		this.patientRepository = patientRepository;
		this.patientListRepository = patientListRepository;
		this.serviceRepository = serviceRepository;
	}
	
	public PatientListParser() {
	
	}
	
	// returns boolean: whether the list has patients, whose gender could not
	// be identified automatically
	public void parse(List<Patient> patients, PatientList patientList)
			throws IOException, ParseException {
		
		boolean hasUnidentifiedGender = false;
		
		// get all factors
		List<Factor> factors = factorRepository.findAll();
		
		// get all services and prepare a price data list
		List<Service> services = serviceRepository.findAll();
		List<PriceData> priceDataList = services.stream()
				.map(service -> new PriceData(service.getId()))
				.collect(Collectors.toList());
		
		Gender female = genderRepository.getById(1);
		Gender male = genderRepository.getById(2);
		Gender unknown = genderRepository.getById(3);
		
		LocalDate fortyYearsAgo = LocalDate.now().minusYears(40);
		
		// these patients will be saved to db
		List<Patient> uniquePatients = new ArrayList<>();
		
		for (Patient patient : patients) {
			// skip empty entries
			if (patient.getFullName() == null &&
					patient.getDepartment() == null &&
					patient.getBirthDate() == null &&
					patient.getProfession() == null &&
					patient.getFactorCodes() == null) {
				continue;
			}
			
			// trim profession and department
			if (patient.getProfession() == null) {
				patient.setProfession("");
			}
			if (patient.getDepartment() == null) {
				patient.setDepartment("");
			}
			patient.setDepartment(patient.getDepartment().trim());
			patient.setProfession(patient.getProfession().trim());
			
			// convert factor codes to factor ids
			if (patient.getFactorCodes() == null) {
				patient.setFactorCodes("");
			}
			
			// skip headers
			if (headerKeywords.contains(patient.getDepartment().trim()) || headerKeywords.contains(patient.getFullName()
					.trim()) ||
					headerKeywords.contains(patient.getFactorCodes()
							.trim()) || headerKeywords.contains(patient.getProfession().trim())) {
				continue;
			}
			
			// accept patients with some data, but without a birth date
			if ((patient.getFullName() != null || patient.getDepartment() != null
					|| patient.getFactorCodes() != null || patient
					.getProfession() != null) && patient
					.getBirthDate() == null) {
				
				// birth dates of people with blank fullName should not be same, otherwise they will be sorted out later
				patient.setBirthDate(LocalDate.parse("1990-01-01").plusDays(uniquePatients.size()));
			}
			
			// accept patients without full name, but with a birth date
			if (patient.getFullName() == null && patient.getBirthDate() != null) {
				patient.setFullName("");
			}
			
			// skip repeated patients
			if (uniquePatients.stream().anyMatch(listPatient -> (patient.getFullName()
					.equals(listPatient.getFullName())) && (patient.getBirthDate()
					.isEqual(listPatient.getBirthDate())))) {
				continue;
			}
			
			// a set to record patients unique service ids with common services inserted at creation
			HashSet<Integer> serviceIdsSet = new HashSet<>(Arrays.asList(10, 11, 14, 16, 19, 25, 32, 37, 42, 49, 57, 75, 77, 82));
			// insert services for 40+ year old patients
			if (patient.getBirthDate().isBefore(fortyYearsAgo)) {
				serviceIdsSet.addAll(Arrays.asList(20, 46));
				
				// insert services for 40+ year old women
				if (patient.getFullName().endsWith("а")) {
					serviceIdsSet.add(40);
				}
			}
			
			// determine gender and add gender-specific services for women
			if (patient.getFullName().endsWith("ч") || patient.getFullName().equals("")) {
				patient.setGender(male);
			} else if (patient.getFullName().endsWith("а")) {
				patient.setGender(female);
				serviceIdsSet.addAll(Arrays.asList(7, 72, 80, 81));
			} else {
				patient.setGender(unknown);
				hasUnidentifiedGender = true;
			}
			
			List<Integer> factorIdList = new ArrayList<>();
			for (String factorCode : patient.getFactorCodes().split(";")) {
				if (!factorCode.endsWith(".")) {
					factorCode += ".";
				}
				for (Factor factor : factors) {
					if (factor.getFactorCode().equals(factorCode)) {
						factorIdList.add(factor.getId());
					}
				}
			}
			patient.setFactorIdAsList(factorIdList);
			patient.setFactorCodes(factorIdList.stream()
					.map(integer -> factors.stream()
							.filter(factor -> factor.getId() == integer)
							.findFirst()
							.get()
							.getFactorCode())
					.collect(Collectors.joining(";")));
			
			// insert factor-specific services, factors ids are stored as ;-separated values in a string
			for (int factorId : patient.getFactorIdAsList()) {
				for (Factor factor : factors) {
					if (factor.getId() == factorId) {
						serviceIdsSet.addAll(factor.getServiceIdAsList());
					}
				}
			}
			
			// sort and save patient's unique services
			patient.setServiceIdAsList(serviceIdsSet.stream().sorted().collect(Collectors.toList()));
			
			// increment quantity of the patients services in price data list
			for (int serviceId : patient.getServiceIdAsList()) {
				for (PriceData priceData : priceDataList) {
					if (priceData.getServiceId() == serviceId) {
						priceData.incrementQuantity();
					}
				}
			}
			
			patient.setPatientListId(patientList.getId());
			
			// store the patient as unique
			uniquePatients.add(patient);
		}
		
		patientRepository.saveAll(uniquePatients);
		
		// filter out entries with 0 quantity and if there are patients with unidentified gender retain women services
		// even if the quantity is zero. The price data list should be checked for zero quantity once gender is specified
		// in SpecifyGenderController
		boolean finalHasUnidentifiedGender = hasUnidentifiedGender;
		priceDataList = priceDataList.stream()
				.filter(priceData ->
						priceData.getQuantity() != 0 ||
								((finalHasUnidentifiedGender) && (priceData.getServiceId() == 7 ||
										priceData.getServiceId() == 72 ||
										priceData.getServiceId() == 80 ||
										priceData.getServiceId() == 81 ||
										priceData.getServiceId() == 40)))
				.collect(Collectors.toList());
		
		patientList.setPricesAsList(priceDataList);
		
		// require specifying gender if there are patients with unidentified gender
		patientList.setProcessStepId(hasUnidentifiedGender ? 2 : 3);
		patientList.setPatientQuantity(patientRepository.countAllByPatientListId(patientList.getId()));
		patientListRepository.save(patientList);
	}
	
}
