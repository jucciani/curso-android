package com.ar.accountmoney.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class InputPasswordFragment extends Fragment {

    private IAccountMoneyListener activityCallback;

    /**
     * Crea una nueva instancia del InputPasswordFragment.
     */
    public static InputPasswordFragment newInstance() {
        InputPasswordFragment newFragment = new InputPasswordFragment();
        //Bundle args = new Bundle();
        //args.putString("PARAM", pram);
        //newFragment.setArguments(args);
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
