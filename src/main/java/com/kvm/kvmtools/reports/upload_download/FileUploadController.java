package com.kvm.kvmtools.reports.upload_download;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.kvm.kvmtools.reports.dao.PatientListRepository;
import com.kvm.kvmtools.reports.entity.Patient;
import com.kvm.kvmtools.reports.entity.PatientList;
import com.kvm.kvmtools.reports.parse.PatientListParser;
import com.kvm.kvmtools.reports.upload_download.storage.StorageFileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api")
public class FileUploadController {
	
	private final PatientListRepository patientListRepository;
	
	private final PatientListParser patientListParser;
	
	@Autowired
	public FileUploadController(PatientListRepository patientListRepository,
	                            PatientListParser patientListParser) {
		this.patientListRepository = patientListRepository;
		this.patientListParser = patientListParser;
	}
	
	@PostMapping("/files")
	public HashMap<String, String> handleFileUpload(@RequestParam("companyName") String companyName,
	                                                @RequestParam("patientList") String patientListJSONString,
	                                                @RequestParam("moduleId") int moduleId) {
		HashMap<String, String> response = new HashMap<>();
		
		try {
			List<Patient> patients = new ObjectMapper().registerModule(new JSR310Module())
					.readValue(patientListJSONString, new TypeReference<>() {
					});
			
			// save company name to db
			PatientList patientList = new PatientList(companyName);
			patientList.setModuleId(moduleId);
			patientList = patientListRepository.save(patientList);
			
			// parse the file
			patientListParser.parse(patients, patientList, moduleId);
			
			response.put("message", "Файл успешно загружен");
		} catch (IOException e) {
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
