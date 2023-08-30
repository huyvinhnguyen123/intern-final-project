package com.beetech.finalproject.domain.service;

import com.beetech.finalproject.common.AccountException;
import com.beetech.finalproject.common.LogStatus;
import com.beetech.finalproject.domain.entities.User;
import com.beetech.finalproject.domain.repository.UserRepository;
import com.beetech.finalproject.utils.CustomDateTimeFormatter;
import com.beetech.finalproject.web.dtos.user.UserCreateDto;
import com.beetech.finalproject.web.dtos.user.UserDetailDto;
import com.beetech.finalproject.web.security.PasswordEncrypt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;

    /**
     * valid loginId
     *
     * @param loginId - input loginId
     */
    public void validLoginId(String loginId) {
        User existingUser = userRepository.findByLoginId(loginId);
        if (existingUser != null && !existingUser.isAccountNonLocked()) {
            log.error("This user is already locked: {}", loginId);
            throw new AccountException(AccountException.ErrorStatus.LOCKED_ACCOUNT, "The user is already locked.");
        }
    }

    /**
     * find user by id
     *
     * @param userId - input userId
     * @return - userDetailDto
     */
    public UserDetailDto findAndDisplayUserDetailById(String userId) {
        User existingUser = userRepository.findById(userId).orElseThrow(
                ()->{
                    log.error(LogStatus.selectOneFail("user"));
                    return new NullPointerException(LogStatus.selectOneFail("user") + userId);
                }
        );
        log.info(LogStatus.selectOneSuccess("user"));

        UserDetailDto userDetailDto = new UserDetailDto();
        userDetailDto.setUserId(existingUser.getUserId());
        userDetailDto.setUsername(existingUser.getUsername());
        userDetailDto.setBirthDay(existingUser.getBirthDay());

        return userDetailDto;
    }

    /**
     * update user
     *
     * @param userCreateDto - input userCreateDto's
     * @return - user
     */
    public UserDetailDto updateUser(UserCreateDto userCreateDto) {
        User existingUser = userRepository.findByLoginId(userCreateDto.getLoginId());
        if(existingUser == null) {
            log.error(LogStatus.selectOneFail("user"));
            throw new NullPointerException(LogStatus.searchOneFail("user") + userCreateDto.getLoginId());
        }
        log.info(LogStatus.selectOneSuccess("user"));

        validLoginId(existingUser.getLoginId());

        existingUser.setLoginId(userCreateDto.getLoginId());
        existingUser.setPassword(PasswordEncrypt.bcryptPassword(userCreateDto.getPassword()));
        existingUser.setUsername(userCreateDto.getUsername());
        existingUser.setBirthDay(CustomDateTimeFormatter.dateOfBirthFormatter(userCreateDto.getBirthDay()));
        userRepository.save(existingUser);
        log.info(LogStatus.updateSuccess("user"));

        UserDetailDto userDetailDto = new UserDetailDto();
        userDetailDto.setUserId(existingUser.getUserId());
        userDetailDto.setUsername(existingUser.getUsername());
        userDetailDto.setBirthDay(existingUser.getBirthDay());

        return userDetailDto;
    }

    /**
     * delete user(soft delete)
     *
     * @param userId - input userId
     * @return - user without loginId
     */
    public UserDetailDto deleteUser(String userId) {
        User existingUser = userRepository.findById(userId).orElseThrow(
                ()->{
                    log.error(LogStatus.selectOneFail("user"));
                    return new NullPointerException(LogStatus.selectOneFail("user") + userId);
                }
        );
        log.info(LogStatus.selectOneSuccess("user"));

        existingUser.setDeleteFlag(9);
        existingUser.setOldLoginId(existingUser.getLoginId());
        existingUser.setLoginId(null);
        userRepository.save(existingUser);
        log.info("Soft delete user success");

        UserDetailDto userDetailDto = new UserDetailDto();
        userDetailDto.setUserId(existingUser.getUserId());
        userDetailDto.setUsername(existingUser.getUsername());
        userDetailDto.setBirthDay(existingUser.getBirthDay());

        return userDetailDto;
    }
}
