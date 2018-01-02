package com.itstrong.popularscience.bean;

/**
 * Created by itstrong on 2016/6/28.
 */
public class Book {

    private String id;
    private String imageUrl;
    private String downloadUrl;
    private String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id='" + id + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
