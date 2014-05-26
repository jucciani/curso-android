package com.ar.accountmoney.task;

import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

import com.ar.accountmoney.dto.AccountMoneyAuthInfo;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by jucciani on 15/05/14.
 */
public class AccountMoneyAuthInfoTask extends AsyncTask<Void, Void, AccountMoneyAuthInfo> {
    private static final String ACCOUNT_MONEY	= "account_money";
    private static final String REQUIRED_ATTR = "required";
    private static final String HAS_SECOND_PASS_URI = "http://internal.mercadolibre.com/internal/users/137773433/hasSecondPass?caller.id=137773433";
    private static final String SECRET_QUESTION_ID_URI = "http://internal.mercadolibre.com/internal/users/137773433/secretquestion?caller.id=137773433";
    private IAccountMoneyAuthInfoHandler handler;

    public interface IAccountMoneyAuthInfoHandler {
        public void handleAuthInfo(AccountMoneyAuthInfo authInfo);
    }

    public AccountMoneyAuthInfoTask(IAccountMoneyAuthInfoHandler handler) {
        this.handler = handler;
    }

    @Override
    protected AccountMoneyAuthInfo doInBackground(Void... voids) {
        AccountMoneyAuthInfo authInfo = null;
        //TODO OBTENER AUTHDATA
        // def data = ['payment_method_id':ACCOUNT_MONEY]
        // def authInfo = [authCode:'', authCodeRequired: false]
        //postToURL("/payment_auth?caller.id=$buyerId", data)
        //authInfo.authCode = postResponse.auth_code
        // authInfo.authCodeRequired = REQUIRED_ATTR.equals(postResponse.second_auth_factor)

        try {
            // Map con claves 'authCode' y 'authCodeRequired'
            authInfo = new AccountMoneyAuthInfo();

            authInfo.setAuthCode("ASDFASDF");
            authInfo.setAuthCodeRequired(true); //Comment to skip password

            System.out.println("AUTH CODE REQUIRED" + authInfo.isAuthCodeRequired());
            if(authInfo.isAuthCodeRequired()){
                getSecondPassInfo(authInfo);
                System.out.println("SEC PASS" + authInfo.isSecondPassCreated());
                if(authInfo.isSecondPassCreated()){
                    getSecretQuestionInfo(authInfo);
                    System.out.println("QUESTION ID"+authInfo.getQuestionId());
                }
            }
        } catch (Exception e){
            System.out.println(e);
        }
        return authInfo;
    }

    private void getSecondPassInfo(AccountMoneyAuthInfo authInfo) throws JSONException {
        JSONObject result = getUriResult(HAS_SECOND_PASS_URI);
        authInfo.setSecondPassCreated(result.getBoolean("has_second_pass"));
    }

    private void getSecretQuestionInfo(AccountMoneyAuthInfo authInfo) throws JSONException {
        JSONObject result = getUriResult(SECRET_QUESTION_ID_URI);
        authInfo.setQuestionId(result.getInt("secret_question_id"));
    }

    private JSONObject getUriResult(String URI){
        InputStream is = null;
        JSONObject result = null;
        AndroidHttpClient client = null;
        try {
            Uri.Builder uriBuilder = Uri.parse(URI)
                    .buildUpon();
            String uri = uriBuilder.build().toString();

            client = AndroidHttpClient.newInstance("Android");
            HttpGet request = new HttpGet(uri);
            HttpResponse response = client.execute(request); //here is where the exception is thrown
            is = response.getEntity().getContent();

            // Creo un JSONObject con la respuesta
            result = new JSONObject(readIt(is));
        } catch (Exception e) {
            System.out.println(e);
        } finally {// Cerramos la conexion y el InputStream.

            if (client != null) client.close();
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(AccountMoneyAuthInfo authInfo) {
        handler.handleAuthInfo(authInfo);
    }

    //Transforma el InputStream a String.
    private String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {

        BufferedReader r = new BufferedReader(new InputStreamReader(stream,"UTF-8"));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }
        return total.toString();
    }
}
