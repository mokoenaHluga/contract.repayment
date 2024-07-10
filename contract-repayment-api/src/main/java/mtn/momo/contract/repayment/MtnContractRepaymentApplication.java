package mtn.momo.contract.repayment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@EnableCaching
@SpringBootApplication
public class MtnContractRepaymentApplication {

	public static void main(String[] args) {
		SpringApplication.run(MtnContractRepaymentApplication.class, args);
	}

}
