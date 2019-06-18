package com.example.youdu.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Book implements Serializable {
    public String tile;

    @SerializedName("author_id")
    public int authorId;

    @SerializedName("cover")
    public String coverUrl;

    public String description;

    @SerializedName("reading_chapter_id")
    public int readingChapterId;
}
