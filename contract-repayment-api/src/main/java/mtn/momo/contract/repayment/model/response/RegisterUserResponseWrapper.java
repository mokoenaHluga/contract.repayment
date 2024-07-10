package mtn.momo.contract.repayment.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import mtn.momo.contract.repayment.model.dto.Error;
import mtn.momo.contract.repayment.model.dto.UserDto;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"data", "errors"})
public class RegisterUserResponseWrapper implements Serializable {
    private UserDto data;
    private List<Error> errors;

    @JsonProperty("data")
    public UserDto getData() {
        return data;
    }

    public void setData(UserDto data) {
        this.data = data;
    }

    @JsonProperty("errors")
    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }
}
