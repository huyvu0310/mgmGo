package com.mgmtp.internship.experiences.repositories;

import com.mgmtp.internship.experiences.constants.ApplicationConstant;
import com.mgmtp.internship.experiences.dto.TagDTO;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.mgmtp.internship.experiences.model.tables.Tables.ACTIVITY_TAG;
import static com.mgmtp.internship.experiences.model.tables.Tables.TAG;

@Component
public class TagRepository {

    private static final int FAILED_TAG_ID = -1;

    @Autowired
    private DSLContext dslContext;

    public long insertTag(String content) {
        return dslContext.insertInto(TAG, TAG.CONTENT)
                .values(content)
                .returning(TAG.ID)
                .fetchOne().getId();
    }

    @Transactional
    public boolean addListTagForActivity(long activityId, List<TagDTO> listTagDTO) {
        if (listTagDTO == null) {
            listTagDTO = Collections.emptyList();
        }
        for (TagDTO tagDTO : listTagDTO) {
            if (tagDTO.getContent().length() > ApplicationConstant.MAX_TAG_LENGTH || tagDTO.getContent().length() == 0) {
                return false;
            }
        }
        if (listTagDTO.size() > ApplicationConstant.LIMIT_TAGS) {
            return false;
        }
        deleteActivityTag(activityId);
        return insertListTag(activityId, listTagDTO);
    }

    public boolean insertListTag(long activityId, List<TagDTO> listTagDTO) {
        for (TagDTO tagDTO : listTagDTO) {
            String tagContent = tagDTO.getContent().trim().replaceAll("\\s+", " ");
            if (tagDTO.getId() == FAILED_TAG_ID && !checkExistTag(tagContent)) {
                tagDTO.setId(insertTag(tagContent));
            }
            if (addTagforActivity(activityId, tagDTO.getId()) == 0)
                return false;
        }
        return true;
    }

    public boolean checkExistTag(String content) {
        return dslContext.fetchExists(TAG,
                TAG.CONTENT.lower().eq(content.toLowerCase()));
    }

    public int addTagforActivity(long activityId, long tagId) {
        return dslContext.insertInto(ACTIVITY_TAG)
                .values(activityId, tagId)
                .execute();
    }

    public void deleteActivityTag(long activityId) {
        dslContext.deleteFrom(ACTIVITY_TAG)
                .where(ACTIVITY_TAG.ACTIVITY_ID.eq(activityId))
                .execute();
    }

    public List<TagDTO> findListTagByContainContent(String tagContent, List<String> listAvailableContent) {
        if (listAvailableContent == null) {
            listAvailableContent = Collections.emptyList();
        }
        return dslContext.selectFrom(TAG)
                .where(TAG.CONTENT.lower().startsWith(tagContent.toLowerCase())
                        .and(TAG.CONTENT.lower().notIn(listAvailableContent.stream().map(String::toLowerCase).collect(Collectors.toList()))))
                .orderBy(TAG.CONTENT)
                .limit(10)
                .fetchInto(TagDTO.class);
    }
}
