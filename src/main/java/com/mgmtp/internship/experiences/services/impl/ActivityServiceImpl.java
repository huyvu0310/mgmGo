package com.mgmtp.internship.experiences.services.impl;

import com.mgmtp.internship.experiences.constants.EnumSort;
import com.mgmtp.internship.experiences.dto.ActivityDTO;
import com.mgmtp.internship.experiences.dto.ActivityDetailDTO;
import com.mgmtp.internship.experiences.dto.CommentDTO;
import com.mgmtp.internship.experiences.repositories.ActivityRepository;
import com.mgmtp.internship.experiences.services.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Activity service for Activity DTO.
 *
 * @author thuynh
 */
@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Override
    public ActivityDetailDTO findById(long activityId) {
        return activityRepository.findById(activityId);
    }

    @Override
    public int update(ActivityDetailDTO activityDetailDTO) {
        return activityRepository.update(activityDetailDTO);
    }

    @Override
    public Long create(ActivityDetailDTO activityDetailDTO) {
        return activityRepository.create(activityDetailDTO);
    }

    @Override
    public ActivityDetailDTO checkExistNameForCreate(String activityName) {
        return activityRepository.findByName(activityName);
    }

    @Override
    public ActivityDetailDTO checkExistNameForUpdate(long activityId, String activityName) {
        ActivityDetailDTO existedActivity = activityRepository.findByName(activityName);
        if (existedActivity != null && existedActivity.getId() == activityId) {
            return null;
        }
        return existedActivity;
    }

    @Override
    public List<ActivityDTO> search(String text, int currentPage, EnumSort sortType, List<String> filterTags) {
        if (currentPage < 1) {
            return Collections.emptyList();
        }
        return activityRepository.search(text, currentPage, sortType, filterTags);
    }

    @Override
    public int countTotalRecordSearch(String text, List<String> filterTags) {
        return activityRepository.countTotalRecordSearch(text, filterTags);
    }

    @Override
    public List<ActivityDTO> getActivities(int currentPage, EnumSort sortType, List<String> filterTags) {
        if (currentPage < 1) {
            return Collections.emptyList();
        }
        return activityRepository.getActivities(currentPage, sortType, filterTags);
    }

    @Override
    public int countTotalRecordActivity(List<String> filterTags) {
        return activityRepository.countTotalRecordActivity(filterTags);
    }

    @Override
    public List<ActivityDTO> getListActivityByUserId(long id, int currentPage) {
        List<ActivityDTO> activityDTOList = activityRepository.getListActivityByUserId(id, currentPage);
        if (activityDTOList.isEmpty() || currentPage < 1) {
            return Collections.emptyList();
        }
        return activityDTOList;
    }

    @Override
    public long getIdActivity(String name) {
        return activityRepository.getIdActivity(name);
    }

    @Override
    public int updatedActiveDate(Long activityId) {
        return activityRepository.updatedActiveDate(activityId);
    }

    @Override
    public int countTotalRecordActivitybyUserId(long id) {
        return activityRepository.countTotalRecordActivitybyUserId(id);
    }

    @Override
    public int deleteActivity(long activityId) {
        return activityRepository.deleteActivity(activityId);
    }

    @Override
    public List<CommentDTO> getAllCommentById(long activityID) {
        return activityRepository.getAllCommentById(activityID);
    }

    @Override
    public int addComment(CommentDTO commentDTO, long activityId, long userId) {
        return activityRepository.addComment(commentDTO, activityId, userId);
    }

    @Override
    public boolean checkIsActivityCreateByUserId(long activityId, long userId) {
        return activityRepository.checkIsActivityCreateByUserId(activityId, userId);
    }

    @Override
    public boolean checkExistedCommentOfUserByInActivity(long userId, long activityId) {
        return activityRepository.checkExistedCommentOfUserInActivity(userId, activityId);
    }
    @Override
    public List<CommentDTO> getComments(int currentPage, long activityId) {
        return activityRepository.getComments(currentPage, activityId);
    }

    @Override
    public int countTotalRecordCommentById(long activityId) {
        return activityRepository.countTotalRecordCommentById(activityId);
    }
}