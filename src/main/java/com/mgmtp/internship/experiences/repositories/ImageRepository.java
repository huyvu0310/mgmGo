package com.mgmtp.internship.experiences.repositories;

import com.mgmtp.internship.experiences.dto.ImageDTO;
import com.mgmtp.internship.experiences.model.tables.tables.records.ImageRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

import static com.mgmtp.internship.experiences.model.tables.tables.Activity.ACTIVITY;
import static com.mgmtp.internship.experiences.model.tables.tables.ActivityImage.ACTIVITY_IMAGE;
import static com.mgmtp.internship.experiences.model.tables.tables.Image.IMAGE;

/**
 * Activity Service interface.
 *
 * @author htnguyen
 */

@Component
public class ImageRepository {

    @Autowired
    private DSLContext dslContext;

    public ImageDTO findImageById(long imageId) {
        ImageRecord image = dslContext
                .selectFrom(IMAGE)
                .where(IMAGE.ID.eq(imageId))
                .fetchOne();

        if (image == null) {
            return null;
        }

        return image.into(ImageDTO.class);
    }

    @Transactional
    public Long updateActivityImage(long activityId, long oldImageId, byte[] newImageData) {
        Long newImageId = insert(newImageData);
        int result = updateImage(activityId, newImageId, oldImageId);
        if (result == 0) {
            throw new RuntimeException();
        }
        int isDeleted = deleteImage(oldImageId);
        if (isDeleted == 0) {
            throw new RuntimeException();
        }
        return newImageId;
    }


    public int updateImage(long activityId, long newImageId, long oldImageId) {
        return dslContext
                .update(ACTIVITY_IMAGE)
                .set(ACTIVITY_IMAGE.IMAGE_ID, newImageId)
                .where(ACTIVITY_IMAGE.ACTIVITY_ID.eq(activityId))
                .and(ACTIVITY_IMAGE.IMAGE_ID.eq(oldImageId))
                .execute();
    }

    public Long addActivityImage(long activityId, byte[] imageData) {
        long imageId = insert(imageData);
        try {
            return dslContext
                    .insertInto(ACTIVITY_IMAGE, ACTIVITY_IMAGE.ACTIVITY_ID, ACTIVITY_IMAGE.IMAGE_ID)
                    .values(activityId, imageId)
                    .returning(ACTIVITY_IMAGE.IMAGE_ID)
                    .fetchOne()
                    .getValue(ACTIVITY_IMAGE.IMAGE_ID);
        } catch (UncategorizedSQLException | DataIntegrityViolationException e) {
            return null;
        }
    }

    public Long insert(byte[] imageData) {
        return dslContext
                .insertInto(IMAGE, IMAGE.IMAGE_DATA)
                .values(imageData)
                .returning(IMAGE.ID)
                .fetchOne().getId();
    }

    public int deleteImage(Long oldImageId) {
        return dslContext
                .deleteFrom(IMAGE)
                .where(IMAGE.ID.eq(oldImageId))
                .execute();
    }

    public List<Long> getListImages(Long activityId) {
        return dslContext
                .select(ACTIVITY_IMAGE.IMAGE_ID)
                .from(ACTIVITY).join(ACTIVITY_IMAGE)
                .on(ACTIVITY.ID.eq(ACTIVITY_IMAGE.ACTIVITY_ID))
                .where(ACTIVITY.ID.eq(activityId))
                .orderBy(ACTIVITY_IMAGE.IMAGE_ID.asc())
                .fetchInto(Long.class);
    }
}
