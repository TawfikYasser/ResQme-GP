package com.example.resqme.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resqme.R;
import com.example.resqme.model.QuestionReply;
import com.example.resqme.model.Report;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class QuestionReplyAdapter extends RecyclerView.Adapter<QuestionReplyAdapter.MyQuestionDetailsAdapterViewHolder>{
    ArrayList<QuestionReply> questionReplies;
    Context context;

    public QuestionReplyAdapter(ArrayList<QuestionReply> questionReplies, Context context) {
        this.questionReplies = questionReplies;
        this.context = context;
    }

    @NonNull
    @Override
    public MyQuestionDetailsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_reply_item_design, parent, false);
        return new QuestionReplyAdapter.MyQuestionDetailsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyQuestionDetailsAdapterViewHolder holder, int position) {
        QuestionReply questionReply = questionReplies.get(position);
        holder.tvQuestionReply.setText(questionReply.getQuestionReplyText());

        DatabaseReference repliesReports = FirebaseDatabase.getInstance().getReference().child("Reports");
        repliesReports.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Report report = dataSnapshot.getValue(Report.class);
                    if(report.getReportStatus().equals("REPLY_REPORT_PENDING")){
                        SharedPreferences userData = context.getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);//Pointer on local data
                        String c_userid = userData.getString("C_USERID","C_DEFAULT");
                        if(report.getUserID().equals(c_userid) &&
                                report.getReportDescription().equals(questionReply.getQuestionReplyID())){
                            holder.reportReply.setEnabled(false);
                        }
                    }else if(report.getReportStatus().equals("REPLY_REPORT_APPROVED")){
                        SharedPreferences userData = context.getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);//Pointer on local data
                        String c_userid = userData.getString("C_USERID","C_DEFAULT");
                        if(report.getUserID().equals(c_userid) &&
                                report.getReportDescription().equals(questionReply.getQuestionReplyID())){
                            holder.reportReply.setEnabled(true);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.reportReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(context)
                        .setTitle("إبلاغ عن الرد")
                        .setMessage("هل أنت متأكد من الإبلاغ عن هذا الرد؟")
                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                DatabaseReference reportsTable = FirebaseDatabase.getInstance().getReference().child("Reports");
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                String reportID = database.getReference("Reports").push().getKey();
                                SharedPreferences userData = context.getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);//Pointer on local data
                                String c_userid = userData.getString("C_USERID","C_DEFAULT");
                                String c_email = userData.getString("C_EMAIL", "C_DEFAULT");
                                Report report = new Report(questionReply.getQuestionReplyID(), reportID, c_userid,"REPLY_REPORT_PENDING", c_email);
                                reportsTable.child(reportID).setValue(report); //Entering report in database
                                Toast.makeText(context, "شكراً لمساعدنا، سنقوم في اسرع وقت بمراجعة هذا البلاغ وإتخاذ الإجراء اللازم.", Toast.LENGTH_LONG).show();
                                LogData.saveLog("APP_CLICK","","","CLICK ON REPORT QUESTION REPLY BUTTON", "QUESTION_DETAILS");
                            }
                        })
                        .setNegativeButton("لا", null)
                        .show();


            }
        });
    }

    @Override
    public int getItemCount() {
        return questionReplies.size();
    }

    public class MyQuestionDetailsAdapterViewHolder extends RecyclerView.ViewHolder{
        TextView tvQuestionReply;
        MaterialButton reportReply;
        public MyQuestionDetailsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionReply = itemView.findViewById(R.id.question_reply_text_item);
            reportReply = itemView.findViewById(R.id.report_reply_btn);
        }
    }
}
