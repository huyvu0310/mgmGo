package com.mgmtp.internship.experiences.repositories;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import static com.mgmtp.internship.experiences.model.tables.tables.ReportActivity.REPORT_ACTIVITY;

/**
 * Repository for report.
 *
 * @author vhduong
 */

@Component
public class ReportRepository {
    @Autowired
    private DSLContext dslContext;

    public boolean insertReport(long activityId, long userId) {
        return dslContext.insertInto(REPORT_ACTIVITY, REPORT_ACTIVITY.ACTIVITY_ID, REPORT_ACTIVITY.USER_ID)
                .values(activityId, userId).execute() > 0;
    }

    public boolean checkReportedByUser(long activityId, long userId) {
        return dslContext.selectFrom(REPORT_ACTIVITY)
                .where(REPORT_ACTIVITY.USER_ID.eq(userId)).and(REPORT_ACTIVITY.ACTIVITY_ID.eq(activityId))
                .execute() > 0;
    }

    public int countReportOfActivity(long activityId){
        return dslContext.selectCount().from(REPORT_ACTIVITY)
                .where(REPORT_ACTIVITY.ACTIVITY_ID.eq(activityId))
                .fetchOne(0, Integer.class);
    }
}
