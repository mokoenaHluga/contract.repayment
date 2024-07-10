package mtn.momo.contract.repayment.service;

import mtn.momo.contract.repayment.exception.UserAlreadyExistException;
import mtn.momo.contract.repayment.model.dto.UserDto;

public interface IUserService {
    UserDto registerNewUser(UserDto user) throws UserAlreadyExistException;
}
