package com.mgmtp.internship.experiences.services.impl;

import com.mgmtp.internship.experiences.repositories.RatingRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Unit test for rating service.
 *
 * @author thuynh
 */

@RunWith(MockitoJUnitRunner.class)
public class RatingServiceImplTest {

    private static final long ACTIVITY_ID = 1;
    private static final long USER_ID = 1;
    private static final int RATING = 5;
    private static final int UPDATE_SUCCESS = 1;

    @Mock
    private RatingRepository ratingRepository;

    @InjectMocks
    private RatingServiceImpl ratingService;

    @Test
    public void shouldReturnAverageRatingOfActivity() {
        double expectedRating = 3.3;
        Mockito.when(ratingRepository.getRate(ACTIVITY_ID)).thenReturn(expectedRating);

        double actualRating = ratingService.getRate(ACTIVITY_ID);

        Assert.assertEquals(expectedRating, actualRating, 0.01);
    }

    @Test
    public void shouldReturnZeroIfActivityNotFound() {
        double expectedRating = 0.0;
        Mockito.when(ratingRepository.getRate(ACTIVITY_ID)).thenReturn(expectedRating);

        double actualRating = ratingService.getRate(ACTIVITY_ID);

        Assert.assertEquals(expectedRating, actualRating, 0.01);
    }

    @Test
    public void shouldReturnRatingOfActivityByUserId() {
        int expectedRating = 5;
        Mockito.when(ratingRepository.getRateByUserId(ACTIVITY_ID, USER_ID)).thenReturn(expectedRating);

        int actualRating = ratingService.getRateByUserId(ACTIVITY_ID, USER_ID);

        Assert.assertEquals(expectedRating, actualRating);
    }

    @Test
    public void shouldReturnZeroIfActivityOrUserNotFound() {
        int expectedRating = 0;
        Mockito.when(ratingRepository.getRateByUserId(ACTIVITY_ID, USER_ID)).thenReturn(expectedRating);

        int actualRating = ratingService.getRateByUserId(ACTIVITY_ID, USER_ID);

        Assert.assertEquals(expectedRating, actualRating);
    }

    @Test
    public void shouldReturnOneIfUpdateRatingSucceeded() {
        Mockito.when(ratingRepository.editRateByUserId(ACTIVITY_ID, USER_ID, RATING)).thenReturn(UPDATE_SUCCESS);

        int actualRating = ratingService.editRateByUserId(ACTIVITY_ID, USER_ID, RATING);

        Assert.assertEquals(UPDATE_SUCCESS, actualRating);
    }

    @Test
    public void shouldNotReturnOneIfUpdateRatingFailed() {
        int failUpdate = 0;
        int invalidActivityId = -1;
        Mockito.when(ratingRepository.editRateByUserId(invalidActivityId, USER_ID, RATING)).thenReturn(failUpdate);

        int actualRating = ratingService.editRateByUserId(invalidActivityId, USER_ID, RATING);

        Assert.assertEquals(failUpdate, actualRating);
    }
}
