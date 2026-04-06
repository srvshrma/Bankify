package com.bank;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BankingFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldExecuteCustomerAccountTransferDashboardAndShowcaseFlow() throws Exception {
        String email = "flow-" + UUID.randomUUID().toString().substring(0, 8) + "@bank.com";

        long customerId = readJson(postJson("/api/customers",
                "{\"fullName\":\"Riya Sen\",\"email\":\"" + email + "\"}"))
                .path("data").path("id").asLong();

        JsonNode accountResponse = readJson(postJson("/api/accounts",
                "{\"customerId\":" + customerId + ",\"accountType\":\"SAVINGS\",\"openingBalance\":500.00}"));
        String targetAccountNumber = accountResponse.path("data").path("accountNumber").asText();

        mockMvc.perform(get("/api/accounts/{accountNumber}", targetAccountNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.balance").value(500.0))
                .andExpect(jsonPath("$.data.recentTransactions.length()").value(1));

        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sourceAccountNumber\":\"BNK240101000001\","
                                + "\"targetAccountNumber\":\"" + targetAccountNumber + "\","
                                + "\"amount\":250.00,"
                                + "\"description\":\"Welcome transfer\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Transfer completed successfully"))
                .andExpect(jsonPath("$.data").value("Welcome transfer"));

        mockMvc.perform(get("/api/accounts/{accountNumber}", targetAccountNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.balance").value(750.0))
                .andExpect(jsonPath("$.data.recentTransactions.length()").value(2))
                .andExpect(jsonPath("$.data.recentTransactions[0].transactionType").value("TRANSFER_IN"));

        mockMvc.perform(get("/api/accounts/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalCustomers").value(3))
                .andExpect(jsonPath("$.data.totalAccounts").value(4))
                .andExpect(jsonPath("$.data.totalBalance").value(new BigDecimal("25200.00").doubleValue()))
                .andExpect(jsonPath("$.data.balanceByAccountType.SAVINGS").value(9000.0));

        mockMvc.perform(get("/api/showcase/java8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.optionalResults.firstRichAccount").exists())
                .andExpect(jsonPath("$.data.asyncInsights.length()").value(greaterThanOrEqualTo(1)));
    }

    @Test
    void shouldReturnValidationAndBusinessErrorsForInvalidRequests() throws Exception {
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\" \",\"email\":\"bad-email\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details.length()").value(greaterThanOrEqualTo(1)));

        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sourceAccountNumber\":\"BNK240101000001\","
                                + "\"targetAccountNumber\":\"BNK240101000001\","
                                + "\"amount\":10.00,"
                                + "\"description\":\"Self transfer\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Source and target accounts must be different"));

        mockMvc.perform(get("/api/accounts/{accountNumber}", "BNK-NOT-FOUND"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Account not found for number BNK-NOT-FOUND"));
    }

    private MvcResult postJson(String path, String json) throws Exception {
        return mockMvc.perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();
    }

    private JsonNode readJson(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}
