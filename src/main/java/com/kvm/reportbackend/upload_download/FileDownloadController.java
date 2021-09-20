package com.kvm.reportbackend.upload_download;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api")
public class FileDownloadController {
	
	public FileDownloadController() {
	}
	
	@GetMapping("/download/{patientListId}/{reportType}")
	public ResponseEntity<Resource> downloadReport(@PathVariable("patientListId") long patientListId, @PathVariable("reportType") int reportType) throws IOException {
		
		Path filePath = Paths.get(System.getProperty("user.dir") + "/reports/" + patientListId + "/" + reportType + ".xlsx");
		ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(filePath));
		
		return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(resource);
	}
}
