package mtn.momo.contract.repayment.service;

import mtn.momo.contract.repayment.exception.AnnualRateException;
import mtn.momo.contract.repayment.model.dto.RepaymentOption;
import mtn.momo.contract.repayment.model.request.RepaymentRequest;
import mtn.momo.contract.repayment.service.impl.RepaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RepaymentServiceImplTest {

    @Mock
    private ICacheService cacheService;

    @InjectMocks
    private RepaymentServiceImpl repaymentService;

    @BeforeEach
    void setup() {
        // Set up the default terms for testing
        when(cacheService.getConfigValue("repayment.terms")).thenReturn("12,24,36");
    }

    private void setRepaymentRate(Double rate) {
        try {
            Field field = RepaymentServiceImpl.class.getDeclaredField("repaymentRate");
            field.setAccessible(true);
            field.set(repaymentService, rate);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void calculateRepaymentOptions_Success() throws AnnualRateException {
        // Set the repayment rate using reflection
        setRepaymentRate(6.5);

        RepaymentRequest request = new RepaymentRequest();
        request.setAmount(10000);

        List<RepaymentOption> options = repaymentService.calculateRepaymentOptions(request);

        assertNotNull(options);
        assertEquals(3, options.size());

        // Assert each term's repayment is correctly calculated
        assertAll("Repayment options",
                () -> assertEquals(0, new BigDecimal("862.96").compareTo(options.get(0).getMonthlyPayment())),
                () -> assertEquals(0, new BigDecimal("445.46").compareTo(options.get(1).getMonthlyPayment())),
                () -> assertEquals(0, new BigDecimal("306.49").compareTo(options.get(2).getMonthlyPayment()))
        );
    }

    @Test
    void calculateRepaymentOptions_NoRatesAvailable() {
        // Set the repayment rate to null using reflection to simulate the absence of rates
        setRepaymentRate(null);

        RepaymentRequest request = new RepaymentRequest();
        request.setAmount(10000);  // $10,000 loan amount

        AnnualRateException exception = assertThrows(AnnualRateException.class,
                () -> repaymentService.calculateRepaymentOptions(request));

        assertEquals("Annual rate is not available.", exception.getMessage());
    }
}
