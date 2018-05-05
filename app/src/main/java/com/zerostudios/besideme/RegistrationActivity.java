package com.zerostudios.besideme;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    private EditText mFName, mLName, mEmail, mPassword;
    private RadioGroup mRadioGroup;
    private Button mRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null)
                {
                    Intent intent = new Intent(RegistrationActivity.this, MapsActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        mFName = findViewById(R.id.fname);
        mLName = findViewById(R.id.lname);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mRadioGroup = findViewById(R.id.radioGroup);
        mRegister = findViewById(R.id.register);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int selectedId = mRadioGroup.getCheckedRadioButtonId();
                final RadioButton radioButton = findViewById(selectedId);

                if(radioButton.getText()== null)
                {
                    return;
                }

                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                final String name = mFName.getText().toString() +" "+ mLName.getText().toString();


                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful())
                        {
                            Toast.makeText(RegistrationActivity.this, "Sign Up Error", Toast.LENGTH_SHORT).show();
                        }

                        else
                        {
                            String userId = mAuth.getCurrentUser().getUid();
                            DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(radioButton.getText().toString()).child(userId).child("name");
                            currentUserDb.setValue(name);
                        }
                    }
                });
            }
        });
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
