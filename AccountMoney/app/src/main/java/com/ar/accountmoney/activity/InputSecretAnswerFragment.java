package com.ar.accountmoney.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


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
                activityCallback.onConfirmPassword();
            }
        });
        return view;
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
