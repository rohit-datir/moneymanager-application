package money.example.moneyManager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${BREVO_FROM_EMAIL:rohitdatir84@gmail.com}")
    private String senderEmail;

    public void sendEmail(String to, String subject, String body) {
        // Implementation for sending email
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);

        }catch (Exception e){
           throw new RuntimeException(e.getMessage());
        }

    }
}
