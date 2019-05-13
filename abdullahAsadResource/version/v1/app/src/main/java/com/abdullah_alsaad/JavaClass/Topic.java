package com.abdullah_alsaad.JavaClass;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = Topic.TABLE_TOPIC)
public class Topic implements Serializable{

    public static final String TABLE_TOPIC = "topic";
    public static final String TOPIC_ID = "id";
    public static final String TOPIC_STRING_ID = "stringId";
    public static final String TOPIC_PARENT_ID = "parentId";
    public static final String TOPIC_NAME = "name";
    public static final String TOPIC_TYPE = "type";
    public static final String TOPIC_PDFURL = "pdfURL";
    public static final String TOPIC_MP3URL = "mp3URL";

    @DatabaseField(id = true, canBeNull = false, columnName = TOPIC_ID)
    private long id;
    @DatabaseField(columnName = TOPIC_STRING_ID)
    private String stringId;
    @DatabaseField(columnName = TOPIC_PARENT_ID)
    private long parentId;
    @DatabaseField(columnName = TOPIC_NAME)
    private String name;
    @DatabaseField(columnName = TOPIC_TYPE)
    private String type;
    @DatabaseField(columnName = TOPIC_PDFURL)
    private String pdfURL;
    @DatabaseField(columnName = TOPIC_MP3URL)
    private String mp3URL;

    public Topic() {
    }

    public Topic(long id, String stringId, String name, long parentId, String type, String pdfURL, String mp3URL) {
        this.id = id;
        this.stringId = stringId;
        this.parentId = parentId;
        this.name = name;
        this.type = type;
        this.pdfURL = pdfURL;
        this.mp3URL = mp3URL;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStringId() {
        return stringId;
    }

    public void setStringId(String stringId) {
        this.stringId = stringId;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPdfURL() {
        return pdfURL;
    }

    public void setPdfURL(String pdfURL) {
        this.pdfURL = pdfURL;
    }

    public String getMp3URL() {
        return mp3URL;
    }

    public void setMp3URL(String mp3URL) {
        this.mp3URL = mp3URL;
    }
}
