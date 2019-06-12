package com.kash.distanttester2tr;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login_activity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user;
    EditText email;
    EditText password;
    Button signInButton;
    Button registerButton;


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseApp.initializeApp(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user!=null){
                    email.setText(user.getEmail());
                }
                else{

                }
            }
        };
        email = findViewById(R.id.emailET);
        password = findViewById(R.id.passwordET);
        signInButton = findViewById(R.id.sign_in_button);
        registerButton =findViewById(R.id.register_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailSTR = email.getText().toString();
                String passwordSTR = password.getText().toString();
                if(signIn(emailSTR,passwordSTR)){
                    Intent toMainWindow = new Intent(getApplicationContext(),main_test_list_activity.class);
                    startActivity(toMainWindow);
                    finish();
                }
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailSTR = email.getText().toString();
                String passwordSTR = password.getText().toString();
                Intent registerActivity = new Intent(getApplicationContext(),RegisterNewUserActivity.class);
                registerActivity.putExtra("login",emailSTR );
                registerActivity.putExtra("pass",passwordSTR);
                startActivity(registerActivity);
            }
        });
    }

    private boolean signIn(String emailSTR, String passwordSTR) {
        mAuth.signInWithEmailAndPassword(emailSTR,passwordSTR).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){Toast.makeText(getApplicationContext(),"Sign in successful",Toast.LENGTH_SHORT).show();}
                else{Toast.makeText(getApplicationContext(),"Sign in failed", Toast.LENGTH_LONG).show();}
            }
        });
        return mAuth.getCurrentUser().getEmail().equals(emailSTR);
    }
}
