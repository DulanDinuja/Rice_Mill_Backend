package com.ricemill;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ricemill.dto.PaddyStockDto;
import com.ricemill.entity.PaddyStock;
import com.ricemill.repository.PaddyStockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class PaddyStockIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaddyStockRepository paddyStockRepository;

    @BeforeEach
    void setUp() {
        paddyStockRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddPaddyStock_Success() throws Exception {
        PaddyStockDto.AddRequest request = PaddyStockDto.AddRequest.builder()
                .paddyType("Basmati")
                .quantity(new BigDecimal("1000.50"))
                .pricePerKg(new BigDecimal("50.00"))
                .supplier("ABC Farms")
                .mobileNumber("1234567890")
                .warehouse("Warehouse A")
                .moistureLevel(new BigDecimal("14.5"))
                .unit("kg")
                .status("Available")
                .build();

        mockMvc.perform(post("/api/v1/paddy-stock/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.paddyType").value("Basmati"))
                .andExpect(jsonPath("$.data.quantity").value(1000.50))
                .andExpect(jsonPath("$.data.pricePerKg").value(50.00))
                .andExpect(jsonPath("$.data.supplier").value("ABC Farms"))
                .andExpect(jsonPath("$.data.totalValue").value(50025.00))
                .andExpect(jsonPath("$.data.batchNumber").isNotEmpty());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddPaddyStock_WithCustomBatchNumber() throws Exception {
        PaddyStockDto.AddRequest request = PaddyStockDto.AddRequest.builder()
                .paddyType("Samba")
                .quantity(new BigDecimal("500.00"))
                .pricePerKg(new BigDecimal("40.00"))
                .supplier("XYZ Supplier")
                .mobileNumber("9876543210")
                .unit("kg")
                .build();

        mockMvc.perform(post("/api/v1/paddy-stock/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.batchNumber").value("CUSTOM-BATCH-001"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddPaddyStock_DuplicateBatchNumber() throws Exception {
        // First add
        PaddyStockDto.AddRequest request1 = PaddyStockDto.AddRequest.builder()
                .paddyType("Red Rice")
                .quantity(new BigDecimal("300.00"))
                .pricePerKg(new BigDecimal("60.00"))
                .supplier("Supplier 1")
                .mobileNumber("1111111111")
                .unit("kg")
                .build();

        mockMvc.perform(post("/api/v1/paddy-stock/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        // Try to add with same batch number
        PaddyStockDto.AddRequest request2 = PaddyStockDto.AddRequest.builder()
                .paddyType("White Rice")
                .quantity(new BigDecimal("400.00"))
                .pricePerKg(new BigDecimal("45.00"))
                .supplier("Supplier 2")
                .mobileNumber("2222222222")
                .unit("kg")
                .build();

        mockMvc.perform(post("/api/v1/paddy-stock/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("DUPLICATE_BATCH_NUMBER"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddPaddyStock_ValidationError() throws Exception {
        PaddyStockDto.AddRequest request = PaddyStockDto.AddRequest.builder()
                .paddyType("")  // Invalid: blank
                .quantity(new BigDecimal("-10"))  // Invalid: negative
                .pricePerKg(new BigDecimal("0"))  // Invalid: zero
                .supplier("Test Supplier")
                .unit("kg")
                .build();

        mockMvc.perform(post("/api/v1/paddy-stock/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllPaddyStocks() throws Exception {
        // Add some test data
        PaddyStock stock1 = PaddyStock.builder()
                .paddyType("Basmati")
                .quantity(new BigDecimal("1000"))
                .pricePerKg(new BigDecimal("50"))
                .supplier("Supplier A")
                .unit("kg")
                .totalValue(new BigDecimal("50000"))
                .build();
        paddyStockRepository.save(stock1);

        mockMvc.perform(get("/api/v1/paddy-stock")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetPaddyStockSummary() throws Exception {
        mockMvc.perform(get("/api/v1/paddy-stock/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalStocks").exists())
                .andExpect(jsonPath("$.data.totalQuantity").exists())
                .andExpect(jsonPath("$.data.totalValue").exists())
                .andExpect(jsonPath("$.data.unit").value("KG"));
    }

    @Test
    void testAddPaddyStock_Unauthorized() throws Exception {
        PaddyStockDto.AddRequest request = PaddyStockDto.AddRequest.builder()
                .paddyType("Basmati")
                .quantity(new BigDecimal("1000"))
                .pricePerKg(new BigDecimal("50"))
                .supplier("Test Supplier")
                .unit("kg")
                .build();

        mockMvc.perform(post("/api/v1/paddy-stock/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}

