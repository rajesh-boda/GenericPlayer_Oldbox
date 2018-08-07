package com.ideabytes.qezytv.genericplayer.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ideabytes.qezytv.genericplayer.R;

/************************************************************
 * Copy right @Ideabytes Software India Private Limited
 * Web site : http://ideabytes.com
 FragmentExit * author:  Suman
 * Created Date : 23-12-2015
 * Description : This fragment is to quit app
 * Modified Date : 15-04-2016
 * Reason: Password validation added to check pwd field filled
 *************************************************************/
public class FragmentExit extends Fragment {
    private static final String LOGTAG = "FragmentExit";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frgament_exit, container, false);
        final Button btnExit = (Button) view.findViewById(R.id.btnExit);
        final EditText etExitPassword = (EditText) view.findViewById(R.id.etPassword);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userValuePwd = etExitPassword.getText().toString();
                if (!userValuePwd.equals("")) {
                    String adminPwd = getActivity().getResources().getString(R.string.exitpassword);
                //if (userValuePwd.equalsIgnoreCase(adminPwd)) {
                if (userValuePwd.equals(adminPwd)) {
                    quitApplication();
                } else {
                    //make empty after clicking on Exit button
                    etExitPassword.setText("");
                    Toast.makeText(getActivity().getApplicationContext(), "Invalid Password", Toast.LENGTH_LONG).show();
                }
            } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Please Enter Password", Toast.LENGTH_LONG).show();
                }
            }
        });
       // Log.d(LOGTAG, "FragmentExit ");
        return view;
    }
    /**
     * This method is to Quit the application
     *
     * @author suman
     *
     */
    private void quitApplication() {
       getActivity().moveTaskToBack(true);
        getActivity().finish();
    }//quitApplication()


}
