package in.ineuron.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class InvalidRequestDataException extends RuntimeException {

    Map<String, String> errorResults;

    public InvalidRequestDataException(Map<String, String> errorResults) {
        this.errorResults=errorResults;
    }
}
