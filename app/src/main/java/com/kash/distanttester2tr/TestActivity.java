package com.kash.distanttester2tr;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TestActivity extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user;
    String testDoc;
    String _questions="Questions",_authorID="authorID",_description="description",_test="test",_title="title";
    TableLayout tl;
    TableRow tr;
    TextView authorNameText,testName;
    Button commitButton;
    Map<String, ArrayList<String>> alternatives;
    ArrayList<String> list_questions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        tl = findViewById(R.id.test_table);
        authorNameText=findViewById(R.id.test_authorLabel);
        testName=findViewById(R.id.test_name);
        commitButton= findViewById(R.id.send_test_result_button);
        db = FirebaseFirestore.getInstance();
        testDoc = getIntent().getStringExtra("DocumentReference");
        Log.d("Doc reference:",testDoc);
        db.collection("Tests").document(testDoc).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                alternatives = (Map)documentSnapshot.get(_test);

                list_questions = (ArrayList<String>) documentSnapshot.get(_questions);
                String[] questions = Arrays.copyOf(list_questions.toArray(),list_questions.size(),String[].class);
                long authorId=(long)documentSnapshot.get(_authorID);
                String title = (String)documentSnapshot.get(_title);

                String testerID=String.valueOf(authorId);
                db.collection("Users").document(testerID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String authorName = documentSnapshot.toObject(Users.class).getName();
                        authorNameText.setText("made by "+authorName);
                    }
                });
                testName.setText(title);
                int numOfQuestions=questions.length;

                Context appContext = getApplicationContext();
                int i=0;
                    for (String key   : alternatives.keySet()) {

                        Object[] value = alternatives.get(key).toArray();  //get() is less efficient


                        TableRow question_tableRow = new TableRow(appContext);
                        TableRow answers_tableRow = new TableRow(appContext);
                        RadioButton r1 = new RadioButton(appContext);
                        RadioButton r2 = new RadioButton(appContext);
                        RadioButton r3 = new RadioButton(appContext);
                        RadioButton r4 = new RadioButton(appContext);
                        RadioGroup ansGroup = new RadioGroup(appContext);
                        TextView question = new TextView(appContext);
                        question.setText(questions[i]);
                        r1.setText(String.valueOf(value[0]));
                        r2.setText(String.valueOf(value[1]));
                        r3.setText(String.valueOf(value[2]));
                        r4.setText(String.valueOf(value[3]));
                        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                        layoutParams.weight=1;
                        answers_tableRow.setWeightSum(4);
                        r1.setLayoutParams(layoutParams);
                        r2.setLayoutParams(layoutParams);
                        r3.setLayoutParams(layoutParams);
                        r4.setLayoutParams(layoutParams);


                        ansGroup.addView(r1, 0);
                        ansGroup.addView(r2, 1);
                        ansGroup.addView(r3, 2);
                        ansGroup.addView(r4, 3);
                        question_tableRow.addView(question);
                        tl.addView(question_tableRow);
                        answers_tableRow.addView(ansGroup);
                        tl.addView(answers_tableRow);
                        i++;
                        //проверь это дерьмо, чувак, а я пошел отдыхать
                    }
                }
        });
        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //CHECK THEM FOR BEING OK
                ArrayList<String> results=new ArrayList<>();
                ArrayMap<String,ArrayList<String>> allAlternatives = new ArrayMap<>();
                ArrayList<String>tempAnswer = new ArrayList<>();
                //get results
                int temp=0;
                try {
                    for (int i = 0; i < tl.getChildCount(); i++) {
                        View child = tl.getChildAt(i);
                        if (child instanceof TableRow) {
                            TableRow row = (TableRow) child;
                            for (int j = 0; j < row.getChildCount(); j++) {
                                View rowChild = row.getChildAt(j);
                                /*if (rowChild instanceof RadioGroup) {
                                    RadioGroup rg = (RadioGroup) rowChild;
                                    if (rg.getCheckedRadioButtonId() == -1) {
                                        throw new Exception("no data in " + i + " row");
                                    }
                                    RadioButton radio = findViewById(rg.getCheckedRadioButtonId());
                                    results.add(String.valueOf(radio.getText()));
                                    break;
                                }*/
                                if(rowChild instanceof RadioButton){
                                    RadioButton rb = (RadioButton)rowChild;
                                    if(rb.isSelected()){results.add(String.valueOf(rb.getText()));}
                                    tempAnswer.add(String.valueOf(rb.getText()));
                                }

                            }
                            allAlternatives.put(list_questions.get(temp),tempAnswer);
                            temp++;
                            tempAnswer.clear();
                            continue;
                        }

                    }
                    personalResUpload(list_questions,results);
                    overallResUpload(list_questions,results,allAlternatives);
                }
                catch(Exception e){
                    Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_LONG).show();}
            }
        });


    }

    private void overallResUpload(ArrayList<String> list_questions, ArrayList<String> results,ArrayMap<String, ArrayList<String>>allAlternatives) {
        String overallResultRef = String.valueOf(db.collection("Tests").document(testDoc).get().getResult().get("Overall_result_ref"));
        if(overallResultRef==null){
            ArrayMap<String,ArrayMap<String,Long>>sendable_result =new ArrayMap<>();
            for(int i=0; i<list_questions.size();i++){
                ArrayList<String> strings = allAlternatives.get(list_questions.get(i));
                ArrayMap<String, Long>rowWithVote=new ArrayMap<>();
                for (String str:strings) {

                    if(str==results.get(i)){
                        rowWithVote.put(str,Long.valueOf(1));
                    }
                    else{rowWithVote.put(str,Long.valueOf(0));
                    }
                }
                sendable_result.put(list_questions.get(i),rowWithVote);
                rowWithVote.clear();
            }
            Map<String,Object> toPost = new HashMap<>();
            toPost.put("results",sendable_result);
            toPost.put("Overall_result_ref",testDoc);
            toPost.put("numberOfTested",1);

            //добавить что надо


            String testResult=db.collection("Results_overall").document().getId();
            db.collection("Results_overall").document(testResult).set(sendable_result);
        }
        else{
            DocumentSnapshot results_overall = db.collection("Results_overall").document(overallResultRef).get().getResult();
            ArrayMap<String, ArrayMap<String,Long>> testResult = (ArrayMap)results_overall.get("results");
            long numberOfTested = (long)results_overall.get("numberOfTested");
            numberOfTested++;
            int pos=0;
            for(String question:list_questions){
                ArrayMap<String,Long> questRes=testResult.get(question);
                long vote = questRes.get(results.get(pos));
                vote++;
                questRes.put(results.get(pos),vote);
                testResult.put(question,questRes);
                pos++;

            }
            Map<String,Object> toPost = new HashMap<>();
            toPost.put("results",testResult);
            toPost.put("numberOfTested",numberOfTested);
            db.collection("Results_overall").document(overallResultRef).set(toPost,SetOptions.merge());
            //read ref
            //read result data
            //increment where needed
        }
    }

    private void personalResUpload(ArrayList<String> questions,ArrayList<String> results) {
        //create new entry in Personal results
        Map<String, String>questionsAndAnswers=new HashMap<>();
        for(int i=0; i<questions.size();i++){
            questionsAndAnswers.put(questions.get(i),results.get(i));
        }
        String userID=mAuth.getUid();
        String testID=testDoc;
        Map<String,Object>personalInfo=new HashMap<>();
        personalInfo.put("testID",testID);
        personalInfo.put("userID",userID);
        personalInfo.put("questions_and_answers",questionsAndAnswers);
        db.collection("Results_personal").document().set(personalInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Personal results upload:","OK");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Personal results upload:","Failure");
            }
        });
        ArrayList<String>donetests = (ArrayList)db.collection("Users").document(mAuth.getUid()).get().getResult().get("doneTests");
        if(donetests==null){
            donetests = new ArrayList<>();
        }
        donetests.add(testDoc);
        ArrayMap<String, ArrayList<String>> testsForDB=new ArrayMap();
        testsForDB.put("donetests",donetests);
        db.collection("Users").document(mAuth.getUid()).set(testsForDB, SetOptions.merge());
        //upload data
        //Optional update users document: add link to result

    }
}
