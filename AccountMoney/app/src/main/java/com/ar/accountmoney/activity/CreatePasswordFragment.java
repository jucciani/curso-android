package com.ar.accountmoney.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;


public class CreatePasswordFragment extends Fragment {

    private IAccountMoneyListener activityCallback;

    /**
     * Crea una nueva instancia del CreatePasswordFragment.
     */
    public static CreatePasswordFragment newInstance() {
        CreatePasswordFragment newFragment = new CreatePasswordFragment();
        //Bundle args = new Bundle();
        //args.putString("PARAM", pram);
        //newFragment.setArguments(args);
        return newFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_password, container, false);

        //Fill Spinner data
        Spinner spinner = (Spinner) view.findViewById(R.id.secret_questions);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.secret_questions, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        //Submit button listener
        view.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onConfirmCreatePassword();
            }
        });
        return view;
    }

    private void onConfirmCreatePassword(){
        validateFormData();
    }

    private void validateFormData(){

        boolean validForm = true;
        validForm &= validatePasswords();
        validForm &= validateSecretAnswer();
        if(validForm){
            activityCallback.onConfirmCreatePassword();
        }
    }

    private static final String FORBIDDEN_CHARACTERS[] = {"*", " ", "-"};

    private boolean validatePasswords() {
        //Obtengo los campos a validar
        String password = ((EditText)getView().findViewById(R.id.new_password)).getText().toString();
        String repeatedPassword = ((EditText)getView().findViewById(R.id.repeated_new_password)).getText().toString();

        //Valido el campo de password
        boolean validPassword = (password != null && password.length() >= 8 && password.length() <= 20);
        if(!validPassword){
            String errorMsg;
            if(password == null || password.length() == 0) {
                errorMsg = getString(R.string.required_error);
            } else {
                errorMsg = getString(R.string.length_error);
            }
            ((EditText)getView().findViewById(R.id.new_password)).setError(errorMsg);
        }
        //Valido caracteres inválidos
        if(validPassword) {
            for (String forbiddenChar : FORBIDDEN_CHARACTERS) {
                if (password.indexOf(forbiddenChar) >= 0) {
                    validPassword = false;
                }
            }
            if (!validPassword) {
                ((EditText) getView().findViewById(R.id.new_password)).setError(getString(R.string.password_hints));
            }
        }

        //Valido el campo repeatedPassword
        boolean validRepeatedPassword = (repeatedPassword != null && password.length() > 0 && password.equals(repeatedPassword));
        if(!validRepeatedPassword){
            String errorMsg;
            if(repeatedPassword == null || repeatedPassword.length() == 0){
                errorMsg = getString(R.string.required_error);
            } else {
                errorMsg = getString(R.string.equals_pass_error);
            }
            ((EditText)getView().findViewById(R.id.repeated_new_password)).setError(errorMsg);
        }
        return validPassword && validRepeatedPassword;
    }

    private boolean validateSecretAnswer() {
        //Obtengo los campos a validar
        String secretAnswer = ((EditText)getView().findViewById(R.id.new_secret_answer)).getText().toString();

        //Valido el campo secretAnswer
        if (secretAnswer != null && secretAnswer.length() >= 8 && secretAnswer.length() <= 20){
            return true;
        } else {
            String errorMsg;
            if(secretAnswer == null || secretAnswer.length() == 0){
                errorMsg = getString(R.string.required_error);
            } else {
                errorMsg = getString(R.string.length_error);
            }
            ((EditText)getView().findViewById(R.id.new_secret_answer)).setError(errorMsg);
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
