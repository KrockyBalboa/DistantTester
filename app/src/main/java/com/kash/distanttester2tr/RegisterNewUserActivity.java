package com.kash.distanttester2tr;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.StatementEvent;

public class RegisterNewUserActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;
    EditText emailET,passwordET,usernameET;
    Spinner jobSpinner;
    List<String> jobList;
    String email, password, job, userName;
    long jobID;

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_user);

        jobList=new ArrayList<>();
        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();



        jobSpinner = findViewById(R.id.job_spinner);
        Log.d("Debug spinner","OnCreate");

        jobSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                job= adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        addSpinnerInfoFromDB();
        emailET=findViewById(R.id.Email);
        passwordET=findViewById(R.id.Password);
        usernameET = findViewById(R.id.username_register);
        emailET.setText(getIntent().getStringExtra("login"));
        passwordET.setText(getIntent().getStringExtra("pass"));

        Button regButton = findViewById(R.id.register_new_user_button);
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerNewUser();

            }
        });
        if(user==null){
            Log.d("User Authentication","user is null, trying to sign in anonymously");
            mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Log.d("User Authentication","User signed in anonymously");
                        user = mAuth.getCurrentUser();
                    }
                    else{
                        Log.w("User Authentication", "signInAnonymously:failure", task.getException());
                        Toast.makeText(getApplicationContext(),"Authentication failed", Toast.LENGTH_SHORT).show();

                    }
                }
            });

        }
        else{Log.d("User Authentication","User is not null");}

    }
    private void registerNewUser(){
         email = emailET.getText().toString().trim();
         password = passwordET.getText().toString().trim();
         userName = usernameET.getText().toString().trim();
         Log.d("Email and pass check:","Email:"+email+"\t Pass:"+password);
        AuthCredential cred = EmailAuthProvider.getCredential(email, password);
        mAuth.getCurrentUser().linkWithCredential(cred).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task <AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"Successfully linked anon and cred",Toast.LENGTH_SHORT).show();
                }
                else{Toast.makeText(getApplicationContext(),"Linking failed", Toast.LENGTH_LONG).show();
                Log.w("Linking fail",task.getException());
                }
            }
        });

        addUserInfo();
        Intent toMainActivity = new Intent(getApplicationContext(),main_test_list_activity.class);
        startActivity(toMainActivity);
        finish();
    }

    private void addUserInfo() {
        Map<String, Object> userInfo = new HashMap<>();
        getJobID(job);
        userInfo.put("login", email);
        userInfo.put("password",password);
        userInfo.put("name",userName);
        userInfo.put("jobID",jobID);
        db.collection("Users").document(mAuth.getUid()).set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Additional info add","DocumentSnapshot added with id "+"not yet known");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Additional info add","Error adding new info",e);
            }
        });
        /*db.collection("Users").add(userInfo).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        myRef.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(userInfo);*/
    }

    private void getJobID(String job) {
        db.collection("Jobs").document(job).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                jobID = (long) task.getResult().get("code");
            }
        });


    }

    private void addSpinnerInfoFromDB() {

        //myRef=FirebaseDatabase.getInstance().getReference();
        Log.d("Debug spinner","Trying to initialize spinner");
        db.collection("Jobs").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Jobs> jList = queryDocumentSnapshots.toObjects(Jobs.class);
                for (Jobs j :jList
                     ) {
                    jobList.add(j.getName());
                }
                updateUI();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Debug spinner", "Couldnt read db for spinner: \t",e);
            }
        });



        /*
        FirebaseUser user = mAuth.getCurrentUser();
        myRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        */
    }
    private void updateUI() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),R.layout.support_simple_spinner_dropdown_item,jobList);
        jobSpinner.setAdapter(adapter);
        jobSpinner.setSelection(0);
    }
}
