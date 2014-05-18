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

    private boolean validatePasswords() {
        //Obtengo los campos a validar y componente de error
        String password = ((EditText)getView().findViewById(R.id.new_password)).getText().toString();
        String repeatedPassword = ((EditText)getView().findViewById(R.id.repeated_new_password)).getText().toString();
        TextView passwordErrorView = (TextView) getView().findViewById(R.id.new_password_error);
        TextView repeatedPasswordErrorView = (TextView) getView().findViewById(R.id.repeated_new_password_error);

        //Valido el campo de password
        boolean validPassword = (password != null && password.length() >= 8 && password.length() <= 20);
        if(!validPassword){
            String errorMsg;
            if(password == null || password.length() == 0){
                errorMsg = getString(R.string.required);
            } else {
                errorMsg = getString(R.string.password_hints);
            }
            passwordErrorView.setText(errorMsg);
            passwordErrorView.setVisibility(View.VISIBLE);
        } else {
            passwordErrorView.setVisibility(View.GONE);
        }

        //Valido el campo repeatedPassword
        boolean validRepeatedPassword = (repeatedPassword != null && password.length() > 0 && password.equals(repeatedPassword));
        if(!validRepeatedPassword){
            String errorMsg;
            if(repeatedPassword == null || repeatedPassword.length() == 0){
                errorMsg = getString(R.string.required);
            } else {
                errorMsg = getString(R.string.repeated_password_error);
            }
            repeatedPasswordErrorView.setText(errorMsg);
            repeatedPasswordErrorView.setVisibility(View.VISIBLE);
        } else {
            repeatedPasswordErrorView.setVisibility(View.GONE);
        }
        return validPassword && validRepeatedPassword;
    }

    private boolean validateSecretAnswer() {
        //Obtengo los campos a validar y componente de error
        String secretAnswer = ((EditText)getView().findViewById(R.id.new_secret_answer)).getText().toString();
        TextView secretAnswerErrorView = (TextView) getView().findViewById(R.id.new_secret_answer_error);

        //Valido el campo secretAnswer
        if (secretAnswer != null && secretAnswer.length() >= 8 && secretAnswer.length() <= 20){
            secretAnswerErrorView.setVisibility(View.GONE);
            return true;
        } else {
            String errorMsg;
            if(secretAnswer == null || secretAnswer.length() == 0){
                errorMsg = getString(R.string.required);
            } else {
                errorMsg = getString(R.string.secret_answer_error);
            }
            secretAnswerErrorView.setText(errorMsg);
            secretAnswerErrorView.setVisibility(View.VISIBLE);
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
