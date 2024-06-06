package com.pmj.s3.controller;

import com.pmj.s3.model.Employer;
import com.pmj.s3.service.S3Service;
import com.pmj.s3.repository.EmployerRepository;
import org.springframework.core.io.Resource;
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
public class EmployerController {

    private final S3Service s3Service;
    private final EmployerRepository employerRepository;

    public EmployerController(S3Service s3Service, EmployerRepository employerRepository) {
        this.s3Service = s3Service;
        this.employerRepository = employerRepository;
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Employer> createEmployer(
            @RequestParam("file") MultipartFile file,
            @ModelAttribute Employer employer
    ) throws IOException {
        String imageUrl = s3Service.uploadFile(employer.getName(), file);
        employer.setImageUrl(imageUrl);
        Employer savedEmployer = employerRepository.save(employer);
        return new ResponseEntity<>(savedEmployer, HttpStatus.CREATED);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<List<Employer>> getAllEmployers() {
        List<Employer> employers = employerRepository.findAll();
        return new ResponseEntity<>(employers, HttpStatus.OK);
    }

    @GetMapping("/{id}/photoUrl")
    public ResponseEntity<String> getEmployerPhotoUrl(@PathVariable Long id) {
        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employer not found"));

        String imageUrl = employer.getImageUrl();

        return new ResponseEntity<>(imageUrl, HttpStatus.OK);
    }
}