package com.user.api.demo.service;

import com.user.api.demo.model.FileEntity;
import com.user.api.demo.repo.FileEntityRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
public class FileService {

    @Value("${save-dir}")
    private String saveDir;

    @Autowired
    FileEntityRepository repo;

    public List<FileEntity> findAll() {
        return repo.findAll();
    }

    public boolean checkFileExists(Long id) {
        return repo.existsById(id);
    }

    public boolean deleteFileById(Long id) {
        repo.deleteById(id);
        return true;
    }

    public Optional<FileEntity> findById(Long id) {
        return repo.findById(id);
    }

    public List<FileEntity> findByUsername(String username) {
        return repo.findByUsername(username);
    }

    public Optional<FileEntity> findByName(String name) {
        return repo.findByName(name);
    }

    public Optional<FileEntity> findByNameAndUsername(String name,String username) {
        return repo.findByNameAndUsername(name,username);
    }



    public boolean upload(MultipartFile item, String username) throws IOException {
        String path = saveDir+username+"//";
        File directory = new File(path);

        if (!directory.exists()) {
            boolean result = directory.mkdirs();
            if (!result) {
                return false;
            }
        }

        String fileName = item.getOriginalFilename();
        String type = item.getContentType();
        long fileSize = item.getSize();

        // Set the buffer size to 8 KB (or any other suitable size)
        int bufferSize = 8 * 1024;
        byte[] buffer = new byte[bufferSize];

        // Create a BufferedInputStream to read the file in blocks
        try (BufferedInputStream bis = new BufferedInputStream(item.getInputStream())) {
            // Create a temporary file to write the blocks to
            File tempFile = File.createTempFile("upload-", ".tmp");
            tempFile.deleteOnExit();

            // Create a BufferedOutputStream to write the blocks to the temporary file
            try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tempFile))) {
                int bytesRead;
                while ((bytesRead = bis.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                }
                bos.flush();
            }

            // Move the temporary file to the final destination
            Path tempFilePath = tempFile.toPath();
            Path destinationPath = Paths.get(path,fileName);
            if (Files.exists(destinationPath)) {
                int count = 1;
                String baseName = FilenameUtils.getBaseName(fileName);
                String extension = FilenameUtils.getExtension(fileName);
                while (Files.exists(destinationPath)) {
                    fileName = baseName + " (" + count + ")" + "." + extension;
                    destinationPath = Paths.get(path, fileName);
                    count++;
                }
            }
            Files.move(tempFilePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);

            // Create a FileEntity object with the file data and save it to the database
            FileEntity file = new FileEntity(fileName, destinationPath.toString(), fileSize, type, username);
            repo.save(file);
        }
        return true;
    }
}
