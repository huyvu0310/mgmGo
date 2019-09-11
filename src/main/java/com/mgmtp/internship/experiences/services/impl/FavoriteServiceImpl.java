package com.mgmtp.internship.experiences.services.impl;

import com.mgmtp.internship.experiences.dto.ActivityDTO;
import com.mgmtp.internship.experiences.repositories.FavoriteRepository;
import com.mgmtp.internship.experiences.services.FavoriteService;
import org.jooq.exception.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Favorite service.
 *
 * @author thuynh
 */

@Service
public class FavoriteServiceImpl implements FavoriteService {
    @Autowired
    private FavoriteRepository favoriteRepository;

    @Override
    public List<ActivityDTO> getFavoriteActivitiesByUserId(long userId, int currentPage) {
        if (currentPage < 1) {
            return Collections.emptyList();
        }
        return favoriteRepository.getFavoriteActivitiesByUserId(userId, currentPage);
    }

    @Override
    public int countTotalRecord(long userId) {
        return favoriteRepository.countTotalRecord(userId);
    }

    @Override
    public boolean checkFavorite(long activityId, long userId) {
        return favoriteRepository.checkFavorite(activityId, userId);
    }

    @Override
    public boolean updateFavorite(long activityId, long userId, boolean isFavorite) {
        try {
            if (isFavorite) {
                return favoriteRepository.addFavorite(activityId, userId) != 0;
            }
            return favoriteRepository.deleteFavorite(activityId, userId) != 0;
        } catch (DataAccessException e) {
            return false;
        }
    }
}
