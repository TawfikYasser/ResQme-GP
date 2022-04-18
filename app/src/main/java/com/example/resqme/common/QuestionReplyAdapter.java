package com.example.resqme.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resqme.R;
import com.example.resqme.model.QuestionReply;
import com.google.android.material.button.MaterialButton;

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
        holder.reportReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "إبلاغ", Toast.LENGTH_SHORT).show();
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
