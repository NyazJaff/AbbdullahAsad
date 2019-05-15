package com.abdullah_alsaad.JavaClass;
import java.util.Date;

public class RadioSetting {
    private String extraInfo,title, youtubeUrl,apiKey;
    private boolean live;
    private Date updatedTime;

    public RadioSetting(String extraInfo, String title, String youtubeUrl, boolean live, Date updatedTime, String apiKey) {
        this.extraInfo = extraInfo;
        this.title = title;
        this.youtubeUrl = youtubeUrl;
        this.live = live;
        this.updatedTime = updatedTime;
        this.apiKey = apiKey;
    }
    public RadioSetting() {
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}



