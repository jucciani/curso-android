package com.ar.accountmoney.task;

import android.os.AsyncTask;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jucciani on 15/05/14.
 */
public class AccountMoneyAuthInfoTask extends AsyncTask<Void, Void, Map<String, Object>> {
    private static final String ACCOUNT_MONEY	= "account_money";
    private static final String REQUIRED_ATTR = "required";
    private IAccountMoneyAuthInfoHandler handler;

    public interface IAccountMoneyAuthInfoHandler {
        public void handleAuthInfo(Map<String, Object> authInfo);
    }

    public AccountMoneyAuthInfoTask(IAccountMoneyAuthInfoHandler handler) {
        this.handler = handler;
    }

    @Override
    protected Map<String, Object> doInBackground(Void... voids) {
        //TODO OBTENER AUTHDATA
       // def data = ['payment_method_id':ACCOUNT_MONEY]
       // def authInfo = [authCode:'', authCodeRequired: false]
       //postToURL("/payment_auth?caller.id=$buyerId", data)
       //authInfo.authCode = postResponse.auth_code
       // authInfo.authCodeRequired = REQUIRED_ATTR.equals(postResponse.second_auth_factor)

        // Map con claves 'authCode' y 'authCodeRequired'
        Map<String, Object> authInfo = new HashMap<String, Object>();

        authInfo.put("authCode","ASDFASDF");
        authInfo.put("authCodeRequired",true); //Comment to skip password

        if(authInfo.get("authCodeRequired") != null){
            // getResourceFromURL /jm/services/mp/v1.0/loginService/getSecondPwdQuestion/$custId&$siteId
            //if(jsonData?.toString()!='{}'){
                //se parsea el questionID a string para poder machear con todos questionId del site.
                //return["questionId":"${jsonData.secret_question_id}",
                //        "answer":jsonData.answer,
                //        "secondPwd":jsonData.second_password]
            //}
            authInfo.put("questionId",1);
            authInfo.put("answer","MERCADOLIBRE");
            authInfo.put("secondPwd","android"); //Comment for new pasword
        }
        return authInfo;
    }

    @Override
    protected void onPostExecute(Map<String, Object> authInfo) {
        handler.handleAuthInfo(authInfo);
    }
}
