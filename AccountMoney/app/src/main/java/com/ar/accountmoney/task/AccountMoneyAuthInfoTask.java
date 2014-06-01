package com.ar.accountmoney.task;

import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

import com.ar.accountmoney.dto.AccountMoneyAuthInfo;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jucciani on 15/05/14.
 */
public class AccountMoneyAuthInfoTask extends AsyncTask<Void, Void, AccountMoneyAuthInfo> {
    //Data values
    private static final String ACCOUNT_MONEY	= "account_money";
    private static final String REQUIRED_ATTR = "required";
    //Data keys
    private static final String SECOND_AUTH_FACTOR = "second_auth_factor";
    private static final String AUTH_CODE = "auth_code";
    private static final String PAYMENT_METHOD_ID = "payment_method_id";
    private static final String HAS_SECOND_PASS = "has_second_pass";
    private static final String SECRET_QUESTION_ID = "secret_question_id";
    //URIs
    private static final String GET_AUTH_DATA_URI = "http://10.0.3.2:35000/payment_auth?caller.id=157286354";
    private static final String HAS_SECOND_PASS_URI = "http://10.0.3.2:35000/internal/users/157286354/hasSecondPass?caller.id=157286354";
    private static final String SECRET_QUESTION_ID_URI = "http://10.0.3.2:35000/internal/users/157286354/secretquestion?caller.id=157286354";
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

        try {
            // Map con claves 'authCode' y 'authCodeRequired'
            authInfo = new AccountMoneyAuthInfo();
            getAuthCode(authInfo);

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

    private void getAuthCode(AccountMoneyAuthInfo authInfo) throws JSONException {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair(PAYMENT_METHOD_ID, ACCOUNT_MONEY));
        JSONObject result = postToUri(GET_AUTH_DATA_URI, nameValuePairs);
        System.out.println(result);
        authInfo.setAuthCode(result.getString(AUTH_CODE));
        authInfo.setAuthCodeRequired(REQUIRED_ATTR.equals(result.getString(SECOND_AUTH_FACTOR)));
    }

    private void getSecondPassInfo(AccountMoneyAuthInfo authInfo) throws JSONException {
        JSONObject result = getUriResult(HAS_SECOND_PASS_URI);
        System.out.println(result);
        authInfo.setSecondPassCreated(result.getBoolean(HAS_SECOND_PASS));
    }

    private void getSecretQuestionInfo(AccountMoneyAuthInfo authInfo) throws JSONException {
        JSONObject result = getUriResult(SECRET_QUESTION_ID_URI);
        System.out.println(result);
        authInfo.setQuestionId(result.getInt(SECRET_QUESTION_ID));
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

    private JSONObject postToUri(String URI, List<NameValuePair> nameValuePairs) {
        InputStream is = null;
        JSONObject result = null;
        AndroidHttpClient client = null;
        try {
            Uri.Builder uriBuilder = Uri.parse(URI)
                    .buildUpon();
            String uri = uriBuilder.build().toString();

            client = AndroidHttpClient.newInstance("Android");
            HttpPost request = new HttpPost(uri);
            request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

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
