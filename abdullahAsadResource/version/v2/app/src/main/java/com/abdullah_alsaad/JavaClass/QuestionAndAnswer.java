package com.abdullah_alsaad.JavaClass;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

@DatabaseTable(tableName = QuestionAndAnswer.TABLE_QUESTIONANDANSWER)
public class QuestionAndAnswer implements Serializable {
    public static final String TABLE_QUESTIONANDANSWER = "questionAndAnswer";

    public static final String QUESTIONANDANSWER_ID = "id";
    public static final String QUESTIONANDANSWER_TITLE = "tile";
    public static final String QUESTIONANDANSWER_QUESTION = "question";
    public static final String QUESTIONANDANSWER_ANSWER = "answer";
    public static final String QUESTIONANDANSWER_PARENT_ID = "parentId";
    public static final String QUESTIONANDANSWER_MP3URL = "mp3URL";
    public static final String TOPIC_TYPE = "type";
    public static final String QUESTIONANDANSWER_CREATED_DATE = "createdDate";

    @DatabaseField(generatedId = true,columnName = QUESTIONANDANSWER_ID)
    private long id;
    @DatabaseField(columnName = QUESTIONANDANSWER_TITLE)
    private String tile;
    @DatabaseField(columnName = QUESTIONANDANSWER_QUESTION)
    private String question;
    @DatabaseField(columnName = QUESTIONANDANSWER_ANSWER)
    private String answer;
    @DatabaseField(columnName = QUESTIONANDANSWER_PARENT_ID)
    private long parentId;
    @DatabaseField(columnName = QUESTIONANDANSWER_MP3URL)
    private String mp3URL;
    @DatabaseField(columnName = TOPIC_TYPE)
    private String type;
    @DatabaseField(columnName = QUESTIONANDANSWER_CREATED_DATE, format="yyyy-MM-dd HH:mm:ss",
            dataType= DataType.DATE_STRING)
    private Date createdTime;

    public QuestionAndAnswer() {
    }

    public QuestionAndAnswer(long id, String tile, long parentId, String type, String question, String answer, String mp3URL) {
        this.id = id;
        this.tile = tile;
        this.parentId = parentId;
        this.type = type;
        this.question = question;
        this.answer = answer;
        this.mp3URL = mp3URL;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTile() {
        return tile;
    }

    public void setTile(String tile) {
        this.tile = tile;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getMp3URL() {
        return mp3URL;
    }

    public void setMp3URL(String mp3URL) {
        this.mp3URL = mp3URL;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
