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
import com.example.resqme.common.MyReportAdapter;
import com.example.resqme.model.Report;
import com.example.resqme.model.SparePart;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class SpareFragment extends Fragment {

    public SpareFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    //InitViews
    RecyclerView sparepartsRV;
    DatabaseReference sparepartsDB;
    SparePartsAdapter sparepartsAdapter;
    ArrayList<SparePart> spareParts;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_spare, container, false);

        sparepartsRV = view.findViewById(R.id.spare_parts_recycler);
        context = getActivity().getApplicationContext();
        //sparepartsDB = FirebaseDatabase.getInstance().getReference().child("SpareParts");
        sparepartsRV.setHasFixedSize(true);
        sparepartsRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        spareParts = new ArrayList<>();

        spareParts.add(new SparePart("","إطارات West Lake ", "https://firebasestorage.googleapis.com/v0/b/resqme-60664.appspot.com/o/sparepartsdefaultimage.jpg?alt=media&token=2a65e70b-716c-4cff-b00d-31b5118b0cda"
        ,"500 جنيه", "جديد", "", "","مرسيدس", "Available"));

        spareParts.add(new SparePart("","طقم مرايا يمين وشمال", "https://firebasestorage.googleapis.com/v0/b/resqme-60664.appspot.com/o/sparepartsdefaultimage.jpg?alt=media&token=2a65e70b-716c-4cff-b00d-31b5118b0cda"
                ,"300 جنيه", "مستعمل", "", "","هونداي", "Available"));

        spareParts.add(new SparePart("","مضخم صوت", "https://firebasestorage.googleapis.com/v0/b/resqme-60664.appspot.com/o/sparepartsdefaultimage.jpg?alt=media&token=2a65e70b-716c-4cff-b00d-31b5118b0cda"
                ,"270 جنيه", "جديد", "", "","الجميع", "Available"));

        sparepartsAdapter = new SparePartsAdapter(getActivity(), spareParts);
        sparepartsRV.setAdapter(sparepartsAdapter);

        return view;
    }
}