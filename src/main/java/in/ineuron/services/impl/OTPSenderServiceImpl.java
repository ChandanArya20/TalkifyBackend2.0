package in.ineuron.services.impl;

import in.ineuron.services.EmailSenderService;
import in.ineuron.services.OTPSenderService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@AllArgsConstructor
public class OTPSenderServiceImpl implements OTPSenderService {

    private EmailSenderService emailSender;

    @Override
    public Integer sendOTPByEmail(String email ) throws MessagingException {

        Random random = new Random();
        int OTP = random.nextInt(100000, 999999);

        emailSender.sendEmail(email,"to send/verify OTP", "Your OTP is : "+OTP);
        return OTP;
    }

}

