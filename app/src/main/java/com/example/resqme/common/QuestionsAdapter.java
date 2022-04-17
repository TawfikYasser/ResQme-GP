package com.example.resqme.common;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.resqme.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resqme.model.Question;
import com.example.resqme.model.Report;

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
