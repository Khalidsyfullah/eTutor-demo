package com.akapps.etutor;

public class Profile {
    String name, email, phonenumber, gender;

    public Profile(){}

    public Profile(String name, String email, String phonenumber, String gender) {
        this.name = name;
        this.email = email;
        this.phonenumber = phonenumber;
        this.gender = gender;;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public String getGender() {
        return gender;
    }

}
