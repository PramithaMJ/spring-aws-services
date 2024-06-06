package com.pmj.s3.controller;

import com.pmj.s3.model.Employer;
import com.pmj.s3.service.S3Service;
import com.pmj.s3.repository.EmployerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
}