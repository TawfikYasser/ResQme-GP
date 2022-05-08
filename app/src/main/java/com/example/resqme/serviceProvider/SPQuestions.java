package com.example.resqme.serviceProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.resqme.R;
import com.example.resqme.common.Questions;
import com.example.resqme.common.QuestionsAdapter;
import com.example.resqme.customer.WinchRequests;
import com.example.resqme.model.Question;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SPQuestions extends AppCompatActivity {

    RecyclerView questionRV;
    DatabaseReference questionDB;
    SPQuestionsAdapter questionsAdapter;
    ArrayList<Question> questions;
    Context context, context_2;;
    View view;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spquestions);
        initToolbar();
        forceRTLIfSupported();
        mAuth = FirebaseAuth.getInstance();
        questionRV = findViewById(R.id.sp_questions_recycler);
        context = SPQuestions.this;
        context_2 = SPQuestions.this;
        view = this.getWindow().getDecorView().getRootView();
        questionDB = FirebaseDatabase.getInstance().getReference().child("Questions");
        questionRV.setHasFixedSize(true);
        questionRV.setLayoutManager(new LinearLayoutManager(this));
        questions = new ArrayList<>();
        questionsAdapter = new SPQuestionsAdapter(questions, this, context_2, view);
        questionRV.setAdapter(questionsAdapter);

        questionDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                questions.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    if(!(dataSnapshot.getValue() instanceof String)){
                        Question question = dataSnapshot.getValue(Question.class);
                        questions.add(question);
                        if(questions.size() !=0){
                            questionsAdapter = new SPQuestionsAdapter(questions, context, context_2, view);
                            questionRV.setAdapter(questionsAdapter);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        questionDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                questions.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    if(!(dataSnapshot.getValue() instanceof String)){
                        Question question = dataSnapshot.getValue(Question.class);
                        questions.add(question);
                        if(questions.size() !=0){
                            questionsAdapter = new SPQuestionsAdapter(questions, context, context_2, view);
                            questionRV.setAdapter(questionsAdapter);
                        }
                    }
                }
                if(questions.size() == 0){
                    questions.clear();
                    questionsAdapter = new SPQuestionsAdapter(questions, context, context_2, view);
                    questionRV.setAdapter(questionsAdapter);
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
        Toolbar toolbar = findViewById(R.id.toolbar_sp_questions);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("الأسئلة");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(SPQuestions.this, R.style.Theme_ResQme);
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