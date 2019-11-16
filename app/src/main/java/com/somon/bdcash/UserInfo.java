package com.somon.bdcash;

public class UserInfo {

    private String userName;
    private String phoneNumber;
    private String confirmPassword;

    public UserInfo() {
    }

    public UserInfo(String userName, String phoneNumber, String confirmPassword) {
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.confirmPassword = confirmPassword;
    }

    public UserInfo(String userName, String confirmPassword) {
        this.userName = userName;
        this.confirmPassword = confirmPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
