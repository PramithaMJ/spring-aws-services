package com.pmj.s3.controller;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.pmj.s3.model.Employer;
import com.pmj.s3.service.S3Service;
import com.pmj.s3.repository.EmployerRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/employers")
@Log4j2
public class EmployerController {

    private final S3Service s3Service;
    private final EmployerRepository employerRepository;

    public EmployerController(S3Service s3Service, EmployerRepository employerRepository) {
        this.s3Service = s3Service;
        this.employerRepository = employerRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<Employer> createEmployer(
            @RequestParam("file") MultipartFile file,
            @ModelAttribute Employer employer
    ) throws IOException {
        String imageUrl = s3Service.uploadFile(employer.getName(), file);
        employer.setImageUrl(imageUrl);
        Employer savedEmployer = employerRepository.save(employer);
        return new ResponseEntity<>(savedEmployer, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Employer>> getAllEmployers() {
        List<Employer> employers = employerRepository.findAll();
        return new ResponseEntity<>(employers, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/image", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<InputStreamResource> getEmployerImage(@PathVariable Long id) {
        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employer not found"));

        String imageUrl = employer.getImageUrl();
        log.info("Image URL to retrieve: " + imageUrl);

        // Extract the key from the imageUrl
        String keyName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        S3Object s3Object = s3Service.getFile(keyName);
        S3ObjectInputStream objectInputStream = s3Object.getObjectContent();
        InputStreamResource inputStreamResource = new InputStreamResource(objectInputStream);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + keyName + "\"")
                .body(inputStreamResource);
    }
}
