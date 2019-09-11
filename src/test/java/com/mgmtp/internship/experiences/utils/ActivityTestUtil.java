package com.mgmtp.internship.experiences.utils;

import com.mgmtp.internship.experiences.dto.ActivityDetailDTO;

public class ActivityTestUtil {
    private static final long ACTIVITY_ID = 1L;
    private static final String ACTIVITY_DES = "des";
    private static final String ACTIVITY_ADDR = "address";

    private static final int CREATED_BY_USER_ID = 1;
    private static final int UPDATED_BY_USER_ID = 1;
    private static final double RATING = 2.5;

    private ActivityTestUtil() {

    }

    public static ActivityDetailDTO prepareExpectedActivityDetailDTOWithNameForTest(String activityName) {
        ActivityDetailDTO activityDetailDTO = new ActivityDetailDTO();
        activityDetailDTO.setId(ACTIVITY_ID);
        activityDetailDTO.setName(activityName);
        activityDetailDTO.setDescription(ACTIVITY_DES);
        activityDetailDTO.setAddress(ACTIVITY_ADDR);
        activityDetailDTO.setRating(RATING);
        activityDetailDTO.setCreatedByUserId(CREATED_BY_USER_ID);
        activityDetailDTO.setUpdatedByUserId(UPDATED_BY_USER_ID);
        return activityDetailDTO;
    }
}
