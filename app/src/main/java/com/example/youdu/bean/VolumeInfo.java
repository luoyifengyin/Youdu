package com.example.youdu.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VolumeInfo {
    public int id;

    @SerializedName("book_id")
    public int bookId;

    public String title;

    @SerializedName("cover")
    public String coverPath;

    public List<ChapterInfo> chapters;

    public class ChapterInfo {
        public int id;

        public int number;

        @SerializedName("volume_id")
        public int volumeId;

        public String tile;

        @SerializedName("content_path")
        public String contentPath;

        @SerializedName("update_time")
        public String updateTime;
    }
}
