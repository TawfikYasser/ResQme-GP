package com.example.resqme.customer;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
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
import com.example.resqme.common.LogData;
import com.example.resqme.common.Login;
import com.example.resqme.common.MyReports;
import com.example.resqme.common.Questions;
import com.example.resqme.serviceProvider.ServiceProviderSettings;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.Locale;
import java.util.Objects;

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
    Locale locale;
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

        if(Locale.getDefault().getLanguage().equals("ar")){
            locale = new Locale("en");
            Locale.setDefault(locale);
            Resources resources = getActivity().getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }else{
            locale = new Locale("en");
        }

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
                new AlertDialog.Builder(getActivity(), R.style.AlertDialogCustom)
                        .setTitle("تسجيل الخروج")
                        .setMessage("هل أنت متأكد انك تريد تسجيل الخروج؟")
                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                LogData.saveLog("APP_CLICK","","","CLICK ON LOGOUT BUTTON", "SETTINGS");
                                sendToLogin();
                            }
                        })
                        .setNegativeButton("لا", null)
                        .show();
                break;
            case R.id.reportsBtn:
                sendReport();
                LogData.saveLog("APP_CLICK","","","CLICK ON ADD REPORT PAGE", "SETTINGS");
                break;
            case R.id.reports_list_btn:
                sendToMyReports();
                LogData.saveLog("APP_CLICK","","","CLICK ON SHOW REPORTS PAGE", "SETTINGS");

                break;
            case R.id.aboutusBtn:
                Intent intentAboutUs = new Intent(getActivity(), AboutUs.class);
                startActivity(intentAboutUs);
                LogData.saveLog("APP_CLICK","","","CLICK ON ABOUT US PAGE", "SETTINGS");
                break;
            case R.id.contactusBtn:
                Intent intentContactUs = new Intent(getActivity(), ContactUs.class);
                startActivity(intentContactUs);
                LogData.saveLog("APP_CLICK","","","CLICK ON CONTACT US PAGE", "SETTINGS");
                break;
            case R.id.requestsBtn:
                goToRequests();
                break;
            case R.id.cartBtn:
                Intent intentToCart = new Intent(getActivity(), CartForCustomer.class);
                startActivity(intentToCart);
                LogData.saveLog("APP_CLICK","","","CLICK ON CART PAGE", "SETTINGS");
                break;
            case R.id.askQuestionSettings:
                Intent goToAskQuestionPage = new Intent(getActivity(), AskQuestion.class);
                startActivity(goToAskQuestionPage);
                LogData.saveLog("APP_CLICK","","","CLICK ON ASK QUESTION PAGE", "SETTINGS");
                break;
            case R.id.questionsBtnCustomerSettings:
                Intent goToQuestionsPage = new Intent(getActivity(), Questions.class);
                startActivity(goToQuestionsPage);
                LogData.saveLog("APP_CLICK","","","CLICK ON SHOW QUESTIONS PAGE", "SETTINGS");
                break;

        }
    }

    private void goToRequests() {
        String[] Requests = {"طلبات الونش", "طلبات مراكز الخدمة", "طلبات قطع الغيار"};
        final String[] selectedRequestType = {"طلبات الونش"};
        new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom)
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
                            LogData.saveLog("APP_CLICK","","","CLICK ON SHOW WINCH REQUESTS PAGE", "SETTINGS");
                        }else if(selectedRequestType[0].equals("طلبات مراكز الخدمة")){
                            Intent goToCMCRequests = new Intent(getActivity(), CMCRequests.class);
                            startActivity(goToCMCRequests);
                            LogData.saveLog("APP_CLICK","","","CLICK ON SHOW CMC REQUESTS PAGE", "SETTINGS");
                        }else if(selectedRequestType[0].equals("طلبات قطع الغيار")){
                            Intent goToSpareRequests = new Intent(getActivity(), SparePartsRequests.class);
                            startActivity(goToSpareRequests);
                            LogData.saveLog("APP_CLICK","","","CLICK ON SHOW SPARE PARTS REQUESTS PAGE", "SETTINGS");
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
        mAuth.signOut();
        File deletePrefFile = new File("/data/data/com.example.resqme/shared_prefs/CUSTOMER_LOCAL_DATA.xml");
        deletePrefFile.delete();
        File deletePref2File = new File("/data/data/com.example.resqme/shared_prefs/CAR_LOCAL_DATA.xml");
        deletePref2File.delete();
        Intent intent = new Intent(getActivity(), Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}