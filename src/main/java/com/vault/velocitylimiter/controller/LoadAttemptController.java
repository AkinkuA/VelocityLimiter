package com.vault.velocitylimiter.controller;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vault.velocitylimiter.exception.LoadAttemptException;
import com.vault.velocitylimiter.model.LoadAttempt;
import com.vault.velocitylimiter.service.LoadAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/load_attempts")
public class LoadAttemptController {

    @Autowired
    private LoadAttemptService loadAttemptService;

    private final Logger logger = LoggerFactory.getLogger(LoadAttemptController.class);

    @PostMapping
    public ResponseEntity<Map<String, Object>> processLoadAttempt(@RequestBody LoadAttempt loadAttempt) {
        Map<String, Object> response = new HashMap<>();
        String id = String.valueOf(loadAttempt.getId());

        String error = null;

        if (Objects.equals(id, "null")){
            error = "Id is missing in the load attempt.";
        } else if (loadAttempt.getCustomer() == null) {
            error = "Customer id is missing in the load attempt.";
        } else if (loadAttempt.getLoadAmount() == null){
            error = "Load amount is missing in the load attempt.";
        } else if (loadAttempt.getTime() == null){
            error = "Load time is missing in the load attempt.";
        }

        if (error != null){
            logger.error(error);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        String customerId = String.valueOf(loadAttempt.getCustomer().getCustomerId());
        response.put("id", id);
        response.put("customer_id", customerId);
        try {
            loadAttempt.setAccepted(false);
            loadAttemptService.processLoadAttempt(loadAttempt);
            response.put("accepted", true);
            logger.info("Load attempt id {} for customer id {} has succeeded", id, customerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (DataIntegrityViolationException de) {
            logger.error("Load attempt already processed for this customer.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (LoadAttemptException ex) {
            logger.error("Load attempt id {} error: {}", id, ex.getMessage());
            response.put("accepted", false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping(value = "/process-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> processFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Read the input file content
            String inputContent = new String(file.getBytes(), StandardCharsets.UTF_8);

            // Split the input file by lines
            String[] inputLines = inputContent.split("\\r?\\n");

            // Process each line and generate the output
            StringBuilder outputContent = new StringBuilder();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            for (String inputLine : inputLines) {
                if (!inputLine.trim().isEmpty()) {
                    // Deserialize the input line to a LoadAttempt object
                    LoadAttempt loadAttempt = objectMapper.readValue(inputLine, LoadAttempt.class);

                    // Process the load attempt and get the result
                    ResponseEntity<Map<String, Object>> responseFromLoadAttempt = processLoadAttempt(loadAttempt);

                    // Serialize the response object and append it to the output content
                    if (responseFromLoadAttempt.getBody() != null) {
                        outputContent.append(objectMapper.writeValueAsString(responseFromLoadAttempt.getBody()))
                                .append("\n\n");
                    }
                }
            }

            Path folderPath = Paths.get("output_folder");
            if (!Files.exists(folderPath)) {
                Files.createDirectory(folderPath);
            }

            // Generate a unique file name using the current timestamp
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String timestamp = LocalDateTime.now().format(formatter);
            String uniqueFileName = "output_" + timestamp + ".txt";

            // Save the output content to a file with a unique file name in the folder
            Path outputPath = folderPath.resolve(uniqueFileName);
            Files.writeString(outputPath, outputContent.toString());

            response.put("message", "File uploaded check 'output_folder' for result. File name is " + uniqueFileName);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            response.put("message", "Error uploading file, check logs for details");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
