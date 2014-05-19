package com.ar.accountmoney.activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ar.accountmoney.dto.AccountMoneyAuthInfo;
import com.ar.accountmoney.task.AccountMoneyAuthInfoTask;


public class AccountMoney extends ActionBarActivity implements IAccountMoneyListener, AccountMoneyAuthInfoTask.IAccountMoneyAuthInfoHandler {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_money);
        new AccountMoneyAuthInfoTask(this).execute();
    }

    @Override
    public void handleAuthInfo(AccountMoneyAuthInfo authInfo) {
        if(!authInfo.isSecondPassCreated()){
            //Inicializo el fragment de CreatePassword
            if(getSupportFragmentManager().findFragmentById(R.id.account_money_fragment) == null){
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.account_money_fragment, CreatePasswordFragment.newInstance());
                transaction.commit();
            }
        } else {
            //Inicializo el fragment de InputPassword
            if(getSupportFragmentManager().findFragmentById(R.id.account_money_fragment) == null){
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.account_money_fragment, InputPasswordFragment.newInstance());
                transaction.commit();
            }
        }
        findViewById(R.id.progressBar).setVisibility(View.GONE);
    }

    @Override
    public void onConfirmCreatePassword() {

    }

    @Override
    public void onConfirmPassword() {

    }

    @Override
    public void onForgotPassword() {
        String secretQuestion = getResources().getStringArray(R.array.secret_questions)[0];
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.account_money_fragment, InputSecretAnswerFragment.newInstance(secretQuestion));
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onConfirmSecretAnswer() {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.account_money, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
