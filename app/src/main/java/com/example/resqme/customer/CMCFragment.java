package com.example.resqme.customer;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.resqme.R;
import com.example.resqme.model.CMC;
import com.example.resqme.model.SparePart;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class CMCFragment extends Fragment {


    public CMCFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    //InitViews
    RecyclerView cmcRV;
    DatabaseReference cmcDB;
    CMCAdapter cmcAdapter;
    ArrayList<CMC> cmcs;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_c_m_c, container, false);

        cmcRV = view.findViewById(R.id.cmc_recycler);
        context = getActivity().getApplicationContext();
        //sparepartsDB = FirebaseDatabase.getInstance().getReference().child("SpareParts");
        cmcRV.setHasFixedSize(true);
        cmcRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        cmcs = new ArrayList<>();

        cmcs.add(new CMC("","مركز البطل أوتو بلس", "https://firebasestorage.googleapis.com/v0/b/resqme-60664.appspot.com/o/UserImages%2FIMG_20220316_134648913.jpg?alt=media&token=f86bd598-5476-4562-8dcf-e15f36643ee5"
                ,"الطريق الدائرى . نزلة الكهرباء بجوار، محور 26 يوليو", "BMW", "", ""));

        cmcs.add(new CMC("","مركز الفاروق لخدمة السيارات", "https://firebasestorage.googleapis.com/v0/b/resqme-60664.appspot.com/o/UserImages%2FIMG_20220316_134648913.jpg?alt=media&token=f86bd598-5476-4562-8dcf-e15f36643ee5"
                ,"الطريق الدائري، أثر النبي، حي مصر القديمة", "الجميع", "", ""));


        cmcs.add(new CMC("","Renault Moqatam Service Center", "https://firebasestorage.googleapis.com/v0/b/resqme-60664.appspot.com/o/UserImages%2FIMG_20220316_134648913.jpg?alt=media&token=f86bd598-5476-4562-8dcf-e15f36643ee5"
                ,"Al Abageyah, El Khalifa, Cairo Governorate", "Renault", "", ""));


        cmcAdapter = new CMCAdapter(getActivity(), cmcs);
        cmcRV.setAdapter(cmcAdapter);

        return view;
    }
}