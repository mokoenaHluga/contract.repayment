package mtn.momo.contract.repayment.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"code", "message"})
public class Error {

    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String code;
    private String message;

    public Error(String message, String code) {
        this.message = message;
        this.code = code;
    }

    public Error(String message) {
        this.message = message;
    }

    public static class ErrorBuilder {
        private String code;
        private String message;

        public ErrorBuilder withCode(String code) {this.code = code;return this;}
        public ErrorBuilder withMessage(String message) {this.message = message;return this;}
        public Error build() {return new Error(this.code, this.message);}
        public String toString() {
            return "Error.ErrorBuilder(code=" + this.code + ", message=" + this.message + ")";
        }
    }
}
