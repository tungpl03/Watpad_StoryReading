package com.example.storywatpad.model;

public class ReadingHistory {
    private int userId;
    private int storyId;
    private int chapterId;
    private boolean liked;
    private int view;
    private String lastReadAt;
    public ReadingHistory(int userId, int storyId, int chapterId, boolean liked, int view, String lastReadAt) {
        this.userId = userId;
        this.storyId = storyId;
        this.chapterId = chapterId;
        this.liked = liked;
        this.view = view;
        this.lastReadAt = lastReadAt;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getStoryId() {
        return storyId;
    }

    public void setStoryId(int storyId) {
        this.storyId = storyId;
    }

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public String getLastReadAt() {
        return lastReadAt;
    }

    public void setLastReadAt(String lastReadAt) {
        this.lastReadAt = lastReadAt;
    }
}
