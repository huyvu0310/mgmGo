package com.mgmtp.internship.experiences.repositories;

import com.mgmtp.internship.experiences.model.tables.tables.Rating;
import com.mgmtp.internship.experiences.model.tables.tables.records.RatingRecord;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.mgmtp.internship.experiences.model.tables.Tables.RATING;
import static org.jooq.impl.DSL.avg;
import static org.jooq.impl.DSL.round;

/**
 * Rating Repository.
 *
 * @author thuynh
 */
@Component
public class RatingRepository {

    @Autowired
    private DSLContext dslContext;

    public double getRate(long activityId) {
        Record1<BigDecimal> averageRating = dslContext.select(round(avg(Rating.RATING.VALUE), 1).as("rating"))
                .from(RATING)
                .where(RATING.ACTIVITY_ID.eq(activityId))
                .groupBy(RATING.ACTIVITY_ID).fetchOne();

        return averageRating == null ? 0 : averageRating.into(Double.class);
    }

    public int getRateByUserId(long activityId, long userId) {
        RatingRecord rate = dslContext.selectFrom(RATING)
                .where(RATING.ACTIVITY_ID.eq(activityId))
                .and(RATING.USER_ID.eq(userId)).fetchOne();

        return rate == null ? 0 : rate.map(rateRecord -> rate.getValue());
    }

    public int editRateByUserId(long activityId, long userId, int rate) {
        return updateRate(activityId, userId, rate) == 1 ? 1 : insertRate(activityId, userId, rate);
    }

    public int updateRate(long activityId, long userId, int rate) {
        return dslContext.update(RATING)
                .set(RATING.VALUE, rate)
                .where(RATING.ACTIVITY_ID.eq(activityId))
                .and(RATING.USER_ID.eq(userId))
                .execute();
    }

    public int insertRate(long activityId, long userId, int rate) {
        return dslContext.insertInto(RATING, RATING.ACTIVITY_ID, RATING.USER_ID, RATING.VALUE)
                .values(activityId, userId, rate)
                .execute();
    }
}
