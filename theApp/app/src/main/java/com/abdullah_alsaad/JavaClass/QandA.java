package com.abdullah_alsaad.JavaClass;

public class QandA {
    private String createdTime;
    private String question;
    private String id;

    public QandA(String createdTime, String question,int id) {
        this.createdTime = createdTime;
        this.question = question;
    }

    public QandA() {
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
