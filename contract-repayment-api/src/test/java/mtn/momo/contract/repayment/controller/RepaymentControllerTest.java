package mtn.momo.contract.repayment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mtn.momo.contract.repayment.model.dto.RepaymentOption;
import mtn.momo.contract.repayment.model.request.RepaymentRequest;
import mtn.momo.contract.repayment.service.IRepaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RepaymentControllerTest {

    @Mock
    private IRepaymentService IRepaymentService;

    @InjectMocks
    private RepaymentController repaymentController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(repaymentController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testCalculateRepaymentOptions_Success() throws Exception {
        RepaymentRequest request = new RepaymentRequest();
        List<RepaymentOption> options = new ArrayList<>();
        options.add(new RepaymentOption(12, new BigDecimal("87.5")));
        options.add(new RepaymentOption(24, new BigDecimal("45.0")));
        options.add(new RepaymentOption(36, new BigDecimal("31.25")));

        when(IRepaymentService.calculateRepaymentOptions(any())).thenReturn(options);

        mockMvc.perform(post("/api/repayments/calculate/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].term").value(12))
                .andExpect(jsonPath("$[0].monthlyPayment").value(87.5))
                .andExpect(jsonPath("$[1].term").value(24))
                .andExpect(jsonPath("$[1].monthlyPayment").value(45.0))
                .andExpect(jsonPath("$[2].term").value(36))
                .andExpect(jsonPath("$[2].monthlyPayment").value(31.25));
        verify(IRepaymentService).calculateRepaymentOptions(any());
    }

    @Test
    public void testCalculateRepaymentOptions_EmptyResponse() throws Exception {
        RepaymentRequest request = new RepaymentRequest();

        when(IRepaymentService.calculateRepaymentOptions(any())).thenReturn(new ArrayList<>());

        mockMvc.perform(post("/api/repayments/calculate/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty()); // This will assert that the array is empty
        verify(IRepaymentService).calculateRepaymentOptions(any());
    }
}
