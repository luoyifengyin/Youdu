package com.example.youdu.bean.db;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class Chapter extends LitePalSupport {
    private int id;
    private int code;
    private int number;
    private String title;
    private Volume volume;
    private String content;
    @Column(ignore = true)
    private String contentPath;
    private String updateTime;

    public int getId() {
        return id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Volume getVolume() {
        return volume;
    }

    public void setVolume(Volume volume) {
        this.volume = volume;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentPath() {
        return contentPath;
    }

    public void setContentPath(String contentPath) {
        this.contentPath = contentPath;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
