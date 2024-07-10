package mtn.momo.contract.repayment.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mtn.momo.contract.repayment.model.dto.UserDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    private String userName;
    private String password;
    private UserDto userDto;
}
