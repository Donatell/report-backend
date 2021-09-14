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
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class FileUploadController {
	
	private final StorageService storageService;
	
	private final PatientListRepository patientListRepository;
	
	private final PatientRepository patientRepository;
	
	private final PatientListParser patientListParser;
	
	@Autowired
	public FileUploadController(StorageService storageService,
	                            PatientListRepository patientListRepository,
	                            PatientRepository patientRepository,
	                            PatientListParser patientListParser) {
		this.storageService = storageService;
		this.patientListRepository = patientListRepository;
		this.patientRepository = patientRepository;
		this.patientListParser = patientListParser;
		
	}
	
	@GetMapping("/files")
	public HashMap<String, String> listUploadedFiles(Model model) {
		
		model.addAttribute("files", storageService.loadAll().map(
				path -> MvcUriComponentsBuilder.fromMethodName(
						FileUploadController.class,
						"serveFile",
						path.getFileName().toString())
						.build()
						.toUri()
						.toString())
				.collect(Collectors.toList()));
		HashMap<String, String> map = new HashMap<>();
		map.put("Name", "John");
		return map;
	}
	
	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
		
		Resource file = storageService.loadAsResource(filename);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" +
						file.getFilename() +
						"\"").body(file);
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
