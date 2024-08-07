package in.ineuron.services.impl;

import in.ineuron.services.EmailSenderService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderServiceImpl implements EmailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    String fromEmail;

    public String sendEmail(String to, String sub, String textMessage) throws MessagingException {

        // Create a MimeMessage instance
        MimeMessage message = mailSender.createMimeMessage();
        // Initialize MimeMessageHelper to assist with creating the message
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(sub);
        helper.setText(textMessage);

        mailSender.send(message);

        return "mail-sent";
    }

}
