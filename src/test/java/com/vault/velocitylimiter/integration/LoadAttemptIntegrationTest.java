package com.vault.velocitylimiter.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vault.velocitylimiter.model.LoadAttempt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class LoadAttemptIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testProcessFile_GeneratesExpectedOutput() throws Exception {
        // Read the input file
        byte[] inputFileBytes = Files.readAllBytes(Paths.get("src/test/resources/input.txt"));

        // Create a mock MultipartFile with the input file content
        MockMultipartFile inputFile = new MockMultipartFile("file", "input_file.txt", "text/plain", inputFileBytes);

        // Perform the request
        MvcResult mvcResult = mockMvc.perform(multipart("/load_attempts/process-file")
                        .file(inputFile))
                .andExpect(status().isOk())
                .andReturn();

        // Get the generated output file name from the response
        String responseBody = mvcResult.getResponse().getContentAsString();
        String generatedFileName = responseBody.substring(StringUtils.ordinalIndexOf(responseBody, "output_", 2), responseBody.indexOf(".txt") + 4);

        // Read the generated output file
        Path folderPath = Paths.get("output_folder");
        Path generatedOutputPath = folderPath.resolve(generatedFileName);
        List<String> generatedOutputLines = Files.readAllLines(generatedOutputPath);

        // Read the expected output file
        List<String> expectedOutputLines = Files.readAllLines(Paths.get("src/test/resources/output.txt"));

        // Compare the contents of the generated and expected output files
        assertEquals(expectedOutputLines.size(), generatedOutputLines.size(), "Number of lines in the output files do not match.");

        for (int i = 0; i < expectedOutputLines.size(); i++) {
            if (expectedOutputLines.get(i).isBlank()){
                continue;
            }
            LoadAttempt expectedLoadAttempt = objectMapper.readValue(expectedOutputLines.get(i), LoadAttempt.class);
            LoadAttempt generatedLoadAttempt = objectMapper.readValue(generatedOutputLines.get(i), LoadAttempt.class);
            assertEquals(expectedLoadAttempt, generatedLoadAttempt, "LoadAttempt objects at line " + (i + 1) + " do not match.");
        }

        Files.delete(generatedOutputPath);
    }
}
