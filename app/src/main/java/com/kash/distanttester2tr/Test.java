package com.kash.distanttester2tr;

import java.util.Map;

public class Test {
    String[] Questions;
    long authorID;
    String description;
    Map<String, String[]> test;
    String testAuthor;
    String title;

    public Test() {
    }

    public String[] getQuestions() {
        return Questions;
    }

    public void setQuestions(String[] questions) {
        Questions = questions;
    }

    public long getAuthorID() {
        return authorID;
    }

    public void setAuthorID(long authorID) {
        this.authorID = authorID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String[]> getTest() {
        return test;
    }

    public void setTest(Map<String, String[]> test) {
        this.test = test;
    }

    public String getTestAuthor() {
        return testAuthor;
    }

    public void setTestAuthor(String testAuthor) {
        this.testAuthor = testAuthor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
