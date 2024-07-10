package mtn.momo.contract.repayment.service.repository;

import mtn.momo.contract.repayment.service.repository.entity.ConfigurationSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConfigurationSettingRepository extends JpaRepository<ConfigurationSetting, UUID> {
    ConfigurationSetting findByKeyName(String keyName);
}
