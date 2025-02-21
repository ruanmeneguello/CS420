//Copyright 2021 Sean Murdock

package com.getsimplex.steptimer.model;

public class ExchangeOTPForLoginToken {

    private String phoneNumber;
    private Integer oneTimePassword;
    private String region;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getOneTimePassword() {
        return oneTimePassword;
    }

    public void setOneTimePassword(Integer oneTimePassword) {
        this.oneTimePassword = oneTimePassword;
    }

    public String getRegion() {
        return region;
    }
    public void setRegion(String region) {
        this.region = region;

    }
}
