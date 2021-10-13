package com.example.flake.myapplication;

/**
 * Created by Flake on 22.05.2015.
 */
public class secret {

    String title;
    String adress;
    int photoId;
    String text;
    int likes;
    String id;
    String photourl;
    Boolean isLiked;

    secret(String name, String age, String text, int likes, int photoId, String id, String photourl, Boolean isLiked) {
        this.title = name;
        this.adress = age;
        this.text = text;
        this.likes = likes;
        this.id = id;
        this.photourl = photourl;
        this.photoId = photoId;
        this.isLiked = isLiked;
    }

}
