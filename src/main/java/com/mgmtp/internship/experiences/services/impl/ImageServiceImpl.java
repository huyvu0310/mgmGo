package com.mgmtp.internship.experiences.services.impl;

import com.mgmtp.internship.experiences.constants.ApplicationConstant;
import com.mgmtp.internship.experiences.dto.ImageDTO;
import com.mgmtp.internship.experiences.repositories.ImageRepository;
import com.mgmtp.internship.experiences.repositories.UserRepository;
import com.mgmtp.internship.experiences.services.ImageService;
import org.jooq.exception.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


/**
 * Activity Service interface.
 *
 * @author htnguyen
 */

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public ImageDTO findImageById(long imageId) {
        return imageRepository.findImageById(imageId);
    }

    @Override
    public Long updateActivityImage(long activityId, long oldImageId, byte[] imageData) {
        try {
            return imageRepository.updateActivityImage(activityId, oldImageId, imageData);
        } catch (RuntimeException e) {
            return 0L;
        }
    }


    public Long insertImage(byte[] imageData) {
        return imageRepository.insert(imageData);
    }

    @Override
    @Transactional
    public Long updateUserImage(long userId, Long oldImageId, byte[] data) {
        Long imageId = insertImage(data);
        if (imageId == null) throw new DataAccessException("Could not insert image.");
        userRepository.updateImage(userId, imageId);
        if (oldImageId != null) {
            imageRepository.deleteImage(oldImageId);
        }
        return imageId;
    }

    @Override
    public boolean validateProfilePicture(InputStream inputStream) throws IOException {
        BufferedImage img = ImageIO.read(inputStream);
        if (img == null) {
            throw new IOException("File is not an image");
        }
        return img.getWidth() <= 150 && img.getHeight() <= 150;
    }

    @Override
    public List<Long> getListImages(Long activityId) {
        return imageRepository.getListImages(activityId);
    }

    @Override
    public Long addActivityImage(long activity, byte[] imageData) {
        return imageRepository.addActivityImage(activity, imageData);
    }

    @Override
    public Long getSizeOfListImages(Long activityId) {
        return Long.valueOf(getListImages(activityId).size());
    }

    @Override
    public Long checkMaximumImagesOfActivity(Long activityId) {
        Long len = getSizeOfListImages(activityId);
        return len < ApplicationConstant.MAX_NUMBER_UPLOAD_IMAGES ? len : -1;
    }
}
