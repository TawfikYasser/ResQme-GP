package com.example.resqme.customer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resqme.R;
import com.example.resqme.common.MyReportAdapter;
import com.example.resqme.model.WinchRequest;

import java.util.ArrayList;

public class WinchRequestsAdapter extends RecyclerView.Adapter<WinchRequestsAdapter.MyWinchAdapterViewHolder> {

    Context context;
    ArrayList<WinchRequest> winchRequests;

    public WinchRequestsAdapter(Context context, ArrayList<WinchRequest> winchRequests) {
        this.context = context;
        this.winchRequests = winchRequests;
    }

    @NonNull
    @Override
    public MyWinchAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_reports_item, parent, false);
        return new WinchRequestsAdapter.MyWinchAdapterViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return winchRequests.size();
    }

    @Override
    public void onBindViewHolder(@NonNull MyWinchAdapterViewHolder holder, int position) {

    }

    class MyWinchAdapterViewHolder extends RecyclerView.ViewHolder{

        public MyWinchAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

        }
    }
}
