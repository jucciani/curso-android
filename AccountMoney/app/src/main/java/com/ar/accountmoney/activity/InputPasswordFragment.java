package com.ar.accountmoney.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ar.accountmoney.dto.AccountMoneyAuthInfo;


public class InputPasswordFragment extends Fragment {

    private IAccountMoneyListener activityCallback;

    /**
     * Crea una nueva instancia del InputPasswordFragment.
     */
    public static InputPasswordFragment newInstance(AccountMoneyAuthInfo authInfo) {
        InputPasswordFragment newFragment = new InputPasswordFragment();
        return newFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_input_password, container, false);

        //Forgot password listener
        view.findViewById(R.id.forgot_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityCallback.onForgotPassword();
            }
        });
        //Submit button listener
        view.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onConfirmPassword();
            }
        });
        return view;
    }

    private void onConfirmPassword(){
        if(validateInputPassword()){
           activityCallback.onConfirmPassword();
        }
    }
    private boolean validateInputPassword() {
        //Obtengo los campos a validar
        EditText inputPassword = (EditText)getView().findViewById(R.id.input_password);
        String password = inputPassword.getText().toString();

        //Valido el campo inputPassword
        if (password != null && password.length() > 0){
            return true;
        } else {
            inputPassword.setError(getString(R.string.required_error));
            return false;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            activityCallback = (IAccountMoneyListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SearchItemListener");
        }
    }
}
