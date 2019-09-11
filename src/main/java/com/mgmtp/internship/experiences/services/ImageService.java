package com.mgmtp.internship.experiences.services;

import com.mgmtp.internship.experiences.dto.ImageDTO;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


/**
 * Activity Service interface.
 *
 * @author htnguyen
 */
public interface ImageService {

    ImageDTO findImageById(long imageId);

    Long insertImage(byte[] imageData);

    @Transactional
    Long updateUserImage(long userId, Long oldImageId, byte[] data);

    boolean validateProfilePicture(InputStream inputStream) throws IOException;

    Long updateActivityImage(long activityId, long oldImageId, byte[] imageData);

    List<Long> getListImages(Long activityId);

    Long addActivityImage(long activity, byte[] imageData);

    Long getSizeOfListImages(Long activityId);

    Long checkMaximumImagesOfActivity(Long activityId);
}
