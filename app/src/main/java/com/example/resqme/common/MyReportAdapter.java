package com.example.resqme.common;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resqme.R;
import com.example.resqme.model.Report;

import java.util.ArrayList;

public class MyReportAdapter extends RecyclerView.Adapter<MyReportAdapter.MyViewHolder> {

    Context context;
    ArrayList<Report> reports;

    public MyReportAdapter(Context context, ArrayList<Report> reports) {
        this.context = context;
        this.reports = reports;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_reports_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Report report = reports.get(position);
        holder.tvReportDescription.setText(report.getReportDescription());
        if(report.getReportStatus().equals("Pending")){
            holder.tvReportStatus.setText("جاري المراجعة");
            holder.tvReportStatus.setTextColor(Color.rgb(255, 166, 53));
        }else{
            holder.tvReportStatus.setText("تم الرد على المشكلة، راجع البريد الإلكتروني...");
            holder.tvReportStatus.setTextColor(Color.rgb(20, 155, 24));
        }

    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvReportDescription, tvReportStatus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReportDescription = itemView.findViewById(R.id.report_description_item_text);
            tvReportStatus = itemView.findViewById(R.id.report_status_item_text);
        }
    }
}
