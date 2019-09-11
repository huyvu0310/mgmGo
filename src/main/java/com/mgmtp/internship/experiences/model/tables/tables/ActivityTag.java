/*
 * This file is generated by jOOQ.
 */
package com.mgmtp.internship.experiences.model.tables.tables;


import com.mgmtp.internship.experiences.model.tables.Keys;
import com.mgmtp.internship.experiences.model.tables.Public;
import com.mgmtp.internship.experiences.model.tables.tables.records.ActivityTagRecord;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * activity tag table
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ActivityTag extends TableImpl<ActivityTagRecord> {

    private static final long serialVersionUID = -695041951;

    /**
     * The reference instance of <code>public.activity_tag</code>
     */
    public static final ActivityTag ACTIVITY_TAG = new ActivityTag();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ActivityTagRecord> getRecordType() {
        return ActivityTagRecord.class;
    }

    /**
     * The column <code>public.activity_tag.activity_id</code>.
     */
    public final TableField<ActivityTagRecord, Long> ACTIVITY_ID = createField("activity_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.activity_tag.tag_id</code>.
     */
    public final TableField<ActivityTagRecord, Long> TAG_ID = createField("tag_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * Create a <code>public.activity_tag</code> table reference
     */
    public ActivityTag() {
        this(DSL.name("activity_tag"), null);
    }

    /**
     * Create an aliased <code>public.activity_tag</code> table reference
     */
    public ActivityTag(String alias) {
        this(DSL.name(alias), ACTIVITY_TAG);
    }

    /**
     * Create an aliased <code>public.activity_tag</code> table reference
     */
    public ActivityTag(Name alias) {
        this(alias, ACTIVITY_TAG);
    }

    private ActivityTag(Name alias, Table<ActivityTagRecord> aliased) {
        this(alias, aliased, null);
    }

    private ActivityTag(Name alias, Table<ActivityTagRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("activity tag table"));
    }

    public <O extends Record> ActivityTag(Table<O> child, ForeignKey<O, ActivityTagRecord> key) {
        super(child, key, ACTIVITY_TAG);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<ActivityTagRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<ActivityTagRecord, ?>>asList(Keys.ACTIVITY_TAG__FK_ACTIVITY_TAG_ACTIVITY, Keys.ACTIVITY_TAG__FK_ACTIVITY_TAG_TAG);
    }

    public Activity activity() {
        return new Activity(this, Keys.ACTIVITY_TAG__FK_ACTIVITY_TAG_ACTIVITY);
    }

    public Tag tag() {
        return new Tag(this, Keys.ACTIVITY_TAG__FK_ACTIVITY_TAG_TAG);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActivityTag as(String alias) {
        return new ActivityTag(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActivityTag as(Name alias) {
        return new ActivityTag(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ActivityTag rename(String name) {
        return new ActivityTag(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ActivityTag rename(Name name) {
        return new ActivityTag(name, null);
    }
}
