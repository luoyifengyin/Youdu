package com.example.youdu.bean.db;

import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

public class Volume extends LitePalSupport {
    private int id;
    private int code;
    private Book book;
    private String title;
    private String coverPath;
    private List<Chapter> chapters = new ArrayList<>();

    public int getId() {
        return id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public List<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }
}
