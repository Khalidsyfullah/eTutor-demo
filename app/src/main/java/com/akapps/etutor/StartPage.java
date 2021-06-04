package com.akapps.etutor;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class StartPage extends AppCompatActivity {
    EditText email, password;
    Button button;
    TextView textView, back_btn, forgot_pass;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        email = findViewById(R.id.email1);
        password = findViewById(R.id.pass1);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.textView6);
        back_btn = findViewById(R.id.textView3);
        forgot_pass = findViewById(R.id.textView8);
        mAuth = FirebaseAuth.getInstance();

        //Previously Logined user found...
        //Popup dialog will be shown for relogin

        if(mAuth.getCurrentUser() != null && mAuth.getCurrentUser().isEmailVerified()){
            PopupAutologin popupAutologin = new PopupAutologin(StartPage.this);
            popupAutologin.show();
        }

        textView.setOnClickListener(v -> {
            startActivity(new Intent(StartPage.this, RegisterActivity.class));
            finish();
        });


        back_btn.setOnClickListener(v -> {
            //Back pressed
            backHandler();
        });

        button.setOnClickListener(v -> {
            String emailStr = email.getText().toString();
            String passwordStr = password.getText().toString();

            if(emailStr.isEmpty()){
                email.setError("Required!");
                return;
            }
            if(passwordStr.isEmpty()){
                password.setError("Required!");
                return;
            }

            mAuth.signInWithEmailAndPassword(emailStr, passwordStr).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                    //If Email Not verified, then verify first
                    if(!mAuth.getCurrentUser().isEmailVerified()){
                        PopupEmailVerification popupEmailVerification = new PopupEmailVerification(StartPage.this);
                        popupEmailVerification.show();
                    }
                    else{
                        //User found & ready for next page
                        Toast.makeText(StartPage.this, "Sign in successful", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(StartPage.this, CenterPage.class));
                        finish();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    PopupDialog popupDialog = new PopupDialog(StartPage.this, "Error! "+e.getMessage());
                    popupDialog.show();
                }
            });
        });



        //forgot password clicked
        forgot_pass.setOnClickListener(v -> {
            PopupPasswordresetMail popupPasswordresetMail = new PopupPasswordresetMail(StartPage.this);
            popupPasswordresetMail.show();
        });



    }

    @Override
    public void onBackPressed() {
        backHandler();
    }


    //Manage Back Press

    void backHandler()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(StartPage.this)
                .setTitle("Confirm Exit")
                .setMessage("Action will exit the app, sure to continue?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StartPage.this.finish();
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


    //Popup dialog for password reset email here
    class PopupPasswordresetMail extends Dialog{
        Button send;
        EditText ed;
        TextView cancel;
        public PopupPasswordresetMail(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.popup_passwordresetmail);
            send = findViewById(R.id.button3);
            ed = findViewById(R.id.email);
            cancel = findViewById(R.id.textView10);
            cancel.setOnClickListener(v -> {
                //dismiss dialog
                this.dismiss();
            });

            this.setCancelable(false);

            send.setOnClickListener(v -> {
                String mail = ed.getText().toString();
                if(mail.isEmpty()){
                    ed.setError("Required!");
                    return;
                }
                this.dismiss();
                //send password reset email to that email
                mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //successfully Send Email
                        String str = "Password Reset email sent successfully, please check your inbox.";
                        PopupDialog popupDialog = new PopupDialog(StartPage.this, str);
                        popupDialog.show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //error found
                        String str = "Error! "+ e.getMessage();
                        PopupDialog popupDialog = new PopupDialog(StartPage.this, str);
                        popupDialog.show();
                    }
                });

            });
        }
    }

    //Popup dialog for resend verification mail
    class PopupEmailVerification extends Dialog{

        public PopupEmailVerification(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.popup_resendverification);
            Button bt = findViewById(R.id.button5);
            TextView txt = findViewById(R.id.textView13);
            this.setCancelable(false);
            txt.setOnClickListener(v -> {
                //dismiss popup
                this.dismiss();
            });

            //again send confirmation email
            bt.setOnClickListener(v -> {
                this.dismiss();
                if(mAuth.getCurrentUser() == null) return;
                mAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        String str = "Email sent successfully, please check your inbox.";
                        PopupDialog popupDialog = new PopupDialog(StartPage.this, str);
                        popupDialog.show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String str = "Error! "+ e.getMessage();
                        PopupDialog popupDialog = new PopupDialog(StartPage.this, str);
                        popupDialog.show();
                    }
                });
            });

        }
    }

    //Auto login popup
    class PopupAutologin extends Dialog{

        public PopupAutologin(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.popup_autologin);
            TextView tx1, tx2;
            Button bt;
            tx1 = findViewById(R.id.textView42);
            tx2 = findViewById(R.id.textr);
            bt = findViewById(R.id.button16);
            this.setCancelable(false);
            tx1.setOnClickListener(v -> {
                //dismiss
                this.dismiss();
            });
            if(mAuth.getCurrentUser()!= null) tx2.setText(mAuth.getCurrentUser().getEmail());
            bt.setOnClickListener(v -> {
                //auto login
                this.dismiss();
                Toast.makeText(StartPage.this, "Sign in successful", Toast.LENGTH_LONG).show();
                startActivity(new Intent(StartPage.this, CenterPage.class));
                StartPage.this.finish();
            });
        }
    }
}