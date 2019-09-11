package com.mgmtp.internship.experiences.services.impl;

import com.mgmtp.internship.experiences.constants.EnumSort;
import com.mgmtp.internship.experiences.dto.ActivityDTO;
import com.mgmtp.internship.experiences.dto.ActivityDetailDTO;
import com.mgmtp.internship.experiences.dto.CommentDTO;
import com.mgmtp.internship.experiences.repositories.ActivityRepository;
import com.mgmtp.internship.experiences.utils.ActivityTestUtil;
import com.mgmtp.internship.experiences.utils.DateTimeUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Unit test for activity service.
 *
 * @author thuynh
 */
@RunWith(MockitoJUnitRunner.class)
public class ActivityServiceImplTest {
    private static final long USER_ID = 1L;
    private static final int ADD_SUCCESS = 1;
    private static final int ADD_FAIL = 0;
    private static final String EXIST_NAME = "new name";
    private static final long ACTIVITY_ID = 1L;
    private static final List<Long> IMAGES = new ArrayList<>();
    private static final ActivityDetailDTO EXPECTED_ACTIVITY_DETAIL_DTO = ActivityTestUtil.prepareExpectedActivityDetailDTOWithNameForTest("name");
    private static final ActivityDetailDTO EXISTED_ACTIVITY_DETAIL_DTO = ActivityTestUtil.prepareExpectedActivityDetailDTOWithNameForTest(EXIST_NAME);
    private static final String KEY_SEARCH = "abc";
    private static final List<ActivityDTO> EXPECTED_LIST_ACTIVITY_DTO = Collections.singletonList(new ActivityDTO(1L, "name", IMAGES, Collections.emptyList()));
    private static final List<CommentDTO> EXPECTED_LIST_COMMENT_DTO = Collections.singletonList(new CommentDTO(1L, 1L, "displayName", "content", DateTimeUtil.getCurrentDate()));
    private static final CommentDTO COMMENT_DTO = new CommentDTO(1L, 1L, "displayName", "content", DateTimeUtil.getCurrentDate());
    private static final int CURRENT_PAGE = 1;
    private static final String SORT_TYPE = "NEWEST_FIRST";
    private static final List<String> FILTER_TAGS = Collections.emptyList();
    private static final int EXPECTED_RECORD = 3;


    @Mock
    private ActivityRepository activityRepository;

    @InjectMocks
    private ActivityServiceImpl activityService;

    @Test
    public void shouldReturnActivityById() {
        Mockito.when(activityRepository.findById(ACTIVITY_ID)).thenReturn(EXPECTED_ACTIVITY_DETAIL_DTO);

        ActivityDetailDTO actualActivityDetailDTO = activityService.findById(ACTIVITY_ID);

        Assert.assertEquals(EXPECTED_ACTIVITY_DETAIL_DTO, actualActivityDetailDTO);
    }

    @Test
    public void shouldReturnNullIfActivityNotFound() {
        Mockito.when(activityRepository.findById(ACTIVITY_ID)).thenReturn(null);

        ActivityDetailDTO actualActivityDetailDTO = activityService.findById(ACTIVITY_ID);

        Assert.assertEquals(null, actualActivityDetailDTO);
    }

    @Test
    public void shouldReturn1IfUpdateSuccess() {
        final int UPDATE_SUCCESS = 1;
        Mockito.when(activityRepository.update(EXPECTED_ACTIVITY_DETAIL_DTO)).thenReturn(UPDATE_SUCCESS);

        Assert.assertEquals(UPDATE_SUCCESS, activityService.update(EXPECTED_ACTIVITY_DETAIL_DTO));
    }

    @Test
    public void shouldReturn0IfUpdateFailed() {
        final int UPDATE_FAIL = 0;
        EXPECTED_ACTIVITY_DETAIL_DTO.setUpdatedByUserId(1);
        Mockito.when(activityRepository.update(EXPECTED_ACTIVITY_DETAIL_DTO)).thenReturn(UPDATE_FAIL);

        Assert.assertEquals(UPDATE_FAIL, activityService.update(EXPECTED_ACTIVITY_DETAIL_DTO));
    }

    @Test
    public void shouldReturn1IfInsertSuccess() {
        final Long INSERT_SUCCESS = 1L;
        Mockito.when(activityRepository.create(EXPECTED_ACTIVITY_DETAIL_DTO)).thenReturn(INSERT_SUCCESS);

        Assert.assertEquals(INSERT_SUCCESS, activityService.create(EXPECTED_ACTIVITY_DETAIL_DTO));
    }

    @Test
    public void shouldReturn0IfInsertFailed() {
        final Long INSERT_SUCCESS = 0L;
        Mockito.when(activityRepository.create(EXPECTED_ACTIVITY_DETAIL_DTO)).thenReturn(INSERT_SUCCESS);

        Assert.assertEquals(INSERT_SUCCESS, activityService.create(EXPECTED_ACTIVITY_DETAIL_DTO));
    }

    @Test
    public void shouldReturnExistedActivityIfNameExistWhenCreate() {
        Mockito.when(activityRepository.findByName(EXIST_NAME)).thenReturn(EXISTED_ACTIVITY_DETAIL_DTO);

        Assert.assertEquals(EXISTED_ACTIVITY_DETAIL_DTO, activityService.checkExistNameForCreate(EXIST_NAME));
    }

    @Test
    public void shouldReturnNullIfNameNotExistWhenCreate() {
        Mockito.when(activityRepository.findByName(EXPECTED_ACTIVITY_DETAIL_DTO.getName())).thenReturn(null);

        Assert.assertEquals(null, activityService.checkExistNameForCreate(EXPECTED_ACTIVITY_DETAIL_DTO.getName()));
    }

    @Test
    public void shouldReturnExistedActivityIfNameExistWhenUpdate() {
        Mockito.when(activityRepository.findByName(EXIST_NAME)).thenReturn(EXISTED_ACTIVITY_DETAIL_DTO);
        Assert.assertEquals(EXISTED_ACTIVITY_DETAIL_DTO, activityService.checkExistNameForUpdate(2L, EXIST_NAME));
    }

    @Test
    public void shouldReturnNullIfNameNotExistWhenUpdate() {
        Mockito.when(activityRepository.findByName(EXPECTED_ACTIVITY_DETAIL_DTO.getName())).thenReturn(null);

        Assert.assertEquals(null, activityService.checkExistNameForUpdate(EXPECTED_ACTIVITY_DETAIL_DTO.getId(), EXPECTED_ACTIVITY_DETAIL_DTO.getName()));
    }

    @Test
    public void shouldReturnListActivitiesWhenKeySearchCorrect() {
        Mockito.when(activityRepository.search(KEY_SEARCH, CURRENT_PAGE, EnumSort.valueOf(SORT_TYPE), FILTER_TAGS)).thenReturn(EXPECTED_LIST_ACTIVITY_DTO);

        List<ActivityDTO> actualListActivityDTO = activityService.search(KEY_SEARCH, CURRENT_PAGE, EnumSort.valueOf(SORT_TYPE), FILTER_TAGS);

        Assert.assertEquals(EXPECTED_LIST_ACTIVITY_DTO, actualListActivityDTO);
    }

    @Test
    public void shouldReturnNullWhenKeySearchIncorrect() {
        Mockito.when(activityRepository.search(KEY_SEARCH, CURRENT_PAGE, EnumSort.valueOf(SORT_TYPE), FILTER_TAGS)).thenReturn(null);

        List<ActivityDTO> actualListActivityDTO = activityService.search(KEY_SEARCH, CURRENT_PAGE, EnumSort.valueOf(SORT_TYPE), FILTER_TAGS);

        Assert.assertEquals(null, actualListActivityDTO);
    }

    @Test
    public void shouldReturnTotalRecordActivitiesIfKeySearchCorrect() {
        int expectedRecord = 3;
        Mockito.when(activityRepository.countTotalRecordSearch(KEY_SEARCH, FILTER_TAGS)).thenReturn(expectedRecord);

        int actualRecord = activityService.countTotalRecordSearch(KEY_SEARCH, FILTER_TAGS);

        Assert.assertEquals(expectedRecord, actualRecord);
    }

    @Test
    public void shouldReturnZeroRecordActivitiesIfKeySearchIncorrect() {
        int expectedRecord = 0;
        Mockito.when(activityRepository.countTotalRecordSearch(KEY_SEARCH, FILTER_TAGS)).thenReturn(expectedRecord);

        int actualRecord = activityService.countTotalRecordSearch(KEY_SEARCH, FILTER_TAGS);

        Assert.assertEquals(expectedRecord, actualRecord);
    }

    @Test
    public void shouldReturnActivitiesOfPage() {
        Mockito.when(activityRepository.getActivities(CURRENT_PAGE, EnumSort.valueOf(SORT_TYPE), FILTER_TAGS)).thenReturn(EXPECTED_LIST_ACTIVITY_DTO);

        List<ActivityDTO> actualActivities = activityService.getActivities(CURRENT_PAGE, EnumSort.valueOf(SORT_TYPE), FILTER_TAGS);

        Assert.assertEquals(EXPECTED_LIST_ACTIVITY_DTO, actualActivities);
    }

    @Test
    public void shouldReturnEmptyListActivitiesIfPageIncorrect() {
        List<ActivityDTO> expectedActivities = Collections.emptyList();

        List<ActivityDTO> actualActivities = activityService.getActivities(-1, EnumSort.valueOf(SORT_TYPE), FILTER_TAGS);

        Assert.assertEquals(expectedActivities, actualActivities);
    }

    @Test
    public void shouldReturnTotalRecordOfActivities() {
        Mockito.when(activityRepository.countTotalRecordActivity(FILTER_TAGS)).thenReturn(EXPECTED_RECORD);

        int actualPageSize = activityService.countTotalRecordActivity(FILTER_TAGS);

        Assert.assertEquals(EXPECTED_RECORD, actualPageSize);
    }

    @Test
    public void shouldReturnZeroRecordIfHaveNotActivity() {
        int expectedRecord = 0;
        Mockito.when(activityRepository.countTotalRecordActivity(FILTER_TAGS)).thenReturn(expectedRecord);

        int actualPageSize = activityService.countTotalRecordActivity(FILTER_TAGS);

        Assert.assertEquals(expectedRecord, actualPageSize);
    }

    @Test
    public void shouldReturnActivityIdIfGetByName() {
        Mockito.when(activityRepository.getIdActivity(EXPECTED_ACTIVITY_DETAIL_DTO.getName())).thenReturn(ACTIVITY_ID);

        Assert.assertEquals(ACTIVITY_ID, activityService.getIdActivity(EXPECTED_ACTIVITY_DETAIL_DTO.getName()));
    }

    @Test
    public void shouldReturnListActivityIfGetByUserId() {
        Mockito.when(activityRepository.getListActivityByUserId(USER_ID, CURRENT_PAGE)).thenReturn(EXPECTED_LIST_ACTIVITY_DTO);

        List<ActivityDTO> actualListActivityDTO = activityService.getListActivityByUserId(USER_ID, CURRENT_PAGE);

        Assert.assertEquals(EXPECTED_LIST_ACTIVITY_DTO, actualListActivityDTO);
    }

    @Test
    public void shouldReturnEmptyListIfCurrentPageLessThanOne() {
        Mockito.when(activityRepository.getListActivityByUserId(USER_ID, 0)).thenReturn(EXPECTED_LIST_ACTIVITY_DTO);

        Assert.assertEquals(0, activityService.getListActivityByUserId(USER_ID, 0).size());
    }

    @Test
    public void shouldReturnEmptyListIfNotFindUserId() {
        Mockito.when(activityRepository.getListActivityByUserId(USER_ID, CURRENT_PAGE)).thenReturn(Collections.emptyList());

        Assert.assertEquals(0, activityService.getListActivityByUserId(USER_ID, 1).size());
    }

    @Test
    public void shouldReturnZeroRecordIfHaveNoActivityByUserId() {
        Mockito.when(activityRepository.countTotalRecordActivitybyUserId(USER_ID)).thenReturn(0);

        int actualResult = activityService.countTotalRecordActivitybyUserId(USER_ID);

        Assert.assertEquals(0, actualResult);
    }

    @Test
    public void shouldReturnRecordIfHaveActivitiesByUserId() {
        Mockito.when(activityRepository.countTotalRecordActivitybyUserId(USER_ID)).thenReturn(EXPECTED_RECORD);

        int actualResult = activityService.countTotalRecordActivitybyUserId(USER_ID);

        Assert.assertEquals(EXPECTED_RECORD, actualResult);
    }

    @Test
    public void shouldReturn0IfDeleteActivityFail() {
        int deleteSuccess = 0;
        Mockito.when(activityRepository.deleteActivity(ACTIVITY_ID)).thenReturn(deleteSuccess);

        Assert.assertEquals(deleteSuccess, activityService.deleteActivity(ACTIVITY_ID));
    }

    @Test
    public void shouldReturn1IfDeleteActivityFail() {
        int deleteFailed = 0;
        Mockito.when(activityRepository.deleteActivity(ACTIVITY_ID)).thenReturn(deleteFailed);

        Assert.assertEquals(deleteFailed, activityService.deleteActivity(ACTIVITY_ID));
    }

    @Test
    public void shouldReturnAllCommentByActivityId() {
        Mockito.when(activityRepository.getAllCommentById(ACTIVITY_ID)).thenReturn(EXPECTED_LIST_COMMENT_DTO);

        List<CommentDTO> actualListCommentDTO = activityService.getAllCommentById(ACTIVITY_ID);

        Assert.assertEquals(EXPECTED_LIST_COMMENT_DTO, actualListCommentDTO);
    }

    @Test
    public void shouldReturnEmptyListCommentByActivityId() {
        Mockito.when(activityRepository.getAllCommentById(ACTIVITY_ID)).thenReturn(Collections.emptyList());

        List<CommentDTO> actualListCommentDTO = activityService.getAllCommentById(ACTIVITY_ID);

        Assert.assertEquals(Collections.emptyList(), actualListCommentDTO);
    }

    @Test
    public void shouldReturn1IfAddCommentSuccess() {
        Mockito.when(activityRepository.addComment(COMMENT_DTO, ACTIVITY_ID, USER_ID)).thenReturn(ADD_SUCCESS);
        int actualResult = activityService.addComment(COMMENT_DTO, ACTIVITY_ID, USER_ID);

        Assert.assertEquals(ADD_SUCCESS, actualResult);
    }

    @Test
    public void shouldReturn0IfAddCommentFail() {
        Mockito.when(activityRepository.addComment(COMMENT_DTO, ACTIVITY_ID, USER_ID)).thenReturn(ADD_FAIL);

        int actualResult = activityService.addComment(COMMENT_DTO, ACTIVITY_ID, USER_ID);

        Assert.assertEquals(ADD_FAIL, actualResult);
    }

    @Test
    public void shouldReturnTrueIfActivityIsCreateByUserId() {
        Mockito.when(activityRepository.checkIsActivityCreateByUserId(ACTIVITY_ID, USER_ID)).thenReturn(true);

        boolean actualResult = activityService.checkIsActivityCreateByUserId(ACTIVITY_ID, USER_ID);

        Assert.assertEquals(true, actualResult);
    }

    @Test
    public void shouldReturnFalseIfActivityIsNotCreateByUserId() {
        long userIdCreateActivity = 2L;
        Mockito.when(activityRepository.checkIsActivityCreateByUserId(ACTIVITY_ID, userIdCreateActivity)).thenReturn(false);

        boolean actualResult = activityService.checkIsActivityCreateByUserId(ACTIVITY_ID, userIdCreateActivity);

        Assert.assertEquals(false, actualResult);
    }

    @Test
    public void shouldReturnTrueIfHaveCommented(){
        Mockito.when(activityRepository.checkExistedCommentOfUserInActivity(USER_ID, ACTIVITY_ID)).thenReturn(true);

        Boolean actualResult = activityService.checkExistedCommentOfUserByInActivity(USER_ID, ACTIVITY_ID);

        Assert.assertEquals(true, actualResult);
    }

    @Test
    public void shouldReturnFalseIfHaveNotCommented(){
        Mockito.when(activityRepository.checkExistedCommentOfUserInActivity(USER_ID, ACTIVITY_ID)).thenReturn(false);

        Boolean actualResult = activityService.checkExistedCommentOfUserByInActivity(USER_ID, ACTIVITY_ID);

        Assert.assertEquals(false, actualResult);
    }

    @Test
    public void shouldReturnCommentsOfPage() {
        Mockito.when(activityRepository.getComments(CURRENT_PAGE, ACTIVITY_ID)).thenReturn(EXPECTED_LIST_COMMENT_DTO);

        List<CommentDTO> actualComments = activityService.getComments(CURRENT_PAGE, ACTIVITY_ID);

        Assert.assertEquals(EXPECTED_LIST_COMMENT_DTO, actualComments);
    }

    @Test
    public void shouldReturnEmptyListCommentsIfPageIncorrect() {
        List<CommentDTO> expectedComments = Collections.emptyList();

        List<CommentDTO> actualComments = activityService.getComments(CURRENT_PAGE, ACTIVITY_ID);

        Assert.assertEquals(expectedComments, actualComments);
    }

    @Test
    public void shouldReturnTotalRecordOfComments() {
        Mockito.when(activityRepository.countTotalRecordCommentById(ACTIVITY_ID)).thenReturn(EXPECTED_RECORD);

        int actualPageSize = activityService.countTotalRecordCommentById(ACTIVITY_ID);

        Assert.assertEquals(EXPECTED_RECORD, actualPageSize);
    }
}
