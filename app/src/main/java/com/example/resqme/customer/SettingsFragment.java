package com.example.resqme.customer;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.resqme.R;
import com.example.resqme.common.Login;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment implements  View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FirebaseAuth mAuth;
    Button logoutBtn, reportsBtn,aboutusBtn,contactusBtn;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_settings, container, false);
        mAuth = FirebaseAuth.getInstance();
        //logoutBtn = logoutBtn.findViewById(R.id.logout_customer_profile);
        logoutBtn = (Button) v.findViewById(R.id.logout_customer_profile);

        logoutBtn.setOnClickListener((View.OnClickListener) this);

        reportsBtn=(Button) v.findViewById(R.id.reportsBtn);
        reportsBtn.setOnClickListener((View.OnClickListener) this);
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.logout_customer_profile:
                mAuth.signOut();
                sendToLogin();
                break;
            case R.id.reportsBtn:

                sendReport();
                break;

        }
    }

    private void sendReport() {
//        Intent i = new Intent(getActivity(), SendReport.class);
//        startActivity(i);
//        ((SendReport) getActivity()).overridePendingTransition(0, 0);
        Intent intent = new Intent(getActivity(), SendReport.class);
        startActivity(intent);
        }


    void sendToLogin() {
        Intent intent = new Intent(getActivity(), Login.class);
        startActivity(intent);
    }
}