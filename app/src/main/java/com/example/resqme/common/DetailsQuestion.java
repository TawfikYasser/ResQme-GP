package com.example.resqme.common;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.resqme.R;
import com.example.resqme.model.Question;
import com.example.resqme.model.QuestionReply;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DetailsQuestion extends AppCompatActivity {


    RecyclerView questionDetailsRV;
    DatabaseReference repliesDB;
    QuestionReplyAdapter questionReplyAdapter;
    ArrayList<QuestionReply> questionReplies;
    Context context;
    String questionID, questionText, questionCustomerID;
    TextView questionTextTV;
    ShimmerFrameLayout shimmerReplyLayout;
    LinearLayout noQuestionReplies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_question);
        forceRTLIfSupported();
        initToolbar();
        shimmerReplyLayout = findViewById(R.id.question_reply_layout_shimmer);
        shimmerReplyLayout.startShimmer();
        noQuestionReplies = findViewById(R.id.no_question_replies_layout);
        Intent intent = getIntent();
        questionID = intent.getStringExtra("QUESTION_ID");
        questionText = intent.getStringExtra("QUESTION_TEXT");
        questionCustomerID = intent.getStringExtra("CUSTOMER_ID");
        questionTextTV = findViewById(R.id.question_text_details_page);
        questionTextTV.setText(questionText);
        questionDetailsRV = findViewById(R.id.question_details_recycler);
        context = this;
        repliesDB = FirebaseDatabase.getInstance().getReference().child("Replies");
        questionDetailsRV.setHasFixedSize(true);
        questionDetailsRV.setLayoutManager(new LinearLayoutManager(this));
        questionReplies = new ArrayList<>();
        questionReplyAdapter = new QuestionReplyAdapter(questionReplies, this);
        questionDetailsRV.setAdapter(questionReplyAdapter);

        repliesDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!shimmerReplyLayout.isShimmerStarted()){
                    shimmerReplyLayout.startShimmer();
                    shimmerReplyLayout.setVisibility(View.VISIBLE);
                    questionDetailsRV.setVisibility(View.GONE);
                }
                questionReplies.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    if(!(dataSnapshot.getValue() instanceof String)){
                        QuestionReply questionReply = dataSnapshot.getValue(QuestionReply.class);
                        SharedPreferences userData = getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
                        String c_userid = userData.getString("C_USERID", "C_DEFAULT");
                        if(questionReply.getQuestionReplyQuestionID().equals(questionID)){
                            questionReplies.add(questionReply);
                            if(questionReplies.size() !=0){
                                questionReplyAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
                shimmerReplyLayout.stopShimmer();
                shimmerReplyLayout.setVisibility(View.GONE);
                questionDetailsRV.setVisibility(View.VISIBLE);
                if(questionReplies.size() == 0){
                    noQuestionReplies.setVisibility(View.VISIBLE);
                }else{
                    noQuestionReplies.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        repliesDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                questionReplies.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    if(!(dataSnapshot.getValue() instanceof String)){
                        QuestionReply questionReply = dataSnapshot.getValue(QuestionReply.class);
                        SharedPreferences userData = getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
                        String c_userid = userData.getString("C_USERID", "C_DEFAULT");
                        if(questionReply.getQuestionReplyQuestionID().equals(questionID)) {
                            questionReplies.add(questionReply);
                            if (questionReplies.size() != 0) {
                                questionReplyAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
                if(questionReplies.size() == 0){
                    questionReplies.clear();
                    questionReplyAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_question_details);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("تفاصيل السؤال");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(DetailsQuestion.this, R.style.Theme_ResQme);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void forceRTLIfSupported() {
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}