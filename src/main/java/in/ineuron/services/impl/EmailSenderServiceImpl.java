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

    // Retrieve the email address from application.properties
    @Value("${spring.mail.username}")
    String fromEmail;

    // Method to send an email
    public String sendEmail(String to, String sub, String textMessage) throws MessagingException {

        // Create a MimeMessage instance
        MimeMessage message = mailSender.createMimeMessage();

        // Initialize MimeMessageHelper to assist with creating the message
        MimeMessageHelper helper = new MimeMessageHelper(message);

        // Set the sender email address
        helper.setFrom(fromEmail);
        // Set the recipient email address
        helper.setTo(to);
        // Set the email subject
        helper.setSubject(sub);
        // Set the email body
        helper.setText(textMessage);

        // Send the email
        mailSender.send(message);

        // Return a message indicating that the email has been sent
        return "mail-sent";
    }

}
