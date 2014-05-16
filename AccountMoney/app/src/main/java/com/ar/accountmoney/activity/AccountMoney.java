package com.ar.accountmoney.activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class AccountMoney extends ActionBarActivity implements IAccountMoneyListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_money);
        //Inicializo el fragment de CreatePassword
        if(getSupportFragmentManager().findFragmentById(R.id.account_money_fragment) == null){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.account_money_fragment, CreatePasswordFragment.newInstance());
            transaction.commit();
        }
        //Inicializo el fragment de InputPassword
        if(getSupportFragmentManager().findFragmentById(R.id.account_money_fragment) == null){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.account_money_fragment, InputPasswordFragment.newInstance());
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public void onConfirmCreatePassword() {

    }

    @Override
    public void onConfirmPassword() {

    }

    @Override
    public void onForgotPassword() {
        //Inicializo el fragment de InputSecretAnswer
        //if(getSupportFragmentManager().findFragmentById(R.id.account_money_fragment) == null){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.account_money_fragment, InputSecretAnswerFragment.newInstance());
            transaction.addToBackStack(null);
            transaction.commit();
        //}
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
