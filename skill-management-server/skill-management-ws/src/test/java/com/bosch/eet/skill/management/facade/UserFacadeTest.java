package com.bosch.eet.skill.management.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import javax.mail.MessagingException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bosch.eet.skill.management.common.constant.EmailConstant;
import com.bosch.eet.skill.management.facade.impl.UserFacadeImpl;
import com.bosch.eet.skill.management.ldap.exception.LdapException;
import com.bosch.eet.skill.management.ldap.model.LdapInfo;
import com.bosch.eet.skill.management.ldap.service.LdapService;
import com.bosch.eet.skill.management.mail.EmailService;
import com.bosch.eet.skill.management.usermanagement.entity.User;
import com.bosch.eet.skill.management.usermanagement.exception.UserManagementException;
import com.bosch.eet.skill.management.usermanagement.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class UserFacadeTest {

	@Mock
	UserService userService;

	@Mock
	LdapService ldapService;

	@Mock
	EmailService emailService;

	@InjectMocks
	UserFacadeImpl userFacade;

	@Test
	@DisplayName("FindByUserIDInDB happy case")
	public void testFindByUserIDInDB_whenUserServiceReturnUser_thenReturnUser() {

		User dummyUser = User.builder().name("mac9hc").build();

		when(userService.findByNTId("mac9hc")).thenReturn(dummyUser);
		assertThat(userFacade.findByUserIDInDB("mac9hc")).isEqualTo(dummyUser);
	}

	@Test
	@DisplayName("FindByUserIDInLDAP happy case")
	public void testFindByUserIDInLDAP_whenLdapServiceReturnUserInfo_thenReturnUserInfo() throws LdapException {

		Optional<LdapInfo> dummyUser = Optional.of(new LdapInfo());

		when(ldapService.getPrincipalInfo("mac9hc")).thenReturn(dummyUser);
		assertThat(userFacade.findByUserIDInLDAP("mac9hc")).isEqualTo(dummyUser.get());
	}

	@Test
	@DisplayName("FindByUserIDInLDAP happy case")
	public void testFindByUserIDInLDAP_whenLdapServiceThrowException_thenReturnNull() throws LdapException {
		when(ldapService.getPrincipalInfo("mac9hc").orElse(null)).thenThrow(LdapException.class);
		assertThat(userFacade.findByUserIDInLDAP("mac9hc")).isNull();
	}

	@Test
	@DisplayName("AddInternalUser happy case")
	public void testSendRequestAccessMail_whenEmailServiceReturn_thenReturn()
			throws UserManagementException, MessagingException {
		String reason = "test";

		LdapInfo ldapInfo = new LdapInfo();
		ldapInfo.setEmail("test");
		ldapInfo.setUserId("test");

		when(emailService.mailToRequestAccess(ldapInfo.getEmail(), ldapInfo.getUserId(), reason))
				.thenReturn(EmailConstant.SKM_USER_SEND_EMAIL_FAIL);
		assertThat(userFacade.sendRequestAccessMail(ldapInfo, reason)).isEqualTo(EmailConstant.SKM_USER_SEND_EMAIL_FAIL);
	}
}
