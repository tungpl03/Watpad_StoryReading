package com.example.storywatpad.model;

public class StoryTag {
    private int storyTagId;
    private String name;
    public StoryTag(int storyTagId, String name) {
        this.storyTagId = storyTagId;
        this.name = name;
    }

    public int getStoryTagId() {
        return storyTagId;
    }

    public void setStoryTagId(int storyTagId) {
        this.storyTagId = storyTagId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
