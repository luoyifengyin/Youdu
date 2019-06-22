package com.example.youdu.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BookInfo {
    public int id;

    public String tile;

    @SerializedName("author_id")
    public int authorId;

    @Expose(serialize = false, deserialize = false)
    public String authorName;

    @SerializedName("cover")
    public String coverPath;

    public String introduction;

    @SerializedName("publish_time")
    public String publishTime;

    @SerializedName("reading_chapter_id")
    public int readingChapterId;

    @SerializedName("last_time")
    public long lastTime;

    @Expose(serialize = false, deserialize = false)
    public List<VolumeInfo> volumes;
}
