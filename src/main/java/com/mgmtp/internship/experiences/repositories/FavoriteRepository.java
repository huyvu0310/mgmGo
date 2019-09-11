package com.mgmtp.internship.experiences.repositories;

import com.mgmtp.internship.experiences.dto.ActivityDTO;
import com.mgmtp.internship.experiences.model.tables.tables.ActivityImage;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mgmtp.internship.experiences.constants.ApplicationConstant.RECORD_OF_LIST;
import static com.mgmtp.internship.experiences.model.tables.Tables.FAVORITE;
import static com.mgmtp.internship.experiences.model.tables.tables.Activity.ACTIVITY;
import static com.mgmtp.internship.experiences.model.tables.tables.ActivityTag.ACTIVITY_TAG;
import static com.mgmtp.internship.experiences.model.tables.tables.Tag.TAG;

/**
 * Favorite repository.
 *
 * @author thuynh
 */

@Component
public class FavoriteRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(FavoriteRepository.class);

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private DSLContext dslContext;

    public List<ActivityDTO> getFavoriteActivitiesByUserId(long userId, int currentPage) {

        JdbcMapper mapper = JdbcMapperFactory.newInstance()
                .ignorePropertyNotFound()
                .addKeys(ActivityRepository.ID_PROPERTY, ActivityRepository.TAGS_ID_PROPERTY)
                .addKeys(ActivityRepository.ID_PROPERTY, ActivityRepository.IMAGE_ID_PROPERTY)
                .newMapper(ActivityDTO.class);

        try {
            ResultSet resultSet = getResultSet(userId, currentPage);
            Stream<ActivityDTO> stream = mapper.stream(resultSet);
            return stream.collect(Collectors.toList());
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
        return Collections.emptyList();

    }

    private ResultSet getResultSet(long userId, int currentPage) {
        Table activityImage = activityRepository.getFirstActivityImageTbl();

        Table table = dslContext
                .select(
                        ACTIVITY.ID.as(ActivityRepository.ID_PROPERTY),
                        ACTIVITY.NAME.as(ActivityRepository.NAME_PROPERTY),
                        ACTIVITY.ADDRESS.as(ActivityRepository.ADDRESS_PROPERTY),
                        activityImage.field(ActivityImage.ACTIVITY_IMAGE.IMAGE_ID).as(ActivityRepository.IMAGE_ID_PROPERTY)
                )
                .from(ACTIVITY)
                .join(FAVORITE)
                .on(FAVORITE.ACTIVITY_ID.eq(ACTIVITY.ID))
                .leftJoin(ACTIVITY_TAG)
                .on(ACTIVITY_TAG.ACTIVITY_ID.eq(ACTIVITY.ID))
                .leftJoin(TAG)
                .on(TAG.ID.eq(ACTIVITY_TAG.TAG_ID))
                .leftJoin(activityImage)
                .on(ACTIVITY.ID.eq(activityImage.field(ActivityImage.ACTIVITY_IMAGE.ACTIVITY_ID)))
                .where(FAVORITE.USER_ID.eq(userId))
                .groupBy(ACTIVITY.ID, ActivityImage.ACTIVITY_IMAGE.as(ActivityRepository.FIRST_ACTIVITY_IMAGE_TABLE).IMAGE_ID)
                .orderBy(ACTIVITY.ID.desc())
                .offset((currentPage - 1) * RECORD_OF_LIST)
                .limit(RECORD_OF_LIST).asTable();
        return dslContext
                .select(table.asterisk(),
                        TAG.ID.as(ActivityRepository.TAGS_ID_PROPERTY),
                        TAG.CONTENT.as(ActivityRepository.TAGS_CONTENT_PROPERTY))
                .from(table)
                .leftJoin(ACTIVITY_TAG)
                .on(table.field("id", Long.class).eq(ACTIVITY_TAG.ACTIVITY_ID))
                .leftJoin(TAG)
                .on(TAG.ID.eq(ACTIVITY_TAG.TAG_ID))
                .orderBy(DSL.field(ActivityRepository.ID_PROPERTY).desc())
                .fetchResultSet();
    }

    public int countTotalRecord(long userId) {
        return dslContext.selectCount()
                .from(FAVORITE)
                .where(FAVORITE.USER_ID.eq(userId))
                .fetchAny(0, Integer.class);
    }

    public boolean checkFavorite(long activityId, long userId) {
        return dslContext.fetchExists(FAVORITE, FAVORITE.ACTIVITY_ID.eq(activityId).and(FAVORITE.USER_ID.eq(userId)));
    }

    public int addFavorite(long activityId, long userId) {
        return dslContext.insertInto(FAVORITE, FAVORITE.ACTIVITY_ID, FAVORITE.USER_ID)
                .values(activityId, userId)
                .execute();
    }

    public int deleteFavorite(long activityId, long userId) {
        return dslContext.deleteFrom(FAVORITE)
                .where(FAVORITE.ACTIVITY_ID.eq(activityId))
                .and(FAVORITE.USER_ID.eq(userId))
                .execute();
    }
}
