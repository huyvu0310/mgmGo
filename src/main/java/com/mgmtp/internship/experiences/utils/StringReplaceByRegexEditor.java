package com.mgmtp.internship.experiences.utils;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.lang.Nullable;

/**
 * Replace string using regex.
 *
 * @author: vhduong
 */

public class StringReplaceByRegexEditor extends StringTrimmerEditor {

    private final String regex;

    public StringReplaceByRegexEditor(boolean emptyAsNull, String regex) {
        super(emptyAsNull);
        this.regex = regex;
    }

    public StringReplaceByRegexEditor(String charsToDelete, boolean emptyAsNull, String regex) {
        super(charsToDelete, emptyAsNull);
        this.regex = regex;
    }

    @Override
    public void setAsText(@Nullable String text) {
        super.setAsText(text);
        if (this.getValue() != null) {
            setValue(this.getValue().toString().replaceAll(regex, " "));
        }
    }
}
