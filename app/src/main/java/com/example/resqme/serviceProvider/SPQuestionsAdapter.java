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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resqme.R;
import com.example.resqme.common.DetailsQuestion;
import com.example.resqme.common.LogData;
import com.example.resqme.model.Question;
import com.example.resqme.model.QuestionReply;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SPQuestionsAdapter extends RecyclerView.Adapter<SPQuestionsAdapter.SPQuestionsViewHolder> {

    ArrayList<Question> questions;
    Context context, context_2;
    View view;

    public SPQuestionsAdapter(ArrayList<Question> questions, Context context, Context context2, View view) {
        this.questions = questions;
        this.context = context;
        this.context_2 = context2;
        this.view = view;
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



                BottomSheetDialog replyDialog;
                replyDialog = new BottomSheetDialog(context_2, R.style.BottomSheetDialogTheme);

                View rateBottomView = LayoutInflater.from(context_2).inflate(R.layout.reply_bottom_layout,
                        (LinearLayout) view.findViewById(R.id.bottom_sheet_replytoquestion_linear_layout));

                TextInputEditText etReplyToQuestion = rateBottomView.findViewById(R.id.reply_et_to_question);
                MaterialButton sendReplySheetBtn = rateBottomView.findViewById(R.id.sendReplyButtonSheet);


                replyDialog.setContentView(rateBottomView);
                replyDialog.show();

                sendReplySheetBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(!TextUtils.isEmpty(etReplyToQuestion.getText().toString().trim())){
                            new AlertDialog.Builder(context_2, R.style.AlertDialogCustom)
                                    .setTitle("إرسال رد على السؤال")
                                    .setMessage("هل أنت متأكد من إرسال هذا الرد؟")
                                    .setPositiveButton("تأكيد", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                                            String questionReplyID = database.getReference("Replies").push().getKey();// create new id
                                            QuestionReply questionReplyObj = new QuestionReply(questionReplyID, question.getQuestionID(),
                                                    FirebaseAuth.getInstance().getCurrentUser().getUid(), etReplyToQuestion.getText().toString().trim());
                                            DatabaseReference repliesDB = FirebaseDatabase.getInstance().getReference().child("Replies");
                                            repliesDB.child(questionReplyID).setValue(questionReplyObj);//Entering question in database
                                            Toast.makeText(context, "تم إرسال الرد", Toast.LENGTH_LONG).show();
                                            LogData.saveLog("APP_CLICK","","","CLICK ON SEND QUESTION REPLY BUTTON", "SERVICE_PROVIDER_SETTINGS");
                                            replyDialog.cancel();
                                        }
                                    })
                                    .setNegativeButton("رجوع", null)
                                    .show();
                        }else{
                            Toast.makeText(context, "يجب إدخال رد قبل الضغط على إرسال.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
