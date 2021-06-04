package com.akapps.etutor;

public class Post {
    String title, post, category, subjects, salary, contact, uid;

    public Post() {}

    public Post(String title, String post, String category, String subjects, String salary, String contact, String uid) {
        this.title = title;
        this.post = post;
        this.category = category;
        this.subjects = subjects;
        this.salary = salary;
        this.contact = contact;
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public String getPost() {
        return post;
    }

    public String getCategory() {
        return category;
    }

    public String getSubjects() {
        return subjects;
    }

    public String getSalary() {
        return salary;
    }

    public String getContact() {
        return contact;
    }

    public String getUid() {
        return uid;
    }
}
