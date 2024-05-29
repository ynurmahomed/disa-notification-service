package disa.notification.service.config;

import java.util.Collections;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import disa.notification.service.service.impl.FileSystemMailService;
import disa.notification.service.service.impl.MailServiceImpl;
import disa.notification.service.service.interfaces.MailService;

@Configuration
@EnableTransactionManagement 
public class MailSenderConfig {

    @Bean
    public TemplateEngine emailTemplateEngine() {
        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(htmlTemplateResolver());
        return templateEngine;
    }

    private ITemplateResolver htmlTemplateResolver() {
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setOrder(Integer.valueOf(2));
        templateResolver.setResolvablePatterns(Collections.singleton("html/*"));
        templateResolver.setPrefix("/mail/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding("spring.mail.encoding");
        templateResolver.setCacheable(false);
        return templateResolver;
    }

    @Bean
    @ConditionalOnProperty(name = "app.mailservice", havingValue = "javaMail")
    public MailService mailServiceImpl(TemplateEngine templateEngine, MessageSource messageSource) {
        return new MailServiceImpl(templateEngine, messageSource);
    }

    @Bean
    @ConditionalOnProperty(name = "app.mailservice", havingValue = "fileSystem")
    MailService fileSystemMailService(MessageSource messageSource) {
        return new FileSystemMailService(messageSource);
    }
}
