package com.example.storywatpad.model;

public class Comment {
    private int commentId;
    private int userId;
    private Integer chapterId;
    private Integer storyId;
    private Integer parentCommentId;
    private String content;
    private String createdAt;
    private String updatedAt;

    public Comment() {
    }

    public Comment(int commentId, int userId, Integer chapterId, Integer storyId, Integer parentCommentId, String content, String createdAt, String updatedAt) {
        this.commentId = commentId;
        this.userId = userId;
        this.chapterId = chapterId;
        this.storyId = storyId;
        this.parentCommentId = parentCommentId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Comment(int userId, Integer chapterId, Integer parentCommentId, Integer storyId, String content, String createdAt, String updatedAt) {
        this.userId = userId;
        this.chapterId = chapterId;
        this.parentCommentId = parentCommentId;
        this.storyId = storyId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getChapterId() {
        return chapterId;
    }

    public void setChapterId(Integer chapterId) {
        this.chapterId = chapterId;
    }

    public Integer getStoryId() {
        return storyId;
    }

    public void setStoryId(Integer storyId) {
        this.storyId = storyId;
    }

    public Integer getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(Integer parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
