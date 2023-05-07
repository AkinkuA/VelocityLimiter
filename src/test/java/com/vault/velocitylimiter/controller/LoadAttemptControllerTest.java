package com.vault.velocitylimiter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vault.velocitylimiter.model.Customer;
import com.vault.velocitylimiter.model.LoadAttempt;
import com.vault.velocitylimiter.service.LoadAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(LoadAttemptController.class)
public class LoadAttemptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoadAttemptService loadAttemptService;

    private LoadAttempt loadAttempt;

    @BeforeEach
    public void setUp() {
        Customer customer = new Customer(1, 0f, 0, 0f);
        loadAttempt = new LoadAttempt(1, customer, 2000f, LocalDateTime.now(), false);
    }

    @Test
    public void testProcessLoadAttempt() throws Exception {
        when(loadAttemptService.processLoadAttempt(any(LoadAttempt.class))).thenReturn(loadAttempt);

        mockMvc.perform(post("/load_attempts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loadAttempt)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value("1"))
                .andExpect(jsonPath("customer_id").value("1"))
                .andExpect(jsonPath("accepted").value(true));

        verify(loadAttemptService, times(1)).processLoadAttempt(any(LoadAttempt.class));
    }

    @Test
    public void testProcessFile() throws Exception {
        when(loadAttemptService.processLoadAttempt(any(LoadAttempt.class))).thenReturn(loadAttempt);

        String loadAttemptJson = objectMapper.writeValueAsString(loadAttempt);
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", loadAttemptJson.getBytes());

        mockMvc.perform(multipart("/load_attempts/process-file").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").exists());

        verify(loadAttemptService, times(1)).processLoadAttempt(any(LoadAttempt.class));
    }
}
