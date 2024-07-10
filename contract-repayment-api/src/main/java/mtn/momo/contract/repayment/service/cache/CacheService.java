package mtn.momo.contract.repayment.service.cache;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mtn.momo.contract.repayment.model.dto.InterestRateDto;
import mtn.momo.contract.repayment.model.dto.InterestRateMapper;
import mtn.momo.contract.repayment.service.ICacheService;
import mtn.momo.contract.repayment.service.repository.ConfigurationSettingRepository;
import mtn.momo.contract.repayment.service.repository.InterestRateRepository;
import mtn.momo.contract.repayment.service.repository.entity.ConfigurationSetting;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService implements ICacheService {
    private final InterestRateRepository interestRateRepository;
    private final ConfigurationSettingRepository configurationSettingRepository;

    @Getter
    private List<InterestRateDto> cachedInterestRates;
    private final Map<String, String> configSettingsCache = new HashMap<>();

    /**
     * Initializes the cache with interest rates and configuration settings at application startup.
     * This method is automatically invoked after service instantiation.
     */
    @PostConstruct
    public void init() {
        cachedInterestRates = fetchAndCacheInterestRates();
        cacheConfigSettings();
    }

    /**
     * Public method to access cached interest rates. Uses Spring's @Cacheable annotation to manage the caching.
     * The 'interestRatesCache' is the name of the cache where the data is stored, and 'interestRates' is the cache key.
     *
     * @return a list of InterestRateDto representing the cached interest rates.
     */
    @Override
    @Cacheable(value = "interestRatesCache", key = "'interestRates'")
    public List<InterestRateDto> getInterestRates() {
        return fetchAndCacheInterestRates();
    }

    /**
     * Helper method to retrieve interest rates from the repository, map them to DTOs, and cache them.
     *
     * @return a list of InterestRateDto mapped from the interest rates in the database.
     */
    private List<InterestRateDto> fetchAndCacheInterestRates() {
        return interestRateRepository.findAllOrderByCreatedDateDesc().stream()
                .map(InterestRateMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Caches all configuration settings by retrieving them from the repository and storing them in a local cache map.
     */
    private void cacheConfigSettings() {
        List<ConfigurationSetting> settings = configurationSettingRepository.findAll();
        settings.forEach(setting -> configSettingsCache.put(setting.getKeyName(), setting.getValue()));
    }

    /**
     * Retrieves a configuration value by its key, using the @Cacheable annotation to manage caching.
     * If the key is not found in the cache, it will attempt to fetch and cache it using fetchAndCacheConfigValue.
     *
     * @param keyName the key of the configuration setting to retrieve.
     * @return the value of the configuration setting, or null if not found.
     */
    @Override
    @Cacheable(value = "configSettingsCache", key = "#keyName")
    public String getConfigValue(String keyName) {
        return configSettingsCache.getOrDefault(keyName, fetchAndCacheConfigValue(keyName));
    }

    /**
     * Fetches a specific configuration setting from the database and caches it.
     *
     * @param keyName the key of the configuration setting to fetch.
     * @return the value of the configuration setting if found, otherwise null.
     */
    private String fetchAndCacheConfigValue(String keyName) {
        List<ConfigurationSetting> all = configurationSettingRepository.findAll();
        ConfigurationSetting setting = configurationSettingRepository.findByKeyName(keyName);
        if (setting != null) {
            configSettingsCache.put(keyName, setting.getValue());
            return setting.getValue();
        }
        return null;
    }
}
