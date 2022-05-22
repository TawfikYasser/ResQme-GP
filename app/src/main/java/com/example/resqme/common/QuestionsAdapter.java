package com.example.resqme.common;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.resqme.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resqme.customer.SparePartsDetails;
import com.example.resqme.model.Question;
import com.example.resqme.model.QuestionReply;
import com.example.resqme.model.Report;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.MyQuestionsAdapterViewHolder> {
    ArrayList<Question> questions;
    Context context;

    public QuestionsAdapter(ArrayList<Question> questions, Context context) {
        this.questions = questions;
        this.context = context;
    }

    @NonNull
    @Override
    public MyQuestionsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item_design, parent, false);
        return new QuestionsAdapter.MyQuestionsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyQuestionsAdapterViewHolder holder, int position) {
        Question question = questions.get(position);
        holder.tvQuestionText.setText(question.getQuestionText());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Go to details page
                Intent toToDetailsQuestionPage = new Intent(context, DetailsQuestion.class);
                toToDetailsQuestionPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                toToDetailsQuestionPage.putExtra("QUESTION_ID",question.getQuestionID());
                toToDetailsQuestionPage.putExtra("CUSTOMER_ID",question.getQuestionCustomerID());
                toToDetailsQuestionPage.putExtra("QUESTION_TEXT",question.getQuestionText());
                context.startActivity(toToDetailsQuestionPage);
                LogData.saveLog("APP_CLICK","","","CLICK ON QUESTION", "QUESTIONS");
            }
        });
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public class MyQuestionsAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionText;
        public MyQuestionsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionText = itemView.findViewById(R.id.question_text_item_text);
        }
    }
}
