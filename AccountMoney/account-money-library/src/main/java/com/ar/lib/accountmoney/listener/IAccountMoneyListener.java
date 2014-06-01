package com.ar.lib.accountmoney.listener;

/**
 * Created by jucciani on 15/05/14.
 */
public interface IAccountMoneyListener {
    void onConfirmCreatePassword();

    void onConfirmPassword();

    void onForgotPassword();

    void onConfirmSecretAnswer();
}
