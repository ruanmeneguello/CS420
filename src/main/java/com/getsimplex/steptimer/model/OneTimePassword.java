package com.getsimplex.steptimer.model;

import java.util.Date;

public class OneTimePassword {

    private Date expirationDate;
    private Integer oneTimePassword;
    private String loginToken;

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Integer getOneTimePassword() {
        return oneTimePassword;
    }

    public void setOneTimePassword(Integer oneTimePassword) {
        this.oneTimePassword = oneTimePassword;
    }

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }
}
