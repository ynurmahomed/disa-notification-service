package disa.notification.service.service.interfaces;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public interface MailService {
    void sendEmail(final String recipientEmail, final List<ViralLoaderResult> viralLoaders) throws MessagingException, UnsupportedEncodingException;

}
