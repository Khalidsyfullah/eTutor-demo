package com.akapps.etutor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    EditText name, email, phonenumber, pass, cpass;
    RadioGroup radioGroup;
    String nstr, emailstr, phstr, pssstr, cpssstr, gender = "";
    FirebaseAuth mAuth;
    TextView back_btn;
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name = findViewById(R.id.editTextTextPersonName);
        email = findViewById(R.id.editTextTextEmailAddress);
        phonenumber = findViewById(R.id.editTextPhone);
        pass= findViewById(R.id.pass);
        cpass= findViewById(R.id.pass2);
        radioGroup = findViewById(R.id.radio);
        back_btn = findViewById(R.id.textView);

        mAuth = FirebaseAuth.getInstance();


        back_btn.setOnClickListener(v -> {
            //back pressed
            onBackButtonpressed();
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radioButton){
                    gender = "Male";
                }
                else{
                    gender = "Female";
                }
            }
        });

    }

    public void onregisterclicked(View view){
        nstr = name.getText().toString();
        emailstr = email.getText().toString();
        phstr = phonenumber.getText().toString();
        pssstr = pass.getText().toString();
        cpssstr = cpass.getText().toString();

        if(nstr.isEmpty()){
            name.setError("Required!");
            return;
        }
        if(emailstr.isEmpty()){
            email.setError("Required!");
            return;
        }
        if(phstr.isEmpty()){
            phonenumber.setError("Required!");
            return;
        }
        if(pssstr.isEmpty()){
            pass.setError("Required!");
            return;
        }
        if(cpssstr.isEmpty()){
            cpass.setError("Required!");
            return;
        }
        if(gender.isEmpty()){
            Toast.makeText(RegisterActivity.this, "Enter Gender", Toast.LENGTH_LONG).show();
            return;
        }
        if(!pssstr.equals(cpssstr)){
            cpass.setError("Password Not Same!");
            return;
        }

        mAuth.createUserWithEmailAndPassword(emailstr, pssstr).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Profile profile = new Profile(nstr, emailstr, phstr, gender);
                String uid = mAuth.getCurrentUser().getUid();
                myRef.child(uid).setValue(profile);
                mAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        String str = "A verification mail had been sent to you email, please verify first & then login";
                        PopupDialog popupDialog = new PopupDialog(RegisterActivity.this, str);
                        popupDialog.show();
                        popupDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                startActivity(new Intent(RegisterActivity.this, StartPage.class));
                                finish();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String str = "Your account is created, but verification mail send Failed. Please login and apply for resend verification mail.";
                        PopupDialog popupDialog = new PopupDialog(RegisterActivity.this, str);
                        popupDialog.show();
                        popupDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                startActivity(new Intent(RegisterActivity.this, StartPage.class));
                                finish();
                            }
                        });
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String str = "Error! "+ e.getMessage();
                PopupDialog popupDialog = new PopupDialog(RegisterActivity.this, str);
                popupDialog.show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        onBackButtonpressed();
    }

    void onBackButtonpressed()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this)
                .setMessage("All the progress you've made in this page will be lost, sure to proceed?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(RegisterActivity.this, StartPage.class));
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();

        alertDialog.show();
    }
}