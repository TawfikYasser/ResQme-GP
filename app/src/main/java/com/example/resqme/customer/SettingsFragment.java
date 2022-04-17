package com.example.resqme.customer;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.resqme.R;
import com.example.resqme.common.AboutUs;
import com.example.resqme.common.ContactUs;
import com.example.resqme.common.Login;
import com.example.resqme.common.MyReports;
import com.example.resqme.common.Questions;
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
    Button logoutBtn, reportsBtn, aboutusBtn, contactusBtn, viewReportsBtn, requestsBTN, cartBTN,
    askQuestionSettingsBtn, showQuestionsSettingsCustomer;
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

        viewReportsBtn = v.findViewById(R.id.reports_list_btn);
        viewReportsBtn.setOnClickListener((View.OnClickListener)this);

        aboutusBtn = v.findViewById(R.id.aboutusBtn);
        aboutusBtn.setOnClickListener((View.OnClickListener)this);

        contactusBtn = v.findViewById(R.id.contactusBtn);
        contactusBtn.setOnClickListener((View.OnClickListener)this);

        requestsBTN = v.findViewById(R.id.requestsBtn);
        requestsBTN.setOnClickListener((View.OnClickListener)this);

        cartBTN = v.findViewById(R.id.cartBtn);
        cartBTN.setOnClickListener((View.OnClickListener)this);

        askQuestionSettingsBtn = v.findViewById(R.id.askQuestionSettings);
        askQuestionSettingsBtn.setOnClickListener((View.OnClickListener)this);


        showQuestionsSettingsCustomer = v.findViewById(R.id.questionsBtnCustomerSettings);
        showQuestionsSettingsCustomer.setOnClickListener((View.OnClickListener)this);


        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.logout_customer_profile:
                new AlertDialog.Builder(getActivity())
                        .setTitle("تسجيل الخروج")
                        .setMessage("هل أنت متأكد انك تريد تسجيل الخروج؟")
                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mAuth.signOut();
                                sendToLogin();

                            }
                        })
                        .setNegativeButton("لا", null)
                        .show();
                break;
            case R.id.reportsBtn:
                sendReport();
                break;
            case R.id.reports_list_btn:
                sendToMyReports();
                break;
            case R.id.aboutusBtn:
                Intent intentAboutUs = new Intent(getActivity(), AboutUs.class);
                startActivity(intentAboutUs);
                break;
            case R.id.contactusBtn:
                Intent intentContactUs = new Intent(getActivity(), ContactUs.class);
                startActivity(intentContactUs);
                break;
            case R.id.requestsBtn:
                goToRequests();
                break;
            case R.id.cartBtn:
                Intent intentToCart = new Intent(getActivity(), CartForCustomer.class);
                startActivity(intentToCart);
                break;
            case R.id.askQuestionSettings:
                Intent goToAskQuestionPage = new Intent(getActivity(), AskQuestion.class);
                startActivity(goToAskQuestionPage);
                break;
            case R.id.questionsBtnCustomerSettings:
                Intent goToQuestionsPage = new Intent(getActivity(), Questions.class);
                startActivity(goToQuestionsPage);
                break;

        }
    }

    private void goToRequests() {
        String[] Requests = {"طلبات الونش", "طلبات مراكز الخدمة", "طلبات قطع الغيار"};
        final String[] selectedRequestType = {"طلبات الونش"};
        new AlertDialog.Builder(getContext())
                .setTitle("طلباتك")
                .setSingleChoiceItems(Requests, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedRequestType[0] = Requests[i];
                    }
                })
                .setPositiveButton("عرض", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(selectedRequestType[0].equals("طلبات الونش")){
                            Intent goToWinchRequests = new Intent(getActivity(), WinchRequests.class);
                            startActivity(goToWinchRequests);
                        }else if(selectedRequestType[0].equals("طلبات مراكز الخدمة")){
                            Intent goToCMCRequests = new Intent(getActivity(), CMCRequests.class);
                            startActivity(goToCMCRequests);
                        }else if(selectedRequestType[0].equals("طلبات قطع الغيار")){
                            Intent goToSpareRequests = new Intent(getActivity(), SparePartsRequests.class);
                            startActivity(goToSpareRequests);
                        }
                    }
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }

    private void sendReport() {
        Intent intent = new Intent(getActivity(), SendReport.class);
        startActivity(intent);
    }

    private void sendToMyReports() {
        Intent intent = new Intent(getActivity(), MyReports.class);
        startActivity(intent);
    }


    void sendToLogin() {
        SharedPreferences settings = getActivity().getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
        settings.edit().clear().commit();
        SharedPreferences sp = getActivity().getSharedPreferences("SP_LOCAL_DATA", Context.MODE_PRIVATE);
        sp.edit().clear().commit();
        Intent intent = new Intent(getActivity(), Login.class);
        startActivity(intent);
        getActivity().finish();
    }
}