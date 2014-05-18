package com.ar.accountmoney.task;

import android.os.AsyncTask;

import com.ar.accountmoney.dto.AccountMoneyAuthInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jucciani on 15/05/14.
 */
public class AccountMoneyAuthInfoTask extends AsyncTask<Void, Void, AccountMoneyAuthInfo> {
    private static final String ACCOUNT_MONEY	= "account_money";
    private static final String REQUIRED_ATTR = "required";
    private IAccountMoneyAuthInfoHandler handler;

    public interface IAccountMoneyAuthInfoHandler {
        public void handleAuthInfo(AccountMoneyAuthInfo authInfo);
    }

    public AccountMoneyAuthInfoTask(IAccountMoneyAuthInfoHandler handler) {
        this.handler = handler;
    }

    @Override
    protected AccountMoneyAuthInfo doInBackground(Void... voids) {
        //TODO OBTENER AUTHDATA
       // def data = ['payment_method_id':ACCOUNT_MONEY]
       // def authInfo = [authCode:'', authCodeRequired: false]
       //postToURL("/payment_auth?caller.id=$buyerId", data)
       //authInfo.authCode = postResponse.auth_code
       // authInfo.authCodeRequired = REQUIRED_ATTR.equals(postResponse.second_auth_factor)

        // Map con claves 'authCode' y 'authCodeRequired'
        AccountMoneyAuthInfo authInfo = new AccountMoneyAuthInfo();

        authInfo.setAuthCode("ASDFASDF");
        authInfo.setAuthCodeRequired(true); //Comment to skip password

        if(authInfo.isAuthCodeRequired()){
            // getResourceFromURL /jm/services/mp/v1.0/loginService/getSecondPwdQuestion/$custId&$siteId
            //if(jsonData?.toString()!='{}'){
                //se parsea el questionID a string para poder machear con todos questionId del site.
                //return["questionId":"${jsonData.secret_question_id}",
                //        "answer":jsonData.answer,
                //        "secondPwd":jsonData.second_password]
            //}
            authInfo.setQuestionId("1");
            authInfo.setSecretAnswer("MERCADOLIBRE");
            authInfo.setSecondPwd("android"); //Comment for new password
        }
        return authInfo;
    }

    @Override
    protected void onPostExecute(AccountMoneyAuthInfo authInfo) {
        handler.handleAuthInfo(authInfo);
    }
}
