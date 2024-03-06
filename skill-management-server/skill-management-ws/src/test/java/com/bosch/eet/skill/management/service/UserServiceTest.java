package com.bosch.eet.skill.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bosch.eet.skill.management.exception.SkillManagementException;
import com.bosch.eet.skill.management.usermanagement.dto.UserDTO;
import com.bosch.eet.skill.management.usermanagement.entity.User;
import com.bosch.eet.skill.management.usermanagement.repo.UserRepository;
import com.bosch.eet.skill.management.usermanagement.service.impl.UserServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("FindByUserIDInDB happy case")
    public void testFindByNTId_whenUserRepoReturnUser_thenReturnUser() {

        User dummyUser = User.builder()
                .name("mac9hc")
                .build();

        when(userRepository.findByName("mac9hc")).thenReturn(
                Optional.ofNullable(dummyUser)
        );
        assertThat(userService.findByNTId("mac9hc")).isEqualTo(dummyUser);
    }

    @Test
    @DisplayName("FindByUserIDInDB null case")
    public void testFindByNTId_whenUserRepoReturnNull_thenReturnNull() {

        when(userRepository.findByName("mac9hc")).thenThrow(
                SkillManagementException.class
        );

        assertThrows(SkillManagementException.class, () -> userService.findByNTId("mac9hc"));
    }

    @Test
    @DisplayName("AddUser happy case")
    public void testAddUser_whenUserRepoDoseNotThrow_thenReturnTrue() {

        User user = User.builder().build();

        when(userRepository.save(ArgumentMatchers.any())).thenReturn(
                user
        );
        assertThat(userService.addUser(new UserDTO())).isEqualTo(user);
    }
}
