package com.export.yona.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@JsonIgnoreProperties(ignoreUnknown=true)
public class YonaApiModel {
    public String owner;
    public String projectName;
    public String projectDescription;
    public String projectVcs;
    public String projectScope;
    public String projectCreatedDate;
    public String memberCount;
    public String issueCount;
    public String postCount;
    public String milestoneCount;

    public List<User> members;
    public List<User> assignees;
    public List<User> authors;
    public List<Issue> issues;
    public List<Milestone> milestones;
    public List<Post> posts;
    public List<Label> labels;

    public static class Label{

    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class User {
        public String loginId;
        public String name;
        public String email;
        public String role;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class Comment {
        private int id;
        public String type;
        private User author;
        public String createdAt;
        public String body;

    }
    @Data
    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class Issue {
        private int number;
        private int id;
        public String projectName;
        public String title;
        public String body;
        public String owner;
        public String createdAt;
        public String updatedAt;

        public String type;
        public String state;
        public String refUrl;
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
        public String title;
        public String type;
        private User author; // assuming User is another DTO
        public String createdAt;
        public String updatedAt;
        public String body;

        List<User> assignees;
    }

}
