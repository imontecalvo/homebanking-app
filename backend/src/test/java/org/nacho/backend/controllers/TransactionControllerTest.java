package org.nacho.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nacho.backend.dtos.SuccessIntegerResponseDTO;
import org.nacho.backend.dtos.SuccessStringResponseDTO;
import org.nacho.backend.dtos.TransactionDTO;
import org.nacho.backend.dtos.transactions.ExchangeDTO;
import org.nacho.backend.dtos.transactions.SimpleTransactionDTO;
import org.nacho.backend.dtos.transactions.TransferDTO;
import org.nacho.backend.models.Currency;
import org.nacho.backend.models.TransactionType;
import org.nacho.backend.services.impl.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void testNewDepositWhenSuccess() throws Exception {
        //Arrange
        SimpleTransactionDTO deposit = SimpleTransactionDTO.builder()
                .currency(Currency.USD)
                .amount(BigDecimal.valueOf(100.))
                .build();
        doNothing().when(transactionService).newDeposit(deposit);
        SuccessStringResponseDTO expectedResponse = new SuccessStringResponseDTO("Deposit successfully completed");

        //Act & Assert
        MockHttpServletRequestBuilder httpRequest = post("/api/transaction/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deposit));
        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    public void testNewDepositWhenAmountIsNegative() throws Exception {
        //Arrange
        SimpleTransactionDTO deposit = SimpleTransactionDTO.builder()
                .currency(Currency.USD)
                .amount(BigDecimal.valueOf(-100.))
                .build();

        //Act & Assert
        MockHttpServletRequestBuilder httpRequest = post("/api/transaction/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deposit));
        mockMvc.perform(httpRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testNewDepositWhenCurrencyIsInvalid() throws Exception {
        //Arrange
        String deposit = "{\n\"currency\":\"INVALID\",\n\"amount\":100\n}";

        //Act & Assert
        MockHttpServletRequestBuilder httpRequest = post("/api/transaction/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(deposit);
        mockMvc.perform(httpRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testNewDepositWhenFieldsAreMissing() throws Exception {
        //Arrange
        String depositWhenAmountMissing = "{\n\"currency\":\"USD\"\n}";
        String depositWhenCurrencyMissing = "{\n\"amount\":100\n}";

        //Act & Assert
        mockMvc.perform(post("/api/transaction/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(depositWhenAmountMissing))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/transaction/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(depositWhenCurrencyMissing))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testNewWithdrawWhenSuccess() throws Exception {
        //Arrange
        SimpleTransactionDTO withdraw = SimpleTransactionDTO.builder()
                .currency(Currency.USD)
                .amount(BigDecimal.valueOf(100.))
                .build();
        doNothing().when(transactionService).newWithdraw(withdraw);
        SuccessStringResponseDTO expectedResponse = new SuccessStringResponseDTO("Withdraw successfully completed");

        //Act & Assert
        MockHttpServletRequestBuilder httpRequest = post("/api/transaction/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(withdraw));
        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    public void testNewWithdrawWhenAmountIsNegative() throws Exception {
        //Arrange
        SimpleTransactionDTO withdraw = SimpleTransactionDTO.builder()
                .currency(Currency.USD)
                .amount(BigDecimal.valueOf(-100.))
                .build();

        //Act & Assert
        MockHttpServletRequestBuilder httpRequest = post("/api/transaction/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(withdraw));
        mockMvc.perform(httpRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testNewWithdrawWhenCurrencyIsInvalid() throws Exception {
        //Arrange
        String withdraw = "{\n\"currency\":\"INVALID\",\n\"amount\":100\n}";

        //Act & Assert
        MockHttpServletRequestBuilder httpRequest = post("/api/transaction/withdraw")
                .contentType(MediaType.APPLICATION_JSON)
                .content(withdraw);
        mockMvc.perform(httpRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testNewWithdrawWhenFieldsAreMissing() throws Exception {
        //Arrange
        String withdrawWhenAmountMissing = "{\n\"currency\":\"USD\"\n}";
        String withdrawWhenCurrencyMissing = "{\n\"amount\":100\n}";

        //Act & Assert
        mockMvc.perform(post("/api/transaction/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(withdrawWhenAmountMissing))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/transaction/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(withdrawWhenCurrencyMissing))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testNewTransferWhenSuccess() throws Exception {
        //Arrange
        TransferDTO transfer = TransferDTO.builder()
                .destinationUsername("test_dest_user")
                .amount(BigDecimal.valueOf(100))
                .currency(Currency.USD)
                .build();
        SuccessStringResponseDTO expectedResponse = new SuccessStringResponseDTO("Transfer successfully completed");

        //Act & Assert
        mockMvc.perform(post("/api/transaction/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transfer)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    public void testNewTransferWhenAmountIsNegative() throws Exception {
        //Arrange
        TransferDTO transfer = TransferDTO.builder()
                .destinationUsername("test_dest_user")
                .amount(BigDecimal.valueOf(-100))
                .currency(Currency.USD)
                .build();

        //Act & Assert
        mockMvc.perform(post("/api/transaction/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transfer)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testNewTransferWhenCurrencyIsInvalid() throws Exception {
        //Arrange
        String transfer = "{\n\"currency\":\"INVALID\",\n\"amount\":100,\n\"destinationUsername\":\"test_dest_user\"\n}";

        //Act & Assert
        mockMvc.perform(post("/api/transaction/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transfer))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testNewTransferWhenFieldsAreMissing() throws Exception {
        //Arrange
        String transferCurrencyMissing = "{\n\"amount\":100,\n\"destinationUsername\":\"test_dest_user\"\n}";
        String transferAmountMissing = "{\n\"currency\":\"USD\",\n\"destinationUsername\":\"test_dest_user\"\n}";
        String transferDestUserMissing = "{\n\"currency\":\"USD\",\n\"amount\":100\n}";

        //Act & Assert
        mockMvc.perform(post("/api/transaction/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transferCurrencyMissing))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/transaction/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transferAmountMissing))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/transaction/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transferDestUserMissing))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testNewExchangeWhenSuccess() throws Exception {
        //Arrange
        ExchangeDTO exchange = ExchangeDTO.builder()
                .originAmount(BigDecimal.valueOf(100))
                .originCurrency(Currency.USD)
                .destinationCurrency(Currency.ARS)
                .build();
        SuccessStringResponseDTO expectedResponse = new SuccessStringResponseDTO("Exchange successfully completed");

        //Act & Assert
        mockMvc.perform(post("/api/transaction/exchange")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exchange)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    public void testNewExchangeWhenAmountIsNegative() throws Exception {
        //Arrange
        ExchangeDTO exchange = ExchangeDTO.builder()
                .originAmount(BigDecimal.valueOf(-100))
                .originCurrency(Currency.USD)
                .destinationCurrency(Currency.ARS)
                .build();

        //Act & Assert
        mockMvc.perform(post("/api/transaction/exchange")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exchange)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testNewExchangeWhenCurrenciesAreInvalid() throws Exception {
        //Arrange
        String exchangeOriginCurrencyInvalid = "{\n\"originCurrency\":\"INVALID\",\n\"destinationCurrency\":\"ARS\",\n\"originAmount\":100\n}";
        String exchangeDestCurrencyInvalid = "{\n\"originCurrency\":\"USD\",\n\"destinationCurrency\":\"INVALID\",\n\"originAmount\":100\n}";

        //Act & Assert
        mockMvc.perform(post("/api/transaction/exchange")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(exchangeOriginCurrencyInvalid))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/transaction/exchange")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(exchangeDestCurrencyInvalid))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testNewExchangeWhenFieldsAreMissing() throws Exception {
        //Arrange
        String exchangeOriginCurrencyMissing = "{\n\"destinationCurrency\":\"ARS\",\n\"originAmount\":100\n}";
        String exchangeAmountMissing = "{\n\"originCurrency\":\"USD\",\n\"destinationCurrency\":\"ARS\"\n}";
        String exchangeDestCurrencyMissing = "{\n\"originCurrency\":\"USD\",\n\"originAmount\":100\n}";

        //Act & Assert
        mockMvc.perform(post("/api/transaction/exchange")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(exchangeOriginCurrencyMissing))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/transaction/exchange")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(exchangeAmountMissing))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/transaction/exchange")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(exchangeDestCurrencyMissing))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetTransactionHistoryWhenSuccess() throws Exception {
        //Arrange
        Integer page = 0;
        Integer items = 10;
        List<TransactionDTO> expectedResponse = List.of(TransactionDTO.builder()
                .username("test_user")
                .amount(BigDecimal.valueOf(100))
                .type(TransactionType.TRANSFER)
                .currency(Currency.USD)
                .build());

        when(transactionService.getTransactionHistory(page, items)).thenReturn(expectedResponse);

        //Act & Assert
        mockMvc.perform(get("/api/transaction/history")
                        .param("page", page.toString())
                        .param("items", items.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    public void testGetTransactionHistoryWhenParamsAreMissing() throws Exception {
        //Arrange
        int page = 0;
        int items = 10;

        //Act & Assert
        mockMvc.perform(get("/api/transaction/history")
                        .param("page", Integer.toString(page)))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/transaction/history")
                        .param("items", Integer.toString(items)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetNumberOfTransactionsWhenSuccess() throws Exception {
        //Arrange
        Long expectedResult = 15L;
        SuccessIntegerResponseDTO expectedResponse = new SuccessIntegerResponseDTO(expectedResult.doubleValue());
        when(transactionService.getNumberOfTransactions()).thenReturn(expectedResult);

        //Act & Assert
        mockMvc.perform(get("/api/transaction/history-size"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    public void testConvertCurrencyWhenSuccess() throws Exception {
        //Arrange
        Currency from = Currency.USD;
        Currency to = Currency.ARS;
        BigDecimal amount = BigDecimal.valueOf(10);

        BigDecimal expectedResult = BigDecimal.valueOf(10000);
        SuccessIntegerResponseDTO expectedResponse = new SuccessIntegerResponseDTO(expectedResult.doubleValue());
        when(transactionService.convertCurrency(from, to, amount)).thenReturn(expectedResult);

        //Act & Assert
        mockMvc.perform(get("/api/transaction/convert")
                        .param("from", from.toString())
                        .param("to", to.toString())
                        .param("amount", amount.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    public void testConvertCurrencyWhenInvalidCurrencies() throws Exception {
        //Arrange
        Currency from = Currency.USD;
        Currency to = Currency.ARS;
        BigDecimal amount = BigDecimal.valueOf(10);

        //Act & Assert
        mockMvc.perform(get("/api/transaction/convert")
                        .param("from", "INVALID")
                        .param("to", to.toString())
                        .param("amount", amount.toString()))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/transaction/convert")
                        .param("from", from.toString())
                        .param("to", "INVALID")
                        .param("amount", amount.toString()))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void testConvertCurrencyWhenFieldsAreMissing() throws Exception {
        //Arrange
        Currency from = Currency.USD;
        Currency to = Currency.ARS;
        BigDecimal amount = BigDecimal.valueOf(10);

        //Act & Assert
        mockMvc.perform(get("/api/transaction/convert")
                        .param("from", from.toString())
                        .param("to", to.toString()))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/transaction/convert")
                        .param("from", from.toString())
                        .param("amount", amount.toString()))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/transaction/convert")
                        .param("amount", amount.toString())
                        .param("to", to.toString()))
                .andExpect(status().isBadRequest());
    }
}
