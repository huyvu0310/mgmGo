package com.mgmtp.internship.experiences.services;

/**
 * Report Service interface.
 *
 * @author vhduong
 */

public interface ReportService {
    boolean insertReport(long activityId, long userId);

    boolean checkReportedByUser(long activityId, long userId);

    int countReportOfActivity(long activityId);
}
