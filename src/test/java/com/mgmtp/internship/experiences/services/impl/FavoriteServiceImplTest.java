package com.mgmtp.internship.experiences.services.impl;

import com.mgmtp.internship.experiences.dto.ActivityDTO;
import com.mgmtp.internship.experiences.repositories.FavoriteRepository;
import org.jooq.exception.DataAccessException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Unit test for favorite service.
 *
 * @author thuynh
 */
@RunWith(MockitoJUnitRunner.class)
public class FavoriteServiceImplTest {

    private static final long ACTIVITY_ID = 1;
    private static final long USER_ID = 1;
    private static final List<ActivityDTO> EXPECTED_LIST_FAVORITE_ACTIVITY_DTO = Collections.singletonList(new ActivityDTO(1L, "name", new ArrayList<>(), Collections.emptyList()));
    private static final int CURRENT_PAGE = 1;

    @Mock
    private FavoriteRepository favoriteRepository;

    @InjectMocks
    private FavoriteServiceImpl favoriteService;

    @Test
    public void shouldReturnListFavoriteActivities() {
        when(favoriteRepository.getFavoriteActivitiesByUserId(USER_ID, CURRENT_PAGE)).thenReturn(EXPECTED_LIST_FAVORITE_ACTIVITY_DTO);

        List<ActivityDTO> actualFavoriteActivityDTO = favoriteService.getFavoriteActivitiesByUserId(USER_ID, CURRENT_PAGE);

        assertEquals(EXPECTED_LIST_FAVORITE_ACTIVITY_DTO, actualFavoriteActivityDTO);
    }

    @Test
    public void shouldReturnEmptyListFavoriteActivitiesIfCurrentPageInCorrect() {
        List<ActivityDTO> expectedFavoriteActivityDTO = Collections.emptyList();

        List<ActivityDTO> actualFavoriteActivityDTO = favoriteService.getFavoriteActivitiesByUserId(USER_ID, -1);

        assertEquals(expectedFavoriteActivityDTO, actualFavoriteActivityDTO);
    }

    @Test
    public void shouldReturnEmptyListFavoriteActivitiesIfUserIdInCorrect() {
        List<ActivityDTO> expectedFavoriteActivityDTO = Collections.emptyList();
        when(favoriteRepository.getFavoriteActivitiesByUserId(-1, CURRENT_PAGE)).thenReturn(expectedFavoriteActivityDTO);

        List<ActivityDTO> actualFavoriteActivityDTO = favoriteService.getFavoriteActivitiesByUserId(-1, CURRENT_PAGE);

        assertEquals(expectedFavoriteActivityDTO, actualFavoriteActivityDTO);
    }

    @Test
    public void shouldReturnTotalRecordListFavoriteActivities() {
        int expectedTotalRecord = 10;
        when(favoriteRepository.countTotalRecord(USER_ID)).thenReturn(expectedTotalRecord);

        int actualTotalRecord = favoriteService.countTotalRecord(USER_ID);

        assertEquals(expectedTotalRecord, actualTotalRecord);
    }

    @Test
    public void shouldReturnZeroRecordIfUserIdIncorrect() {
        int expectedTotalRecord = 0;
        when(favoriteRepository.countTotalRecord(-1)).thenReturn(expectedTotalRecord);

        int actualTotalRecord = favoriteService.countTotalRecord(-1);

        assertEquals(expectedTotalRecord, actualTotalRecord);
    }

    @Test
    public void shouldReturnTrueIfActivityIsFavoriteOfUser() {
        boolean expectedResult = true;
        when(favoriteRepository.checkFavorite(ACTIVITY_ID, USER_ID)).thenReturn(expectedResult);

        boolean actualResult = favoriteService.checkFavorite(ACTIVITY_ID, USER_ID);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void shouldReturnFalseIfActivityIsNotFavoriteOfUser() {
        boolean expectedResult = false;
        when(favoriteRepository.checkFavorite(ACTIVITY_ID, USER_ID)).thenReturn(expectedResult);

        boolean actualResult = favoriteService.checkFavorite(ACTIVITY_ID, USER_ID);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void shouldReturnTrueIfWantToActivityIsNotFavoriteOfUserAndUpdateFavoriteSuccess() {
        boolean expectedResult = true;
        when(favoriteRepository.deleteFavorite(ACTIVITY_ID, USER_ID)).thenReturn(1);

        boolean actualResult = favoriteService.updateFavorite(ACTIVITY_ID, USER_ID, false);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void shouldReturnTrueIfWantToActivityIsFavoriteOfUserAndUpdateFavoriteSuccess() {
        boolean expectedResult = true;
        when(favoriteRepository.addFavorite(ACTIVITY_ID, USER_ID)).thenReturn(1);

        boolean actualResult = favoriteService.updateFavorite(ACTIVITY_ID, USER_ID, true);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void shouldReturnFalseIfWantToActivityIsNotFavoriteOfUserAndUpdateFavoriteFail() {
        boolean expectedResult = false;
        when(favoriteRepository.deleteFavorite(ACTIVITY_ID, USER_ID)).thenReturn(0);

        boolean actualResult = favoriteService.updateFavorite(ACTIVITY_ID, USER_ID, false);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void shouldReturnFalseIfWantToActivityIsFavoriteOfUserAndUpdateFavoriteFail() {
        boolean expectedResult = false;
        when(favoriteRepository.addFavorite(ACTIVITY_ID, USER_ID)).thenReturn(0);

        boolean actualResult = favoriteService.updateFavorite(ACTIVITY_ID, USER_ID, true);

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void shouldReturnFalseIfUpdateFavoriteFail() {
        boolean expectedResult = false;
        when(favoriteRepository.addFavorite(-1, USER_ID)).thenThrow(DataAccessException.class);

        boolean actualResult = favoriteService.updateFavorite(-1, USER_ID, true);

        assertEquals(expectedResult, actualResult);
    }
}
