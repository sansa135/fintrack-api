package com.sanskruti.fintrack;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ExpenseApiIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    private String register(String user) throws Exception {
        String body = "{\"username\":\"" + user + "\",\"password\":\"secret123\"}";
        String res = mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return json.readTree(res).get("token").asText();
    }

    private static final String EXPENSE =
            "{\"amount\":250.50,\"category\":\"Food\",\"description\":\"Lunch\",\"date\":\"2026-10-05\"}";

    @Test
    void requestsWithoutTokenAreRejected() throws Exception {
        mvc.perform(get("/api/expenses")).andExpect(status().isUnauthorized());
    }

    @Test
    void fullCrudAndSummaryFlow() throws Exception {
        String token = register("alice");

        String created = mvc.perform(post("/api/expenses").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(EXPENSE))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        JsonNode node = json.readTree(created);
        long id = node.get("id").asLong();

        mvc.perform(get("/api/expenses/" + id).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.category").value("Food"));

        mvc.perform(get("/api/expenses/summary?month=2026-10").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.Food").value(250.50));

        mvc.perform(delete("/api/expenses/" + id).header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/expenses/" + id).header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void usersCannotSeeEachOthersExpenses() throws Exception {
        String bob = register("bob");
        String carol = register("carol");
        String created = mvc.perform(post("/api/expenses").header("Authorization", "Bearer " + bob)
                        .contentType(MediaType.APPLICATION_JSON).content(EXPENSE))
                .andReturn().getResponse().getContentAsString();
        long id = json.readTree(created).get("id").asLong();

        mvc.perform(get("/api/expenses/" + id).header("Authorization", "Bearer " + carol))
                .andExpect(status().isNotFound());
    }

    @Test
    void invalidPayloadReturns400() throws Exception {
        String token = register("dave");
        mvc.perform(post("/api/expenses").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":-5,\"category\":\"\",\"date\":\"2026-10-05\"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors.amount").exists());
    }
}
