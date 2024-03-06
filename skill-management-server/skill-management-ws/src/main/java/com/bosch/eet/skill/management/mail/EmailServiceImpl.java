package com.bosch.eet.skill.management.mail;

import static com.bosch.eet.skill.management.common.constant.EmailConstant.ADD_USER_SUBJECT;
import static com.bosch.eet.skill.management.common.constant.EmailConstant.ADD_USER_SYSTEM;
import static com.bosch.eet.skill.management.common.constant.EmailConstant.ASSIGN_DEMAND;
import static com.bosch.eet.skill.management.common.constant.EmailConstant.ASSIGN_USER_COURSE;
import static com.bosch.eet.skill.management.common.constant.EmailConstant.ASSIGN_USER_PROJECT;
import static com.bosch.eet.skill.management.common.constant.EmailConstant.FORWARDED_REQUEST_EVALUATION_APPROVER;
import static com.bosch.eet.skill.management.common.constant.EmailConstant.FORWARDED_REQUEST_EVALUATION_REQUESTER;
import static com.bosch.eet.skill.management.common.constant.EmailConstant.NEW_REQUEST_CREATED_FOR_APPROVER;
import static com.bosch.eet.skill.management.common.constant.EmailConstant.NO_EMAIL_FOUND;
import static com.bosch.eet.skill.management.common.constant.EmailConstant.REQUEST_ACCESS_SUBJECT;
import static com.bosch.eet.skill.management.common.constant.EmailConstant.SKM_USER_SEND_EMAIL_FAIL;
import static com.bosch.eet.skill.management.common.constant.EmailConstant.SPRING_MAIL_RECEIVER;
import static com.bosch.eet.skill.management.common.constant.EmailConstant.SPRING_MAIL_RECEIVER_2;
import static com.bosch.eet.skill.management.common.constant.EmailConstant.SPRING_MAIL_TEMPLATE_ADD_TO_GROUP;
import static com.bosch.eet.skill.management.common.constant.EmailConstant.SPRING_MAIL_TEMPLATE_ADD_USER_TO_SYSTEM;
import static com.bosch.eet.skill.management.common.constant.EmailConstant.SPRING_MAIL_TEMPLATE_ASSIGNED_DEMAND;
import static com.bosch.eet.skill.management.common.constant.EmailConstant.SPRING_MAIL_TEMPLATE_ASSIGNED_USER;
import static com.bosch.eet.skill.management.common.constant.EmailConstant.SPRING_MAIL_TEMPLATE_FORWARD_REQUEST_APPROVER;
import static com.bosch.eet.skill.management.common.constant.EmailConstant.SPRING_MAIL_TEMPLATE_FORWARD_REQUEST_REQUESTER;
import static com.bosch.eet.skill.management.common.constant.EmailConstant.SPRING_MAIL_TEMPLATE_NEW_REQUEST_APPROVER;
import static com.bosch.eet.skill.management.common.constant.EmailConstant.SPRING_MAIL_TEMPLATE_REQUEST_ACCESS;
import static com.bosch.eet.skill.management.common.constant.EmailConstant.SPRING_MAIL_TEMPLATE_UPDATE_REQUEST;
import static com.bosch.eet.skill.management.common.constant.EmailConstant.UPDATED_REQUEST_EVALUATION;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.bosch.eet.skill.management.common.constant.EmailConstant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EmailServiceImpl implements EmailService {

    @Autowired
    private Environment environment;
    @Autowired
    private SpringTemplateEngine templateEngine;

    @Override
	@Async
    public String mailToRequestAccess(String requestUserEmail,
                                      String requestUserId,
                                      String reason) {

        String recipient = environment.getProperty(SPRING_MAIL_RECEIVER);
        String recipient2 = environment.getProperty(SPRING_MAIL_RECEIVER_2);
        JavaMailSender mailSender = getMailSender();
        Context context = getContext(requestUserId, reason);
        MimeMessage mimeMsg;
        try {
            mimeMsg = getMimeMessage(
                    mailSender,
                    context,
                    recipient,
                    SPRING_MAIL_TEMPLATE_REQUEST_ACCESS,
                    REQUEST_ACCESS_SUBJECT
            );
        } catch (MessagingException ex) {
            return EmailConstant.CREATE_FAIL;
        }
        
        try {
        	getMimeMessage(
                    mailSender,
                    context,
                    recipient,
                    SPRING_MAIL_TEMPLATE_REQUEST_ACCESS,
                    REQUEST_ACCESS_SUBJECT
            );
        } catch (MessagingException ex){
            return EmailConstant.CREATE_FAIL;
        }

        if (sendMail(mimeMsg, mailSender)) {
            log.info("Send mail for: " + recipient + " sucessfully.");
            log.info("Send mail for: " + recipient2 + " sucessfully.");
            return EmailConstant.SEND_SUCCESS;
        } else {
			return EmailConstant.SKM_USER_SEND_EMAIL_FAIL;
		}
    }


    @Override
	@Async
    public String mailToUserAddedToGroup(String addUserId, String receiverEmail, String groupName) {
        if (receiverEmail == null) {
			return NO_EMAIL_FOUND;
		}

        JavaMailSender mailSender = getMailSender();
        Context context = getContextAddUser(addUserId, groupName);
        MimeMessage mimeMsg;
        try {
            mimeMsg = getMimeMessage(
                    mailSender,
                    context,
                    receiverEmail,
                    SPRING_MAIL_TEMPLATE_ADD_TO_GROUP,
                    ADD_USER_SUBJECT
            );
        } catch (MessagingException ex) {
            return EmailConstant.CREATE_FAIL;
        }

        if (sendMail(mimeMsg, mailSender)) {
            log.info("Send mail for: " + receiverEmail + " sucessfully.");
            return EmailConstant.SEND_SUCCESS;
        } else {
			return SKM_USER_SEND_EMAIL_FAIL;
		}
    }

    @Override
    public JavaMailSender getMailSender() {
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

    private Context getContext(String requestUserEmail, String requestUserI) {

        // var in the template
        Map<String, Object> properties = new HashMap<>();
        properties.put("username", requestUserEmail);
        properties.put("reason", requestUserI);

        Context context = new Context();
        context.setVariables(properties);

        return context;
    }

    private Context getContextAddUser(String modifiedUserName, String groupName) {

        // var in the template
        Map<String, Object> properties = new HashMap<>();
        properties.put("addUserId", modifiedUserName);
        properties.put("groupName", groupName);

        Context context = new Context();
        context.setVariables(properties);

        return context;
    }

    private Context getContextAddAssociateToSystem(String receiverEmail, String wamServer) {

        Map<String, Object> properties = new HashMap<>();
        properties.put("receiverEmail", receiverEmail);
        properties.put("wamServer", wamServer);

        Context context = new Context();
        context.setVariables(properties);

        return context;
    }


    private Context getContextUpdateRequest(String personalId, String requestId, String requestLink) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("personalId", personalId);
        properties.put("requestId", requestId);
        properties.put("requestLink", requestLink);
        Context context = new Context();
        context.setVariables(properties);
        return context;
    }

    private Context getContextForwardRequest(String receiverId, String requesterId, String requestLink) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("receiverId", receiverId);
        properties.put("requesterId", requesterId);
        properties.put("requestLink", requestLink);

        Context context = new Context();
        context.setVariables(properties);

        return context;
    }

    private Context getContextAssignUser(String personalId, String receiverEmail, String name, String requestLink) {

        // var in the template
        Map<String, Object> properties = new HashMap<>();
        properties.put("personalId", personalId);
        properties.put("receiverEmail", receiverEmail);
        properties.put("name", name);
        properties.put("requestLink", requestLink);
        Context context = new Context();
        context.setVariables(properties);

        return context;
    }

    private Context getContextAssignDemand(String requestLink, String demandId) {

        // var in the template
        Map<String, Object> properties = new HashMap<>();
        properties.put("requestLink", requestLink);
        properties.put("demandId", demandId);
        Context context = new Context();
        context.setVariables(properties);

        return context;
    }

    private MimeMessage getMimeMessage(JavaMailSender mailSender, Context context, final String recipient,
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

    private boolean sendMail(MimeMessage message, JavaMailSender mailSender) {
        try {
            mailSender.send(message);
            log.info(EmailConstant.SEND_SUCCESS);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
	@Async
    public String mailToRequesterAndApproverAboutRequest(String personalId, String receiverEmail, String requestId, String requestLink) {
        if (receiverEmail == null) {
			return NO_EMAIL_FOUND;
		}

        JavaMailSender mailSender = getMailSender();
        Context context = getContextUpdateRequest(personalId, requestId, requestLink);
        MimeMessage mimeMsg;
        try {
            mimeMsg = getMimeMessage(
                    mailSender,
                    context,
                    receiverEmail,
                    SPRING_MAIL_TEMPLATE_UPDATE_REQUEST,
                    UPDATED_REQUEST_EVALUATION
            );
        } catch (MessagingException ex) {
            return EmailConstant.CREATE_FAIL;
        }

        if (sendMail(mimeMsg, mailSender)) {
            log.info("Send mail for: " + receiverEmail + " sucessfully.");
            return EmailConstant.SEND_SUCCESS;
        } else {
			return SKM_USER_SEND_EMAIL_FAIL;
		}
    }

    @Override
	@Async
    public String mailToRequesterAboutForwardRequest(String receiverId, String requesterId, String receiverEmail, String requestLink) {
        if (receiverEmail == null) {
			return NO_EMAIL_FOUND;
		}

        JavaMailSender mailSender = getMailSender();
        Context context = getContextForwardRequest(receiverId, requesterId, requestLink);
        MimeMessage mimeMsg;

        try {
            mimeMsg = getMimeMessage(
                    mailSender,
                    context,
                    receiverEmail,
                    SPRING_MAIL_TEMPLATE_FORWARD_REQUEST_REQUESTER,
                    FORWARDED_REQUEST_EVALUATION_REQUESTER
            );
        } catch (MessagingException e) {
            return EmailConstant.CREATE_FAIL;
        }
        if (sendMail(mimeMsg, mailSender)) {
            log.info("Send mail for: " + receiverEmail + " sucessfully.");
            return EmailConstant.SEND_SUCCESS;
        } else {
			return SKM_USER_SEND_EMAIL_FAIL;
		}

    }

    @Override
	@Async
    public String mailToApproverAboutForwardRequest(String receiverId, String requesterId, String receiverEmail, String requestLink) {
        if (receiverEmail == null) {
			return NO_EMAIL_FOUND;
		}

        JavaMailSender mailSender = getMailSender();
        Context context = getContextForwardRequest(receiverId, requesterId, requestLink);
        MimeMessage mimeMsg;

        try {
            mimeMsg = getMimeMessage(
                    mailSender,
                    context,
                    receiverEmail,
                    SPRING_MAIL_TEMPLATE_FORWARD_REQUEST_APPROVER,
                    FORWARDED_REQUEST_EVALUATION_APPROVER
            );
        } catch (MessagingException e) {
            return EmailConstant.CREATE_FAIL;
        }
        if (sendMail(mimeMsg, mailSender)) {
            log.info("Send mail for: " + receiverEmail + " sucessfully.");
            return EmailConstant.SEND_SUCCESS;
        } else {
			return SKM_USER_SEND_EMAIL_FAIL;
		}

    }

    @Override
	@Async
    public String mailToApproverAboutNewRequest(String receiverId, String requesterId, String receiverEmail, String requestLink) {
        if (receiverEmail == null) {
			return NO_EMAIL_FOUND;
		}

        JavaMailSender mailSender = getMailSender();
        Context context = getContextForwardRequest(receiverId, requesterId, requestLink);
        MimeMessage mimeMsg;

        try {
            mimeMsg = getMimeMessage(
                    mailSender,
                    context,
                    receiverEmail,
                    SPRING_MAIL_TEMPLATE_NEW_REQUEST_APPROVER,
                    NEW_REQUEST_CREATED_FOR_APPROVER
            );
        } catch (MessagingException e) {
            return EmailConstant.CREATE_FAIL;
        }
        if (sendMail(mimeMsg, mailSender)) {
            log.info("Send mail for: " + receiverEmail + " sucessfully.");
            return EmailConstant.SEND_SUCCESS;
        } else {
			return SKM_USER_SEND_EMAIL_FAIL;
		}
    }

    @Override
	@Async
    public String mailToUserAddedToSystem(String receiverEmail, String wamServer) {
        if (receiverEmail == null) {
			return NO_EMAIL_FOUND;
		}

        JavaMailSender mailSender = getMailSender();
        Context context = getContextAddAssociateToSystem(receiverEmail, wamServer);
        MimeMessage mimeMsg;
        try {
            mimeMsg = getMimeMessage(
                    mailSender,
                    context,
                    receiverEmail,
                    SPRING_MAIL_TEMPLATE_ADD_USER_TO_SYSTEM,
                    ADD_USER_SYSTEM
            );
        } catch (MessagingException ex) {
            return EmailConstant.CREATE_FAIL;
        }

        if (sendMail(mimeMsg, mailSender)) {
            log.info("Send mail for: " + receiverEmail + " sucessfully.");
            return EmailConstant.SEND_SUCCESS;
        } else {
			return SKM_USER_SEND_EMAIL_FAIL;
		}

    }

    @Override
	@Async
    public String mailToUserAssignedCourse(String personalId, String receiverEmail, String courseName, String requestLink) {
        if (receiverEmail == null) {
			return NO_EMAIL_FOUND;
		}

        JavaMailSender mailSender = getMailSender();
        Context context = getContextAssignUser(personalId, receiverEmail, courseName, requestLink);
        MimeMessage mimeMsg;
        try {
            mimeMsg = getMimeMessage(
                    mailSender,
                    context,
                    receiverEmail,
                    SPRING_MAIL_TEMPLATE_ASSIGNED_USER,
                    ASSIGN_USER_COURSE
            );
        } catch (MessagingException ex) {
            return EmailConstant.CREATE_FAIL;
        }

        if (sendMail(mimeMsg, mailSender)) {
            log.info("Send mail for: " + receiverEmail + " sucessfully.");
            return EmailConstant.SEND_SUCCESS;
        } else {
			return SKM_USER_SEND_EMAIL_FAIL;
		}

    }

    @Override
    @Async
    public String mailToUserAssignedDemand(String receiverEmail, String requestLink, String demandId) {
        if (receiverEmail == null) {
			return NO_EMAIL_FOUND;
		}

        JavaMailSender mailSender = getMailSender();
        Context context = getContextAssignDemand(requestLink, demandId);
        MimeMessage mimeMsg;
        try {
            mimeMsg = getMimeMessage(
                    mailSender,
                    context,
                    receiverEmail,
                    SPRING_MAIL_TEMPLATE_ASSIGNED_DEMAND,
                    ASSIGN_DEMAND
            );
        } catch (MessagingException ex) {
            return EmailConstant.CREATE_FAIL;
        }

        if (sendMail(mimeMsg, mailSender)) {
            log.info("Send mail for: " + receiverEmail + " sucessfully.");
            return EmailConstant.SEND_SUCCESS;
        } else {
			return SKM_USER_SEND_EMAIL_FAIL;
		}
    }

    @Override
	@Async
    public String mailToUserAssignedProject(String personalId, String receiverEmail, String projectName, String requestLink) {
        if (receiverEmail == null) {
			return NO_EMAIL_FOUND;
		}

        JavaMailSender mailSender = getMailSender();
        Context context = getContextAssignUser(personalId, receiverEmail, projectName, requestLink);
        MimeMessage mimeMsg;
        try {
            mimeMsg = getMimeMessage(
                    mailSender,
                    context,
                    receiverEmail,
                    SPRING_MAIL_TEMPLATE_ASSIGNED_USER,
                    ASSIGN_USER_PROJECT
            );
        } catch (MessagingException ex) {
            return EmailConstant.CREATE_FAIL;
        }

        if (sendMail(mimeMsg, mailSender)) {
            log.info("Send mail for: " + receiverEmail + " sucessfully.");
            return EmailConstant.SEND_SUCCESS;
        } else {
			return SKM_USER_SEND_EMAIL_FAIL;
		}
    }

    private Context getContextPendingRequest(String nameDisplay, Long count, String requestLink) {

        // var in the template
        Map<String, Object> properties = new HashMap<>();
        properties.put("nameDisplay", nameDisplay);
        properties.put("count", count);
        properties.put("requestLink", requestLink);
        Context context = new Context();
        context.setVariables(properties);

        return context;
    }

    @Override
    public String mailPendingRequest(String nameDisplay, String email, Long count, String requestLink, String template, String tilte) {
        JavaMailSender mailSender = getMailSender();
        Context context = getContextPendingRequest(nameDisplay, count, requestLink);
        MimeMessage mimeMsg;
        try {
            mimeMsg = getMimeMessage(
                    mailSender,
                    context,
                    email,
                    template,
                    tilte
            );
        } catch (MessagingException ex) {
            return EmailConstant.CREATE_FAIL;
        }

        if (sendMail(mimeMsg, mailSender)) {
            log.info("Send mail for: " + email + " sucessfully.");
            return EmailConstant.SEND_SUCCESS;
        } else {
			return SKM_USER_SEND_EMAIL_FAIL;
		}
    }


}
