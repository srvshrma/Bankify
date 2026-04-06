package com.bank;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class BankingApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldLoadCustomers() throws Exception {
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(greaterThanOrEqualTo(2)));
    }

    @Test
    void shouldCreateCustomer() throws Exception {
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"Riya Sen\",\"email\":\"riya@bank.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.email").value("riya@bank.com"));
    }

    @Test
    void shouldGenerateJava8Showcase() throws Exception {
        mockMvc.perform(get("/api/showcase/java8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.optionalResults.firstRichAccount").exists())
                .andExpect(jsonPath("$.data.asyncInsights.length()").value(greaterThanOrEqualTo(1)));
    }
}
