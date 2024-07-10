package mtn.momo.contract.repayment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mtn.momo.contract.repayment.model.dto.Error;
import mtn.momo.contract.repayment.model.dto.UserDto;
import mtn.momo.contract.repayment.model.response.RegisterUserResponseWrapper;
import mtn.momo.contract.repayment.service.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserServiceController {
    private final IUserService IUserService;

    @Operation(summary = "Register new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content =
                    {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema =
                    @Schema(implementation = UserDto.class))})})
    @PostMapping(value = "/user/register/v1", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegisterUserResponseWrapper> registerUser(@RequestBody UserDto userDto) {
        RegisterUserResponseWrapper responseWrapper;
        ResponseEntity<RegisterUserResponseWrapper> responseEntity;

        try {
            responseWrapper = new RegisterUserResponseWrapper();
            responseWrapper.setData(IUserService.registerNewUser(userDto));
            responseEntity = ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(responseWrapper);
        } catch (Exception ex) {
            log.error("Error registering new user: {}", userDto.getUsername(), ex);
            responseWrapper = new RegisterUserResponseWrapper();
            responseWrapper.setErrors(Collections.singletonList(new Error(ex.getMessage())));
            responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(responseWrapper);        }
        return responseEntity;
    }
}
