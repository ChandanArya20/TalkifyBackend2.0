package in.ineuron.services;


import jakarta.mail.MessagingException;

public interface OTPSenderService {
    Integer sendOTPByEmail(String email ) throws MessagingException;
}
