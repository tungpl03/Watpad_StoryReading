package com.example.storywatpad.model;

public class Story {
    int story_id;
    int author_id;
    String title;
    String description;
    String CoverImageUrl;
    int genre_id;
    String status;
    String created_at;
    String updated_at;

    long lastReadAt;

    public Story() {
    }

    public Story(int story_id, int author_id, String title, String description, String coverImageUrl, int genre_id, String status, String created_at, String updated_at) {
        this.story_id = story_id;
        this.author_id = author_id;
        this.title = title;
        this.description = description;
        CoverImageUrl = coverImageUrl;
        this.genre_id = genre_id;
        this.status = status;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public int getStory_id() {
        return story_id;
    }

    public void setStory_id(int story_id) {
        this.story_id = story_id;
    }

    public int getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(int author_id) {
        this.author_id = author_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImageUrl() {
        return CoverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        CoverImageUrl = coverImageUrl;
    }

    public int getGenre_id() {
        return genre_id;
    }

    public void setGenre_id(int genre_id) {
        this.genre_id = genre_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
    public String getDrawableImageName() {
        if(CoverImageUrl != null) {
            return CoverImageUrl.replace(".jpg", "").replace(".png", "").replace(".jpeg", "");
        }
    else{
        return "";
        }
    }

    public long getLastReadAt() {
        return lastReadAt;
    }

    public void setLastReadAt(long lastReadAt) {
        this.lastReadAt = lastReadAt;
    }

}
