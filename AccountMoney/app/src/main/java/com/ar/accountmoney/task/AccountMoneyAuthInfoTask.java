package com.ar.accountmoney.task;

import android.os.AsyncTask;

import com.ar.accountmoney.dto.AccountMoneyAuthInfo;

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
            //users/$id/hasSecondPass
            //returns 'has_second_pass'
            authInfo.setSecondPassCreated(true); //False to create password

            if(authInfo.isSecondPassCreated()){
                //users/$id/secretquestion
                //return 'secret_question_id'
                authInfo.setQuestionId("1");
            }
        }
        return authInfo;
    }

    @Override
    protected void onPostExecute(AccountMoneyAuthInfo authInfo) {
        handler.handleAuthInfo(authInfo);
    }
}
