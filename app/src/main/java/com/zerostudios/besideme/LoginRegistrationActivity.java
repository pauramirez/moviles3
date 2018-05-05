package com.zerostudios.besideme;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.newrelic.agent.android.NewRelic;

public class LoginRegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    private EditText mEmail, mPassword;
    private Button mLogin, mRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_registration);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null)
                {
                    Intent intent = new Intent(LoginRegistrationActivity.this, MapsActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };


        mEmail =  findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mLogin =  findViewById(R.id.login);
        mRegister = findViewById(R.id.LoginRegister);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();

                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(LoginRegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful())
                        {
                            Toast.makeText(LoginRegistrationActivity.this, "Sign In Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginRegistrationActivity.this, RegistrationActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        NewRelic.withApplicationToken(
                "AA8a7edc14190634209597cd71a9bb11a87417c18e"
        ).start(this.getApplication());
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}
