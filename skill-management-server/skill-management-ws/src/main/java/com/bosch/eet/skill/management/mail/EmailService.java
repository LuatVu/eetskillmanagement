package com.bosch.eet.skill.management.mail;

import javax.mail.MessagingException;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.bosch.eet.skill.management.usermanagement.exception.UserManagementException;

@Component
public interface EmailService {

    String mailToRequestAccess(String requestUserEmail,
                               String requestUserId,
                               String reason) throws UserManagementException, MessagingException;

    String mailToUserAddedToGroup(String addUserId, String receiverId, String groupName);

    JavaMailSender getMailSender();

    String mailToUserAddedToSystem(String receiverEmail, String wamServer);

    String mailToApproverAboutNewRequest(String receiverId, String requesterId, String receiverEmail, String requestLink);

    String mailToRequesterAboutForwardRequest(String receiverId, String requesterId, String receiverEmail, String requestLink);

    String mailToApproverAboutForwardRequest(String receiverId, String requesterId, String receiverEmail, String requestLink);

    String mailToRequesterAndApproverAboutRequest(String personalId, String receiverEmail, String requestId, String requestLink);

    String mailToUserAssignedCourse(String personalId, String receiverEmail, String courseName, String requestLink);

    String mailToUserAssignedDemand(String receiverEmail, String requestLink, String demandId);

    String mailToUserAssignedProject(String personalId, String receiverEmail, String projectName, String requestLink);

    String mailPendingRequest(String nameDisplay, String email, Long count, String requestLink, String template, String title);
}
