package com.example.instantchattingapp.classes;

public class Personal_Data {
    private String Name;
    private String Email;
    private String Password;
    private String Image;

    public Personal_Data(String name, String email, String password,String image) {
        Name = name;
        Email = email;
        Password = password;
        Image=image;
    }
    public Personal_Data(){}
    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }
    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }
    public void setPassword(String password) {
        Password = password;
    }

    public void setImage(String image) {
        Image = image;
    }
    public String getImage() {
        return Image;
    }
}
