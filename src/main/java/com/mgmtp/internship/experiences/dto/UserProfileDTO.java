package com.mgmtp.internship.experiences.dto;

import com.mgmtp.internship.experiences.constants.ApplicationConstant;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * User profile Dto.
 *
 * @author thuynh
 */
public class UserProfileDTO {

    private Long imageId;

    @NotBlank(message = "Display Name may not be blank")
    @Size(max = 30, message = "You can't not write more than 30 characters")
    private String displayName;

    private int reputationScore;
    private String levelName;

    public UserProfileDTO() {
    }

    public UserProfileDTO(Long imageId, String displayName, int reputationScore) {
        this.imageId = imageId;
        this.displayName = displayName;
        this.reputationScore = reputationScore;
        setLevelName(reputationScore);
    }

    public UserProfileDTO(Long imageId, String displayName) {
        this.imageId = imageId;
        this.displayName = displayName;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getReputationScore() {
        return reputationScore;
    }

    public void setReputatinScore(int reputationScore) {
        this.reputationScore = reputationScore;
        setLevelName(reputationScore);
    }

    public String getLevelName() { return levelName; }

    public void setLevelName(int reputationScore) { this.levelName = ApplicationConstant.getLevelReputation(reputationScore); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfileDTO that = (UserProfileDTO) o;
        return Objects.equals(imageId, that.imageId) &&
                Objects.equals(displayName, that.displayName);
    }


    @Override
    public int hashCode() {
        return Objects.hash(imageId, displayName);
    }

}
