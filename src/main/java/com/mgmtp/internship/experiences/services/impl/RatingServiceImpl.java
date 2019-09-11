package com.mgmtp.internship.experiences.services.impl;

import com.mgmtp.internship.experiences.repositories.RatingRepository;
import com.mgmtp.internship.experiences.services.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

/**
 * Rating service.
 *
 * @author thuynh
 */
@Service
public class RatingServiceImpl implements RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Override
    public double getRate(long activityId) {
        return ratingRepository.getRate(activityId);
    }

    @Override
    public int getRateByUserId(long activityId, long userId) {
        return ratingRepository.getRateByUserId(activityId, userId);
    }

    @Override
    public int editRateByUserId(long activityId, long userId, int rating) {
        try {
            return ratingRepository.editRateByUserId(activityId, userId, rating);
        } catch (DataIntegrityViolationException e) {
            return 0;
        }
    }
}
