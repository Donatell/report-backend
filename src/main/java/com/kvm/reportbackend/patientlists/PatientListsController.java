package com.kvm.reportbackend.patientlists;

import com.kvm.reportbackend.dao.PatientListRepository;
import com.kvm.reportbackend.dao.PatientRepository;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api")

public class PatientListsController {
	private final PatientListRepository patientListRepository;
	
	private final PatientRepository patientRepository;
	
	public PatientListsController(PatientListRepository patientListRepository, PatientRepository patientRepository) {
		this.patientListRepository = patientListRepository;
		this.patientRepository = patientRepository;
	}
	
	@DeleteMapping("/patientLists/{patientListId}")
	public void deletePatientList(@PathVariable("patientListId") long patientListId) throws IOException {
		patientListRepository.delete(patientListRepository.getById(patientListId));
		
		Path path = Path.of(System.getProperty("user.dir") + "/reports/" + patientListId);
		if (Files.exists(path)) {
			File dir = path.toFile();
			for (File file : dir.listFiles()) {
				file.delete();
			}
			Files.delete(path);
		}
	}
}
