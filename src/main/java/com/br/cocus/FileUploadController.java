package com.br.cocus;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Controller
@RequestMapping("/api/files")
public class FileUploadController {

    private final FileUploadService fileUploadService;
    private final Path fileUploadLocation;

    @Autowired
    public FileUploadController (FileUploadProperties fileUploadProperties, FileUploadService fileUploadService){
        this.fileUploadLocation = Paths.get(fileUploadProperties.getUploadDir()).toAbsolutePath().normalize();
        this.fileUploadService = fileUploadService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file){
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            Path targetLocation = fileUploadLocation.resolve(fileName);
            file.transferTo(targetLocation);

            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/files/download/").path(fileName).toUriString();

            return ResponseEntity.ok("Upload completed! Download link: " + fileDownloadUri);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) throws IOException {
        Path filePath = fileUploadLocation.resolve(fileName).normalize();

        try {
            Resource resource = new UrlResource(filePath.toUri());

            String contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());

            if (contentType == null){
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<String>> listFiles() throws IOException {
        return ResponseEntity.ok(fileUploadService.getFileNames());
    }

    @GetMapping("/oneRandomBackwards")
    public ResponseEntity<String> oneRandomBackwards() throws IOException {
        return ResponseEntity.ok(fileUploadService.oneRandomBackwards());
    }

    @GetMapping("/longest100lines")
    public ResponseEntity<List<String>> longest100lines() throws IOException {
        return ResponseEntity.ok(fileUploadService.longest100lines());
    }

    @GetMapping("/longest20LinesOneFile")
    public ResponseEntity<List<String>> longest20LinesOneFile() throws IOException {
        return ResponseEntity.ok(fileUploadService.longest20LinesOneFile());
    }

    @GetMapping(path = "/oneRandomLine", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_PLAIN_VALUE, MediaType.ALL_VALUE})
    public ResponseEntity<String> oneRandomLine(@RequestHeader(value = "Accept") String acceptHeader) throws IOException {

        FileInformation fileInformation = fileUploadService.oneRandomLine();

        // Checking the Accept header and returning the appropriate response
        if (acceptHeader.contains(MediaType.APPLICATION_JSON_VALUE) || acceptHeader.contains(MediaType.APPLICATION_XML_VALUE) || acceptHeader.contains(MediaType.TEXT_PLAIN_VALUE)) {
            return ResponseEntity.ok().body(fileInformation.getContent());
        }  else if (acceptHeader.equals("application/*")){
            return ResponseEntity.ok().body(fileInformation.toString());
        }

        return ResponseEntity.badRequest().body("Invalid Header");
    }
}