package com.user.api.demo.controller;

import com.user.api.demo.model.FileEntity;
import com.user.api.demo.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class FileUploadDownloadController {

    @Autowired
    FileService fileService;
    @Value("${save-dir}")
    private String saveDir;


    @GetMapping("/files/{username}/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String username, @PathVariable String fileName) {
        try {
            //Parte differente per ogni endpoint
            Optional<FileEntity> fileOptional = fileService.findByNameAndUsername(fileName, username);
            FileEntity fileEntity = fileOptional.get();
            // Creazione della risorsa
            FileSystemResource resource = new FileSystemResource(fileEntity.getPath());

            // Impostazione dell'intestazione per il download
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileEntity.getName());

            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        } catch (Exception e) {
            // Gestisci qui l'eccezione generica
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/file/{username}")
    public List<FileEntity> getAllFiles(@PathVariable("username") String username) {
        return fileService.findByUsername(username);
    }
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file, @RequestParam("username") String username) throws Exception {
        if(file == null || file.isEmpty()) {
            throw new BadRequestException("No file selected");
        }
        System.out.println(file.getOriginalFilename());
        System.out.println(username);
        Optional<FileEntity> fileOptional = fileService.findByNameAndUsername(file.getOriginalFilename(), username);

        if(!fileService.upload(file,username)){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create directory");
        }

        List<FileEntity> response = fileService.findByUsername(username);
        return new ResponseEntity<List<FileEntity>>(response, HttpStatus.OK);
    }

    @DeleteMapping(path = "/files/{id}")
    public ResponseEntity<String> deleteFile(@PathVariable("id") Long id) {
        boolean isFileExists = fileService.checkFileExists(id);

        if (!isFileExists) {
            return new ResponseEntity<String>("File " + id + " not found.", HttpStatus.NOT_FOUND);
        } else {
            Optional<FileEntity> fileOptional = fileService.findById(id);
            FileEntity file = fileOptional.get();
            String path = file.getPath();
            File fileDelete = new File(path);
            if (fileDelete.delete()) {
                System.out.println("File cancellato con successo.");
            } else {
                System.out.println("Cancellazione del file fallita.");
            }
            fileService.deleteFileById(id);
        }
        return new ResponseEntity<String>("File " + id + " deleted successfully", HttpStatus.OK);
   }

    public static class BadRequestException extends RuntimeException {
        public BadRequestException(String detail) {
            super(detail);
        }
    }

}
