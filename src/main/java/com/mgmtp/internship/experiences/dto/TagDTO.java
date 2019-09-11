package com.mgmtp.internship.experiences.dto;

import javax.validation.constraints.NotBlank;

public class TagDTO {
    private long id;

    @NotBlank(message = "Tag can not be blank")
    private String content;

    public TagDTO() {

    }

    public TagDTO(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
