package com.mgmtp.internship.experiences.dto;

/**
 * Comment DTO
 *
 * @author hnguyen
 */
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

public class CommentDTO {
    private Long id;
    private UserProfileDTO userProfileDTO;
    @NotBlank(message = "Comment may not be blank")

    @Size(max = 10001, message = "You can not write more than 10000 characters for comment")
    private String content;
    private Timestamp dateCreate;

    public CommentDTO() {

    }

    public CommentDTO(Long id, Long imageId, String displayName, String content, Timestamp dateCreate) {
        this.id = id;
        this.userProfileDTO = new UserProfileDTO(imageId, displayName);
        this.content = content;
        this.dateCreate = dateCreate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(Timestamp dateCreate) {
        this.dateCreate = dateCreate;
    }

    public UserProfileDTO getUserProfileDTO() {
        return userProfileDTO;
    }

    public void setUserProfileDTO(UserProfileDTO userProfileDTO) {
        this.userProfileDTO = userProfileDTO;
    }
}