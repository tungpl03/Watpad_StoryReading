package com.example.storywatpad.model;

public class StoryTagMapping {
    private int storyId;
    private int storyTagId;

    public StoryTagMapping(int storyId, int storyTagId) {
        this.storyId = storyId;
        this.storyTagId = storyTagId;
    }

    public int getStoryId() {
        return storyId;
    }

    public void setStoryId(int storyId) {
        this.storyId = storyId;
    }

    public int getStoryTagId() {
        return storyTagId;
    }

    public void setStoryTagId(int storyTagId) {
        this.storyTagId = storyTagId;
    }
}
