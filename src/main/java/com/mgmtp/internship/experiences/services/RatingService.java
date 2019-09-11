package com.mgmtp.internship.experiences.services;

/**
 * Rating Service interface.
 *
 * @author thuynh
 */
public interface RatingService {
    double getRate(long activityId);
    int getRateByUserId(long activityId, long userId);
    int editRateByUserId(long activityId, long userId, int rating);
}
