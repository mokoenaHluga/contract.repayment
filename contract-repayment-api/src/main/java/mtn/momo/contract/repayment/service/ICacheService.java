package mtn.momo.contract.repayment.service;

import mtn.momo.contract.repayment.model.dto.InterestRateDto;

import java.util.List;

public interface ICacheService {
    List<InterestRateDto> getInterestRates();
    String getConfigValue(String keyName);
}
