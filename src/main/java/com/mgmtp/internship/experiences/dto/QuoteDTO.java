package com.mgmtp.internship.experiences.dto;

/**
 * Quote DTO.
 *
 * @author thuynh
 */
public class QuoteDTO {
    private String content;
    private String title;

    public QuoteDTO() {

    }

    public QuoteDTO(String content, String title) {
        this.content = content;
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
