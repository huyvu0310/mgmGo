package com.mgmtp.internship.experiences.services;

import com.mgmtp.internship.experiences.constants.EnumSort;
import com.mgmtp.internship.experiences.dto.ActivityDTO;
import com.mgmtp.internship.experiences.dto.ActivityDetailDTO;
import com.mgmtp.internship.experiences.dto.CommentDTO;

import java.util.List;

/**
 * Activity Service interface.
 *
 * @author thuynh
 */
public interface ActivityService {
    ActivityDetailDTO findById(long activityId);

    int update(ActivityDetailDTO activityDetailDTO);

    Long create(ActivityDetailDTO activityDetailDTO);

    ActivityDetailDTO checkExistNameForCreate(String activityName);

    ActivityDetailDTO checkExistNameForUpdate(long activityId, String activityName);

    List<ActivityDTO> search(String text, int currentPage, EnumSort sortType, List<String> filterTags);

    int countTotalRecordSearch(String text, List<String> filterTags);

    List<ActivityDTO> getActivities(int currentPage, EnumSort sortType, List<String> filterTags);

    int countTotalRecordActivity(List<String> filterTags);

    List<ActivityDTO> getListActivityByUserId(long id, int currentPage);

    long getIdActivity(String name);

    int updatedActiveDate(Long activityId);

    int countTotalRecordActivitybyUserId(long id);

    int deleteActivity(long activityId);

    List<CommentDTO> getAllCommentById(long activityID);

    int addComment(CommentDTO commentDTO, long activityId, long userId);

    boolean checkIsActivityCreateByUserId(long activityId, long userId);

    boolean checkExistedCommentOfUserByInActivity(long userId, long activityId);

    List<CommentDTO> getComments(int currentPage, long activityId);

    int countTotalRecordCommentById(long activityId);
}
