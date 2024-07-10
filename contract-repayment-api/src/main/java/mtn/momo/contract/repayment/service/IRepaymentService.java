package mtn.momo.contract.repayment.service;

import mtn.momo.contract.repayment.exception.AnnualRateException;
import mtn.momo.contract.repayment.model.dto.RepaymentOption;
import mtn.momo.contract.repayment.model.request.RepaymentRequest;

import java.util.List;

public interface IRepaymentService {
    List<RepaymentOption> calculateRepaymentOptions(RepaymentRequest amount) throws AnnualRateException;
}
