package com.example.resqme.common;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.example.resqme.R;
import com.example.resqme.customer.CustomerHome;
import com.example.resqme.model.Question;
import com.example.resqme.model.Report;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class Questions extends AppCompatActivity {


    RecyclerView questionRV;
    DatabaseReference questionDB;
    QuestionsAdapter questionsAdapter;
    ArrayList<Question> questions;
    Context context;
    LinearLayout noQuestions;
    Locale locale;
    ShimmerFrameLayout shimmerCustomerQuestions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        if(Locale.getDefault().getLanguage().equals("ar")){
            locale = new Locale("en");
            Locale.setDefault(locale);
            Resources resources = Questions.this.getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }else{
            locale = new Locale("en");
        }
        forceRTLIfSupported();
        initToolbar();
        shimmerCustomerQuestions = findViewById(R.id.questions_layout_customer_shimmer);
        shimmerCustomerQuestions.startShimmer();
        noQuestions = findViewById(R.id.no_questions_layout);
        questionRV = findViewById(R.id.questions_recycler);
        context = this.getApplicationContext();
        questionDB = FirebaseDatabase.getInstance().getReference().child("Questions");
        questionRV.setHasFixedSize(true);
        questionRV.setLayoutManager(new LinearLayoutManager(this));
        OverScrollDecoratorHelper.setUpOverScroll(questionRV, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        questions = new ArrayList<>();
        questionsAdapter = new QuestionsAdapter(questions, this);
        questionRV.setAdapter(questionsAdapter);

        questionDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!shimmerCustomerQuestions.isShimmerStarted()){
                    shimmerCustomerQuestions.startShimmer();
                    shimmerCustomerQuestions.setVisibility(View.VISIBLE);
                    questionRV.setVisibility(View.GONE);
                }
                questions.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    if(!(dataSnapshot.getValue() instanceof String)){
                        Question question = dataSnapshot.getValue(Question.class);
                        SharedPreferences userData = getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
                        String c_userid = userData.getString("C_USERID", "C_DEFAULT");
                        if(question.getQuestionCustomerID().equals(c_userid)){
                            questions.add(question);
                            if(questions.size() !=0){
                                questionsAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
                shimmerCustomerQuestions.stopShimmer();
                shimmerCustomerQuestions.setVisibility(View.GONE);
                questionRV.setVisibility(View.VISIBLE);
                if(questions.size() == 0){
                    noQuestions.setVisibility(View.VISIBLE);
                }else{
                    noQuestions.setVisibility(View.GONE);
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
                        SharedPreferences userData = getSharedPreferences("CUSTOMER_LOCAL_DATA", Context.MODE_PRIVATE);
                        String c_userid = userData.getString("C_USERID", "C_DEFAULT");
                        if(question.getQuestionCustomerID().equals(c_userid)){
                            questions.add(question);
                            if(questions.size() !=0){
                                questionsAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
                if(questions.size() == 0){
                    questions.clear();
                    questionsAdapter.notifyDataSetChanged();
                }
                if(questions.size() == 0){
                    noQuestions.setVisibility(View.VISIBLE);
                }else{
                    noQuestions.setVisibility(View.GONE);
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
        Toolbar toolbar = findViewById(R.id.toolbar_questions);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("الأسئلة");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextAppearance(Questions.this, R.style.Theme_ResQme);
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