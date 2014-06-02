package com.ar.lib.accountmoney.task;

import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jucciani on 01/06/14.
 */
public class CreateSecondPasswordTask extends AsyncTask<String, Void, Void> {
    //Data keys
    public static final String CUST_ID = "cust_id";
    public static final String SITE_ID = "site_id";
    public static final String SECOND_PASS_ID = "second_password";
    public static final String SECOND_PASS_REPEATED_ID = "second_password_repeated";
    public static final String QUESTION_ID = "question";
    public static final String SECRET_ANSWER_ID = "answer";
    //URIs
    private static final String CALLER_ID_URI = "?caller.id=";
    private static final String BASE_USER_API_URI = "http://10.0.3.2:35000/internal/users/";
    private static final String SECOND_PASS_URI = "/secondpassword";

    private Map<String, String> data;

    public CreateSecondPasswordTask(Map<String, String> data){
        this.data = data;
    }

    @Override
    protected Void doInBackground(String... voids) {
        String userId = this.data.get(CUST_ID);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
        nameValuePairs.add(new BasicNameValuePair(CUST_ID, userId));
        nameValuePairs.add(new BasicNameValuePair(SITE_ID, this.data.get(SITE_ID)));
        nameValuePairs.add(new BasicNameValuePair(SECOND_PASS_ID, this.data.get(SECOND_PASS_ID)));
        nameValuePairs.add(new BasicNameValuePair(SECOND_PASS_REPEATED_ID, this.data.get(SECOND_PASS_REPEATED_ID)));
        nameValuePairs.add(new BasicNameValuePair(QUESTION_ID, this.data.get(QUESTION_ID)));
        nameValuePairs.add(new BasicNameValuePair(SECRET_ANSWER_ID, this.data.get(SECRET_ANSWER_ID)));
        JSONObject result = postToUri(BASE_USER_API_URI+userId+SECOND_PASS_URI+CALLER_ID_URI+userId, nameValuePairs);

        return null;
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
            System.out.print(is);
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
