package com.export.yona;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class YonaApiModel {
    private String owner;
    private String projectName;
    private String projectDescription;
    private String projectVcs;
    private String projectScope;
    private String projectCreatedDate;
    private String memberCount;
    private String issueCount;
    private String postCount;
    private String milestoneCount;

    List<User> members;
    List<User> assignees;
    List<User> authors;
    List<Issue> issues;
    List<Milestone> milestones;
    List<Post> posts;
    List<Label> labels;

    public static class Label{

    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class User {
        private String loginId;
        private String name;
        private String email;
        private String role;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class Comment {
        private int id;
        private String type;
        private User author;
        private String createdAt;
        private String body;

    }
    @Data
    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class Issue {
        private int number;
        private int id;
        private String projectName;
        private String title;
        private String body;
        private String owner;
        private String createdAt;
        private String updatedAt;

        private String type;
        private String state;
        private String refUrl;
        List<User> assignees;
    }
    @Data
    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class Milestone {
        int id;
        String title;
        String state;
        String description;
    }
    @Data
    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class Post {
        private int number;
        private int id;
        private String title;
        private String type;
        private User author; // assuming User is another DTO
        private String createdAt;
        private String updatedAt;
        private String body;

        List<User> assignees;
    }

}
