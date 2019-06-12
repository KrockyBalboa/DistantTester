package com.kash.distanttester2tr;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Test_creating extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseFirestore db;
    Button addQuestionBut, submitTestBut;
    EditText testNameET,testDescrET;
    TableLayout tl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_creating);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        //String name = String.valueOf(db.collection("Users").document(user.getUid()).get().getResult().get("name"));
        //String userID=user.getUid();
        addQuestionBut = findViewById(R.id.questionAdd);
        submitTestBut = findViewById(R.id.testSubmit);
        final Context appContext = getApplicationContext();
        tl = findViewById(R.id.testCreationTable);
        addQuestionBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewQuestionSlot(appContext);
                createNewAlternativeSlot(appContext);


            }
        });
        submitTestBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    private void createNewAlternativeSlot(final Context appContext) {
        TableRow previousRow=(TableRow)tl.getChildAt(tl.getChildCount()-1);
        TextView previousText=(TextView)previousRow.getChildAt(0);
        int alternativeNum;
        if(previousText.getText().toString().contains("Alternative")){
            alternativeNum=Integer.parseInt(String.valueOf(previousText.getText().charAt(previousText.getText().length()-1)));
            alternativeNum++;
        }
        else{alternativeNum=1;}
        final TableRow alternativeRow = new TableRow(appContext);
        TextView alternativeDesc = new TextView(appContext);
        final EditText alternative = new EditText(appContext);
        final Button deleteAlternative = new Button(appContext);

        final Button addMoreAlternative = new Button(appContext);
        addMoreAlternative.setText("+");
        alternativeDesc.setText("Alternative #"+alternativeNum);
        alternative.setScroller(new Scroller(appContext));
        alternative.setMinLines(2);
        alternative.setMaxLines(5);
        deleteAlternative.setVisibility(View.GONE);
        deleteAlternative.setText("-");
        deleteAlternative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alternativeRow.removeAllViews();
                tl.removeView(alternativeRow);
            }
        });
        addMoreAlternative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMoreAlternative.setVisibility(View.GONE);
                deleteAlternative.setVisibility(View.VISIBLE);
                createNewAlternativeSlot(appContext);

            }
        });
        alternativeRow.addView(alternativeDesc);
        alternativeRow.addView(alternative);
        alternativeRow.addView(addMoreAlternative);
        alternativeRow.addView(deleteAlternative);
        tl.addView(alternativeRow);
    }

    private void createNewQuestionSlot(Context appContext) {
        TableRow questionRow = new TableRow(appContext);
        TextView questionDescr = new TextView(appContext);
        EditText question = new EditText(appContext);
        questionDescr.setText("Input your question");
        question.setHint("here");
        question.setMinLines(2);
        question.setMaxLines(5);
        question.setScroller(new Scroller(appContext));
        questionRow.addView(questionDescr);
        questionRow.addView(question);
        tl.addView(questionRow);
    }
}
