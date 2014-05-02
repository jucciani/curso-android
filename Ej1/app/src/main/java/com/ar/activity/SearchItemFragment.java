package com.ar.activity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ar.R;

/**
 * Created by jucciani on 02/05/14.
 */
public class SearchItemFragment extends Fragment {

    private static EditText editText;
    SearchItemListener activityCallback;


    public interface SearchItemListener {
        public void onSearchButtonClick(String query);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.search_item, container, false);
        final Button button = (Button) view.findViewById(R.id.searchItemButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchItem(view);
            }
        });
        editText = (EditText) view.findViewById(R.id.query);
        return view;
    }

    public void searchItem(View view) {
        activityCallback.onSearchButtonClick(editText.getText().toString());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            activityCallback = (SearchItemListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SearchItemListener");
        }
    }
}
