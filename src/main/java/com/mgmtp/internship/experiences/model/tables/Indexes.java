/*
 * This file is generated by jOOQ.
 */
package com.mgmtp.internship.experiences.model.tables;


import com.mgmtp.internship.experiences.model.tables.tables.Activity;
import com.mgmtp.internship.experiences.model.tables.tables.Comment;
import com.mgmtp.internship.experiences.model.tables.tables.Image;
import com.mgmtp.internship.experiences.model.tables.tables.Rating;
import com.mgmtp.internship.experiences.model.tables.tables.Tag;
import com.mgmtp.internship.experiences.model.tables.tables.User;

import javax.annotation.Generated;

import org.jooq.Index;
import org.jooq.OrderField;
import org.jooq.impl.Internal;


/**
 * A class modelling indexes of tables of the <code>public</code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Indexes {

    // -------------------------------------------------------------------------
    // INDEX definitions
    // -------------------------------------------------------------------------

    public static final Index PK_ACTIVITY = Indexes0.PK_ACTIVITY;
    public static final Index PK_COMMENT = Indexes0.PK_COMMENT;
    public static final Index PK_IMAGE = Indexes0.PK_IMAGE;
    public static final Index PK_RATING = Indexes0.PK_RATING;
    public static final Index PK_TAG = Indexes0.PK_TAG;
    public static final Index PK_USER = Indexes0.PK_USER;
    public static final Index USER_DISPLAY_NAME_KEY = Indexes0.USER_DISPLAY_NAME_KEY;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Indexes0 {
        public static Index PK_ACTIVITY = Internal.createIndex("pk_activity", Activity.ACTIVITY, new OrderField[] { Activity.ACTIVITY.ID }, true);
        public static Index PK_COMMENT = Internal.createIndex("pk_comment", Comment.COMMENT, new OrderField[] { Comment.COMMENT.ID }, true);
        public static Index PK_IMAGE = Internal.createIndex("pk_image", Image.IMAGE, new OrderField[] { Image.IMAGE.ID }, true);
        public static Index PK_RATING = Internal.createIndex("pk_rating", Rating.RATING, new OrderField[] { Rating.RATING.ID }, true);
        public static Index PK_TAG = Internal.createIndex("pk_tag", Tag.TAG, new OrderField[] { Tag.TAG.ID }, true);
        public static Index PK_USER = Internal.createIndex("pk_user", User.USER, new OrderField[] { User.USER.ID }, true);
        public static Index USER_DISPLAY_NAME_KEY = Internal.createIndex("user_display_name_key", User.USER, new OrderField[] { User.USER.DISPLAY_NAME }, true);
    }
}
