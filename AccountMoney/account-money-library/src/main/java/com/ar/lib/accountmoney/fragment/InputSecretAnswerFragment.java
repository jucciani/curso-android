package com.ar.lib.accountmoney.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.ar.lib.accountmoney.R;

import com.ar.lib.accountmoney.listener.IAccountMoneyListener;


public class InputSecretAnswerFragment extends Fragment {

    private IAccountMoneyListener activityCallback;
    public static final String SECRET_QUESTION = "SECRET_QUESTION";

    /**
     * Crea una nueva instancia del InputSecretAnswerFragment.
     */
    public static InputSecretAnswerFragment newInstance(String secretQuestion) {
        InputSecretAnswerFragment newFragment = new InputSecretAnswerFragment();
        Bundle args = new Bundle();
        args.putString(InputSecretAnswerFragment.SECRET_QUESTION, secretQuestion);
        newFragment.setArguments(args);
        return newFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_input_secret_answer, container, false);

        //Set secretQuestion
        ((TextView)view.findViewById(R.id.secret_question))
                .setText(getArguments().getString(InputSecretAnswerFragment.SECRET_QUESTION));
        //Submit button listener
        view.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onConfirmSecretAnswer();
            }
        });
        return view;
    }


    private void onConfirmSecretAnswer(){
        if(validateInputPassword()){
            activityCallback.onConfirmSecretAnswer();
        }
    }
    private boolean validateInputPassword() {
        //Obtengo los campos a validar
        EditText inputAnswer = (EditText)getView().findViewById(R.id.input_secret_answer);
        String answer = inputAnswer.getText().toString();

        //Valido el campo inputPassword
        if (answer != null && answer.length() > 0){
            return true;
        } else {
            inputAnswer.setError(getString(R.string.required_error));
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
