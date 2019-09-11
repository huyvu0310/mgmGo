package com.mgmtp.internship.experiences.config.security;

import com.mgmtp.internship.experiences.dto.UserProfileDTO;
import com.mgmtp.internship.experiences.model.tables.tables.records.UserRecord;
import com.mgmtp.internship.experiences.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;

import java.util.Collection;

/**
 * Override LdapuserDetailsMapper.
 *
 * @author tdnguyen
 */
public class CustomLdapUserDetailMapper extends LdapUserDetailsMapper {
    @Autowired
    private UserService userService;

    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
        UserDetails details = super.mapUserFromContext(ctx, username, authorities);
        if (!userService.checkUsernameAvailable(username)) {
            userService.insertUser(username);
        }
        UserRecord userRecord = userService.findUserByUserName(username);
        UserProfileDTO userProfileDTO = new UserProfileDTO(userRecord.getImageId(), userRecord.getDisplayName(), userRecord.getReputationScore());
        return new CustomLdapUserDetails(userRecord.getId(), userProfileDTO, (LdapUserDetails) details);
    }
}
