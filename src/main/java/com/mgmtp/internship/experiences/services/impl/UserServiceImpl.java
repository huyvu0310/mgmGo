package com.mgmtp.internship.experiences.services.impl;

import com.mgmtp.internship.experiences.config.security.CustomLdapUserDetails;
import com.mgmtp.internship.experiences.dto.UserProfileDTO;
import com.mgmtp.internship.experiences.model.tables.tables.records.UserRecord;
import com.mgmtp.internship.experiences.repositories.UserRepository;
import com.mgmtp.internship.experiences.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * User service.
 *
 * @author thuynh
 */
@Service
public class UserServiceImpl implements UserService {

    private Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public int insertUser(String username) {
        return userRepository.insertUser(username);
    }

    @Override
    public boolean checkUsernameAvailable(String username) {
        return userRepository.checkUsernameAvailable(username);
    }

    @Override
    public UserRecord findUserByUserName(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public UserProfileDTO findUserProfileById(long userId) {
        return userRepository.findUserProfileById(userId);
    }

    @Override
    public CustomLdapUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        return (CustomLdapUserDetails) principal;
    }

    @Override
    public boolean updateProfile(long userId, UserProfileDTO profile) {
        if (userRepository.updateProfile(userId, profile)) {
            CustomLdapUserDetails currentUser = getCurrentUser();
            if (currentUser == null) {
                return false;
            }
            currentUser.getUserProfileDTO().setDisplayName(profile.getDisplayName());
            return true;
        }
        return false;
    }

    @Override
    public boolean checkExitDisplayName(String displayName, long id) {
        return userRepository.checkExitDisplayName(displayName, id);
    }

    @Override
    public int getReputationScoreById(Long id) {
        UserRecord userRecord = userRepository.getReputationScoreById(id);
        return (userRecord == null) ? 0 : userRecord.getReputationScore();
    }

    @Override
    public boolean calculateAndUpdateRepulationScore(long id, int score) {
        int reputationScore = getReputationScoreById(id) + score;
        if (userRepository.updateReputationScoreById(id, reputationScore) == 1) {
            getCurrentUser().getUserProfileDTO().setReputatinScore(reputationScore);
            return true;
        }
        logger.error("Can't update reputation score");
        return false;
}
}
