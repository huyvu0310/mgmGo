package com.mgmtp.internship.experiences.config.security;

import com.mgmtp.internship.experiences.dto.UserProfileDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapUserDetails;

import java.util.Collection;

/**
 * Custom LdapUserDetails.
 *
 * @author tdnguyen
 */
public class CustomLdapUserDetails implements LdapUserDetails {
    private Long id;
    private UserProfileDTO userProfileDTO;
    private LdapUserDetails details;

    public CustomLdapUserDetails(Long id, UserProfileDTO userProfileDTO, LdapUserDetails details) {
        this.id = id;
        this.userProfileDTO = userProfileDTO;
        this.details = details;
    }

    public CustomLdapUserDetails(LdapUserDetails details) {
        this.details = details;
    }

    public boolean isEnabled() {
        return details.isEnabled();
    }

    public String getDn() {
        return details.getDn();
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return details.getAuthorities();
    }

    public String getPassword() {
        return details.getPassword();
    }

    public String getUsername() {
        return details.getUsername();
    }

    public boolean isAccountNonExpired() {
        return details.isAccountNonExpired();
    }

    public boolean isAccountNonLocked() {
        return details.isAccountNonLocked();
    }

    public boolean isCredentialsNonExpired() {
        return details.isCredentialsNonExpired();
    }

    @Override
    public void eraseCredentials() {
        this.details.eraseCredentials();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserProfileDTO getUserProfileDTO() {
        return userProfileDTO;
    }

    public void setUserProfileDTO(UserProfileDTO userProfileDTO) {
        this.userProfileDTO = userProfileDTO;
    }

    @Override
    public String toString() {
        return "CustomLdapUserDetails{" +
                "id=" + id +
                ", userProfileDTO=" + userProfileDTO +
                ", details=" + details +
                '}';
    }
}