package com.kvm.reportbackend.upload_download;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.kvm.reportbackend.dao.PatientListRepository;
import com.kvm.reportbackend.dao.PatientRepository;
import com.kvm.reportbackend.entity.Patient;
import com.kvm.reportbackend.entity.PatientList;
import com.kvm.reportbackend.parse.PatientListParser;
import com.kvm.reportbackend.upload_download.storage.StorageFileNotFoundException;
import com.kvm.reportbackend.upload_download.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api")
public class FileUploadController {
	
	private final PatientListRepository patientListRepository;
	
	private final PatientListParser patientListParser;
	
	@Autowired
	public FileUploadController(StorageService storageService,
	                            PatientListRepository patientListRepository,
	                            PatientRepository patientRepository,
	                            PatientListParser patientListParser) {
		this.patientListRepository = patientListRepository;
		this.patientListParser = patientListParser;
		
	}
	
	@PostMapping("/files")
	public HashMap<String, String> handleFileUpload(@RequestParam("companyName") String companyName,
	                                                @RequestParam("patientList") String patientListJSONString) {
		HashMap<String, String> response = new HashMap<>();
		
		try {
			List<Patient> patients =
					new ObjectMapper().registerModule(new JSR310Module())
							.readValue(
									patientListJSONString,
									new TypeReference<>() {
									});
			
			System.out.println(patients);
			
			// save company name to db
			PatientList patientList = new PatientList(companyName);
			patientList = patientListRepository.save(patientList);
			
			// parse the file
			patientListParser.parse(patients, patientList);
			
			response.put("message", "Файл успешно загружен");
		} catch (IOException | ParseException e) {
			response.put("message", e.getMessage());
			e.printStackTrace();
		}
		
		return response;
	}
	
	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(
			StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}
}
