package com.mgmtp.internship.experiences.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

/**
 * Activity detail.
 *
 * @author httbui.ext
 */
public class ActivityDetailDTO {

    private long id;

    @NotBlank(message = "Name may not be blank")
    @Size(max = 100, message = "You can not write more than 100 characters for name")
    private String name;

    @NotBlank(message = "Description may not be blank")
    @Size(max = 100000, message = "You can not write more than 10000 characters for description")
    private String description;
    private String address;
    private List<Long> images;
    private double rating;
    private long createdByUserId;
    private long updatedByUserId;
    private Timestamp createdDate;
    private Timestamp updatedDate;
    private Timestamp activeDate;
    private boolean isFavorite;

    private List<TagDTO> tags;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Long> getImages() {
        return images;
    }

    public void setImages(List<Long> images) {
        this.images = images;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public long getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(long createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public long getUpdatedByUserId() {
        return updatedByUserId;
    }

    public void setUpdatedByUserId(long updatedByUserId) {
        this.updatedByUserId = updatedByUserId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Timestamp getCreatedDate() { return createdDate; }

    public void setCreatedDate(Timestamp createdDate) { this.createdDate = createdDate; }

    public Timestamp getUpdatedDate() { return updatedDate; }

    public void setUpdatedDate(Timestamp updatedDate) { this.updatedDate = updatedDate; }

    public Timestamp getActiveDate() { return activeDate; }

    public void setActiveDate(Timestamp activeDate) { this.activeDate = activeDate; }
    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public List<TagDTO> getTags() {
        return tags;
    }

    public void setTags(List<TagDTO> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivityDetailDTO that = (ActivityDetailDTO) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
