package com.example.resqme.serviceProvider;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resqme.R;
import com.example.resqme.common.DetailsQuestion;
import com.example.resqme.model.Question;
import com.example.resqme.model.QuestionReply;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SPQuestionsAdapter extends RecyclerView.Adapter<SPQuestionsAdapter.SPQuestionsViewHolder> {

    ArrayList<Question> questions;
    Context context;

    public SPQuestionsAdapter(ArrayList<Question> questions, Context context) {
        this.questions = questions;
        this.context = context;
    }

    @NonNull
    @Override
    public SPQuestionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sp_questions_item_design, parent, false);
        return new SPQuestionsAdapter.SPQuestionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SPQuestionsViewHolder holder, int position) {
        Question question = questions.get(position);
        holder.sp_question.setText(question.getQuestionText());
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
            }
        });
        holder.sendReply_sp_item_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText input = new EditText(context);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                lp.setMargins(12, 12, 12, 12);
                input.setLayoutParams(lp);
                new AlertDialog.Builder(context)
                        .setTitle("إضافة رد على السؤال")
                        .setPositiveButton("إرسال", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Send the reply
                                if(!TextUtils.isEmpty(input.getText())){
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    String questionReplyID = database.getReference("Replies").push().getKey();// create new id
                                    QuestionReply questionReplyObj = new QuestionReply(questionReplyID, question.getQuestionID(),
                                            FirebaseAuth.getInstance().getCurrentUser().getUid(), input.getText().toString().trim());
                                    DatabaseReference repliesDB = FirebaseDatabase.getInstance().getReference().child("Replies");
                                    repliesDB.child(questionReplyID).setValue(questionReplyObj);//Entering question in database
                                    Toast.makeText(context, "تم إرسال الرد", Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(context, "يجب كتابة رد قبل الضغط على إرسال!", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setView(input)
                        .setNegativeButton("إلغاء", null)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public class SPQuestionsViewHolder extends RecyclerView.ViewHolder{

        TextView sp_question;
        MaterialButton sendReply_sp_item_question;

        public SPQuestionsViewHolder(@NonNull View itemView) {
            super(itemView);
            sp_question = itemView.findViewById(R.id.question_text_item_text_sp_item);
            sendReply_sp_item_question = itemView.findViewById(R.id.send_reply_on_question_btn);
        }
    }
}
