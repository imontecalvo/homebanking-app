package org.nacho.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nacho.backend.dtos.BalanceDTO;
import org.nacho.backend.models.Currency;
import org.nacho.backend.services.IBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BalanceController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class BalanceControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    IBalanceService balanceService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void testGetUserBalances() throws Exception {
        //Arrange
        List<BalanceDTO> expectedResult = List.of(BalanceDTO.builder()
                        .userId(1L)
                        .amount(BigDecimal.TEN)
                        .currency(Currency.USD)
                .build());
        when(balanceService.getUserBalances()).thenReturn(expectedResult);

        //Act & Assert
        mockMvc.perform(get("/api/balance"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResult)));
    }
}