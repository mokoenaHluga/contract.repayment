package mtn.momo.contract.repayment.model.dto;

import mtn.momo.contract.repayment.service.repository.entity.InterestRate;

public class InterestRateMapper {

    public static InterestRateDto toDto(InterestRate entity) {
        if (entity == null) {
            return null;
        }

        return InterestRateDto.builder().interestRate(entity.getInterestRate()).build();
    }
}
