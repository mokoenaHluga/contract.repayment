package mtn.momo.contract.repayment.service.cache;

import mtn.momo.contract.repayment.model.dto.InterestRateDto;
import mtn.momo.contract.repayment.service.repository.ConfigurationSettingRepository;
import mtn.momo.contract.repayment.service.repository.InterestRateRepository;
import mtn.momo.contract.repayment.service.repository.entity.ConfigurationSetting;
import mtn.momo.contract.repayment.service.repository.entity.InterestRate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class CacheServiceTest {

    @Mock
    private InterestRateRepository interestRateRepository;

    @Mock
    private ConfigurationSettingRepository configurationSettingRepository;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private CacheService cacheService;

    @BeforeEach
    void setUp() {
        // Mock data for testing
        InterestRate interestRate = new InterestRate(UUID.randomUUID(), 6.5, LocalDateTime.now(), LocalDateTime.now());
        when(interestRateRepository.findAllOrderByCreatedDateDesc()).thenReturn(List.of(interestRate));

        ConfigurationSetting setting = new ConfigurationSetting();
        setting.setKeyName("repayment.terms");
        setting.setValue("12,24,36");
        when(configurationSettingRepository.findAll()).thenReturn(List.of(setting));

        cacheService.init();  // Initialize the cache with mocked data
    }

    @Test
    void testInitMethod() {
        List<InterestRateDto> cachedInterestRates = cacheService.getInterestRates();
        assertFalse(cachedInterestRates.isEmpty());
        assertEquals(6.5, cachedInterestRates.get(0).getInterestRate());
    }

    @Test
    void testGetInterestRates() {
        List<InterestRateDto> rates = cacheService.getInterestRates();
        assertNotNull(rates);
        assertFalse(rates.isEmpty());
        assertEquals(6.5, rates.get(0).getInterestRate());
    }

    @Test
    void testGetInterestRatesWhenEmpty() {
        when(interestRateRepository.findAllOrderByCreatedDateDesc()).thenReturn(Collections.emptyList());

        List<InterestRateDto> rates = cacheService.getInterestRates();

        assertNotNull(rates);
        assertTrue(rates.isEmpty());
    }

    @Test
    void testGetConfigValue() {
        String terms = cacheService.getConfigValue("repayment.terms");
        assertNotNull(terms);
        assertEquals("12,24,36", terms);
    }

    @Test
    void testGetConfigValueWhenNotFound() {
        when(configurationSettingRepository.findByKeyName("non.existent.key")).thenReturn(null);

        String value = cacheService.getConfigValue("non.existent.key");

        assertNull(value);
    }
}
