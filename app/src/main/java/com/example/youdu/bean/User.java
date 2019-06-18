package com.example.youdu.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    public int uid;

    public String name;

    @SerializedName("profile_picture")
    public String avatarUrl;
}
