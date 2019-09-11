package com.mgmtp.internship.experiences.dto;

import java.util.List;

/**
 * Activity DTO.
 *
 * @author thuynh
 */
public class ActivityDTO {
    private long id;
    private String name;
    private List<Long> images;
    private List<TagDTO> tags;
    private String address;

    public ActivityDTO() {
    }

    public ActivityDTO(long id, String name, List<Long> images, List<TagDTO> tags) {
        this.id = id;
        this.name = name;
        this.images = images;
        this.tags = tags;
    }

    public ActivityDTO(long id, String name, List<Long> images, List<TagDTO> tags, String address) {
        this.id = id;
        this.name = name;
        this.images = images;
        this.tags = tags;
        this.address = address;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getImages() {
        return images;
    }

    public void setImages(List<Long> images) {
        this.images = images;
    }

    public List<TagDTO> getTags() {
        return tags;
    }

    public void setTags(List<TagDTO> tags) {
        this.tags = tags;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
