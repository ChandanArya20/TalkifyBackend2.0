package in.ineuron.constant;

import lombok.Getter;

@Getter
public enum ErrorConstant {

	GENERIC_ERROR(1000, "Unable to process your request, please try again later."),
	EMAIL_CONFLICT_ERROR(1001, "Email already registered with another account"),
	PHONE_CONFLICT_ERROR(1002, "Phone No. already registered with another account"),
	USER_NOT_FOUND_ERROR(1003, "User not found"),
	USER_NOT_AUTHORIZED_ERROR(1004, "User not authorized"),
	TOKEN_EXPIRED_ERROR(1005, "Token is expired"),
	TOKEN_NOT_FOUND_ERROR(1006, "Token is not found with request"),
	MESSAGE_NOT_FOUND_ERROR(1007, "Message not found"),
	INVALID_REQUEST_DATA_ERROR(1008, "Request is invalid"),
	INVALID_REQUEST_FIELD_ERROR(1009, "Request data field is invalid"),
	CHAT_NOT_FOUND_ERROR(1010, "Chat not found"),
	USER_CONFLICT_ERROR(1011, "Conflict"),
	INVALID_PASSWORD_ERROR(1012, "Invalid password"),
	EMAIL_SENDING_ERROR(1013, "Error while sending email"),
	OTP_ERROR(1013, "OTP error"),
	TOKEN_INVALID_ERROR(1014, "Token is invalid"),
	NOT_ALLOWED_RESOURCE_ERROR(1015, "Forbidden"),
	USER_UNAUTHORIZED_ERROR(1016, "Unauthorized");

	private final int errorCode;
    private final String errorMessage;
    
    ErrorConstant(int errorCode, String errorMessage) {
    	this.errorCode = errorCode;
    	this.errorMessage = errorMessage;
    }

}
