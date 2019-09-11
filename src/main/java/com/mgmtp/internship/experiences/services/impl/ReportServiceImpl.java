package com.mgmtp.internship.experiences.services.impl;

import com.mgmtp.internship.experiences.repositories.ReportRepository;
import com.mgmtp.internship.experiences.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service report.
 *
 * @author vhduong
 */

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Override
    public boolean insertReport(long activityId, long userId) {
        return reportRepository.insertReport(activityId, userId);
    }

    @Override
    public boolean checkReportedByUser(long activityId, long userId) {
        return reportRepository.checkReportedByUser(activityId, userId);
    }

    @Override
    public int countReportOfActivity(long activityId) {
        return reportRepository.countReportOfActivity(activityId);
    }
}
