package com.bosch.eet.skill.management.mail;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.bosch.eet.skill.management.usermanagement.exception.UserManagementException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SendEmailJob {

	@Autowired
	private Environment environment;
	
	@Autowired
    private SpringTemplateEngine templateEngine;
	
	//	@Scheduled(cron = "0 0 0 * * FRI")
	@Scheduled(cron = "0 0 */1 * * *") // for testing only: run 1 time/hour
	private void sendEmail() throws MessagingException, UserManagementException {
		String recipient = getRecipients();
		if (!recipient.isEmpty()) {
			JavaMailSenderImpl mailSender = getMailSender();
			Context context = getContext();
//			MimeMessage mimeMsg = getMimeMessage(mailSender, context, recipient, "spring.mail.template.news.weekly", Constants.MAIL_SUBJECT_NEWS_WEEKLY);
			
//			mailSender.send(mimeMsg);
			log.info("Send mail for: " + recipient + " sucessfully.");
		}
	}
	
	public void sendEmailRQONEConnectionError(final String errorCode, final String errorMessage, final String details) throws MessagingException {
//		String recipient = Constants.MAIL_GUIDED_TEAM;
		
		Context context = getRQONEConnectionErrorContext(errorCode, errorMessage, details);
		JavaMailSenderImpl mailSender = getMailSender();
//		MimeMessage mimeMsg = getMimeMessage(mailSender, context, recipient, "spring.mail.template.rq1.connection.error", Constants.MAIL_SUBJECT_RQ1_ERROR);
		
//		mailSender.send(mimeMsg);
		log.info("Send mail: RQONE Connection error sucessfully.");
	}
	
	private JavaMailSenderImpl getMailSender() {
		String host = environment.getProperty("spring.mail.host");
		String port = environment.getProperty("spring.mail.port");
		String username = environment.getProperty("spring.mail.username");
		String password = environment.getProperty("spring.mail.password");
		String protocol = environment.getProperty("spring.mail.protocol");
		String auth = environment.getProperty("spring.mail.properties.mail.smtp.auth");
		String starttls = environment.getProperty("spring.mail.properties.mail.smtp.starttls.enable");
		String tls = environment.getProperty("spring.mail.smtp.ssl.protocols");
		
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
	    mailSender.setHost(host);
	    mailSender.setPort(Integer.parseInt(port));
	    mailSender.setUsername(username);
	    mailSender.setPassword(password);
	    
	    Properties props = mailSender.getJavaMailProperties();
	    props.put("mail.transport.protocol", protocol);
	    props.put("mail.smtp.auth", auth);
	    props.put("mail.smtp.ssl.protocols", tls);
	    props.put("mail.smtp.starttls.enable", starttls);
	    props.put("mail.smtp.ssl.trust", host);
	    
	    return mailSender;
	}
	
	private Context getContext() {
		String date = getDateStartAndEndOfCurrentWeek();
	    
//	    String openPMTNews = settingService.findByKey(Constants.MAIL_OPEN_PMT_NEWS);
//	    String support = settingService.findByKey(Constants.MAIL_SUPPORT);
//	    String newsletterSettings = settingService.findByKey(Constants.MAIL_NEWSLETTER_SETTINGS);
	    
		// var in the template
	    Map<String, Object> properties = new HashMap<>();
        properties.put("date", date);
//        properties.put("openPMTNews", openPMTNews);
//        properties.put("support", support);
//        properties.put("newsletterSettings", newsletterSettings);
        
	    Context context = new Context();
        context.setVariables(properties);
        
        return context;
	}
	
	private MimeMessage getMimeMessage(JavaMailSenderImpl mailSender, Context context, final String recipient, 
			final String templateProperty, final String subject) throws MessagingException {
		String from = environment.getProperty("spring.mail.properties.mail.smtp.from");
		String enCoding = environment.getProperty("spring.mail.default-encoding");
		String templateName = environment.getProperty(templateProperty);
		
		MimeMessage mimeMsg = mailSender.createMimeMessage();
	    MimeMessageHelper helper = new MimeMessageHelper(mimeMsg, true, enCoding);
	    
	    helper.setFrom(from);
	    helper.setTo(InternetAddress.parse(recipient));
	    helper.setSubject(subject);
	    String html = templateEngine.process(templateName, context);
        helper.setText(html, true);
        
        return mimeMsg;
	}
	
	private String getRecipients() throws UserManagementException {
//		List<UserDto> userDtos = userService.findByConfigurationIdAndIsChecked(Constants.MAIL_WEEKLY_REPORT_ID, true);
		String recipient = "";
//		if (!CollectionUtils.isEmpty(userDtos)) {
//			List<String> mails = userDtos.stream().map(item -> item.getEmail()).collect(Collectors.toList());
//			recipient = String.join(",", mails);
//		}
		return recipient;
	}
	
	private String getDateStartAndEndOfCurrentWeek() {
		String result = "";
		LocalDate date = LocalDate.now();
	    LocalDate start = date;
	    while (start.getDayOfWeek() != DayOfWeek.MONDAY) {
	       start = start.minusDays(1);
	    }
	    String startDate = start.format(DateTimeFormatter.ofPattern("dd.MM.uuuu"));
	    
	    LocalDate end = date;
	    while (end.getDayOfWeek() != DayOfWeek.FRIDAY) {
	       end = end.plusDays(1);
	    }
	    String endDate = end.format(DateTimeFormatter.ofPattern("dd.MM.uuuu"));
	    result = String.format("%s - %s", startDate, endDate); 
	    return result;
	}
	
	private Context getRQONEConnectionErrorContext(final String errorCode, final String errorMessage, final String details) {
		// var in the template
	    Map<String, Object> properties = new HashMap<>();
        properties.put("errorCode", errorCode);
        properties.put("errorMessage", errorMessage);
        properties.put("details", details);
        
	    Context context = new Context();
        context.setVariables(properties);
        
        return context;
	}
}
