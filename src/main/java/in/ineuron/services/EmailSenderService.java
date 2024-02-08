package in.ineuron.services;


import jakarta.mail.MessagingException;

public interface EmailSenderService {

    String sendEmail(String to, String sub, String textMessage) throws MessagingException;

}
