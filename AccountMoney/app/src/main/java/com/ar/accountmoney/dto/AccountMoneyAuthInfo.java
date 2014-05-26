package com.ar.accountmoney.dto;

import java.io.Serializable;

/**
 * Created by jucciani on 17/05/14.
 */
public class AccountMoneyAuthInfo implements Serializable{
    private String authCode;
    private boolean authCodeRequired;
    private boolean secondPassCreated;
    private int questionId;
    private String secretAnswer;
    private String secondPwd;

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCodeRequired(boolean authCodeRequired) {
        this.authCodeRequired = authCodeRequired;
    }

    public boolean isAuthCodeRequired() {
        return authCodeRequired;
    }

    public void setSecondPassCreated(boolean secondPassCreated) {
        this.secondPassCreated = secondPassCreated;
    }

    public boolean isSecondPassCreated() {
        return secondPassCreated;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setSecretAnswer(String secretAnswer) {
        this.secretAnswer = secretAnswer;
    }

    public String getSecretAnswer() {
        return secretAnswer;
    }

    public void setSecondPwd(String secondPwd) {
        this.secondPwd = secondPwd;
    }

    public String getSecondPwd() {
        return secondPwd;
    }
}
