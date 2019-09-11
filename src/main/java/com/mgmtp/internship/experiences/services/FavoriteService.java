package com.mgmtp.internship.experiences.services;

import com.mgmtp.internship.experiences.dto.ActivityDTO;

import java.util.List;

/**
 * Favorite service interface.
 *
 * @author thuynh
 */

public interface FavoriteService {
    List<ActivityDTO> getFavoriteActivitiesByUserId(long userId, int currentPage);

    int countTotalRecord(long userId);

    boolean checkFavorite(long activityId, long userId);

    boolean updateFavorite(long activityId, long userId, boolean isFavorite);
}
