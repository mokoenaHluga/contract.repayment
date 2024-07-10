package mtn.momo.contract.repayment.service.repository;

import mtn.momo.contract.repayment.service.repository.entity.InterestRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;

public interface InterestRateRepository extends JpaRepository<InterestRate, UUID> {
    @Query("SELECT i FROM InterestRate i ORDER BY i.createdDate DESC")
    List<InterestRate> findAllOrderByCreatedDateDesc();
}
