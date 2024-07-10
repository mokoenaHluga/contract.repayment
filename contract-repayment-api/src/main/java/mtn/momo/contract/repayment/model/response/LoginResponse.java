package mtn.momo.contract.repayment.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mtn.momo.contract.repayment.model.dto.UserDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String jwt;
    private String sessionId;
}
