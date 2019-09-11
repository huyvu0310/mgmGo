package com.mgmtp.internship.experiences.dto;

/**
 * Activity Service interface.
 *
 * @author htnguyen
 */

public class ImageDTO {
    private long id;
    private byte[] image;

    public ImageDTO(long id, byte[] image) {
        this.id = id;
        this.image = image;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
