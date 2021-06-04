package com.akapps.etutor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfile extends Fragment {
    EditText email, name, phnnum;
    Button save_btn, edit_profile, password_change, logout, update_profile_pic;
    RadioGroup radioGroup;
    TextView add_image;
    CircleImageView circleImageView;
    RadioButton rb1, rb2;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    StorageReference stRef = FirebaseStorage.getInstance().getReference();
    String uid;
    Context myContext;
    String gender = "";
    String emailStr = "";

    //will be used to pick up image from gallery
    Bitmap bitmap;
    @SuppressLint("IntentReset")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_profile, container, false);
        name = view.findViewById(R.id.editTextTextPersonName2);
        email = view.findViewById(R.id.editTextTextEmailAddress2);
        phnnum = view.findViewById(R.id.editTextPhone2);
        radioGroup = view.findViewById(R.id.radio);
        save_btn = view.findViewById(R.id.button9);
        logout = view.findViewById(R.id.button6);
        edit_profile = view.findViewById(R.id.button7);
        rb1 = view.findViewById(R.id.radioButton);
        rb2 = view.findViewById(R.id.radioButton2);
        update_profile_pic = view.findViewById(R.id.button15);
        add_image =  view.findViewById(R.id.textView40);
        password_change = view.findViewById(R.id.button8);
        circleImageView = view.findViewById(R.id.profile_image);
        radioGroup.setEnabled(false);
        name.setEnabled(false);
        email.setEnabled(false);
        phnnum.setEnabled(false);
        rb1.setEnabled(false);
        rb2.setEnabled(false);
        update_profile_pic.setVisibility(View.GONE);
        add_image.setVisibility(View.GONE);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radioButton){
                    gender = "Male";
                }else{
                    gender = "Female";
                }
            }
        });

        if(mAuth.getCurrentUser()!= null) uid = mAuth.getCurrentUser().getUid();

        //Retrive Data & set to profile
        myRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Profile profile = snapshot.getValue(Profile.class);
                if(profile == null) return;
                name.setText(profile.getName());
                emailStr = profile.getEmail();
                email.setText(emailStr);
                phnnum.setText(profile.getPhonenumber());
                gender = profile.getGender();
                if(gender.equals("Male")){
                    radioGroup.check(R.id.radioButton);
                }else{
                    radioGroup.check(R.id.radioButton2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Retrieve profile picture, maximum supported size = 10 MB
        final long TEN_MEGABYTE = 10 * 1024 * 1024;
        stRef.child(mAuth.getCurrentUser().getUid()).getBytes(TEN_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                //image found, set to imageview
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                circleImageView.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Image Not found
            }
        });

        edit_profile.setOnClickListener(v -> {
            edit_profile.setVisibility(View.GONE);
            password_change.setVisibility(View.GONE);
            logout.setVisibility(View.GONE);
            name.setEnabled(true);
            phnnum.setEnabled(true);
            //email.setEnabled(true);
            radioGroup.setEnabled(true);
            rb1.setEnabled(true);
            rb2.setEnabled(true);
            save_btn.setVisibility(View.VISIBLE);
            update_profile_pic.setVisibility(View.VISIBLE);
            add_image.setVisibility(View.VISIBLE);
        });

        save_btn.setOnClickListener(v -> {
            String nameStr = name.getText().toString();
            String phone_num = phnnum.getText().toString();
            //String mailr = email.getText().toString();
            if(nameStr.isEmpty()){
                name.setError("Required!");
                return;
            }
            if(phone_num.isEmpty()){
                phnnum.setError("Required!");
                return;
            }
            if(gender.isEmpty()){
                Toast.makeText(myContext, "Select Gender!", Toast.LENGTH_LONG).show();
                return;
            }

            //Update email address, if you want, uncomment all
            /*if(!emailStr.equals(mailr)){
                mAuth.getCurrentUser().updateEmail(mailr).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(myContext, "Email address updated!", Toast.LENGTH_LONG).show();
                        emailStr = mailr;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(myContext, "Failed! "+e.getMessage(), Toast.LENGTH_LONG).show();
                        email.setText(emailStr);
                    }
                });
            }*/

            Profile profile = new Profile(nameStr, emailStr, phone_num, gender);
            myRef.child(uid).setValue(profile);
            Toast.makeText(myContext, "Profile updated!", Toast.LENGTH_LONG).show();
            name.setEnabled(false);
            phnnum.setEnabled(false);
            //email.setEnabled(false);
            radioGroup.setEnabled(false);
            rb1.setEnabled(false);
            rb2.setEnabled(false);
            edit_profile.setVisibility(View.VISIBLE);
            password_change.setVisibility(View.VISIBLE);
            logout.setVisibility(View.VISIBLE);
            save_btn.setVisibility(View.GONE);
            update_profile_pic.setVisibility(View.GONE);
            add_image.setVisibility(View.GONE);
        });

        logout.setOnClickListener(v -> {
            PopupLogout popupLogout = new PopupLogout(myContext);
            popupLogout.show();
        });

        password_change.setOnClickListener(v -> {
            PasswordChange passwordChange = new PasswordChange(myContext);
            passwordChange.show();
        });

        //Add Image, let user choose one from gallery

        ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK && result.getData()!= null){
                            Uri selectedImageUri = result.getData().getData();
                            circleImageView.setImageURI(selectedImageUri);
                            Drawable drawable = circleImageView.getDrawable();
                            bitmap = ((BitmapDrawable) drawable).getBitmap();
                        }
                        else{
                            Toast.makeText(myContext, "You haven't Selected any image!", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );

        add_image.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryIntent.setType("image/*");
            resultLauncher.launch(galleryIntent);
        });

        //Upload Image to firebase storage
        update_profile_pic.setOnClickListener(v -> {
            if(bitmap == null){
                Toast.makeText(myContext, "No Image Found!", Toast.LENGTH_LONG).show();
                return;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            //Maximum size 10 mb
            if(data.length > TEN_MEGABYTE){
                Toast.makeText(myContext, "Size is too large!", Toast.LENGTH_LONG).show();
                return;
            }


            //Process will take time, so a loading screen
            PopupLoading popupLoading = new PopupLoading(myContext);
            popupLoading.show();
            stRef.child(mAuth.getCurrentUser().getUid()).putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    popupLoading.dismiss();
                    Toast.makeText(myContext, "Successfully Uploaded!", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    popupLoading.dismiss();
                    PopupDialog popupDialog = new PopupDialog(myContext, "Error! "+e.getMessage());
                    popupDialog.show();
                }
            });
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myContext = context;
    }

    //Popup Logout
    class PopupLogout extends Dialog{

        public PopupLogout(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.popup_logout);
            Button bt1, bt2;
            bt1 = findViewById(R.id.button10);
            bt2 = findViewById(R.id.button11);
            this.setCancelable(false);
            bt1.setOnClickListener(v -> {
                mAuth.signOut();
                this.dismiss();
                ((CenterPage) requireActivity()).logout();
            });

            bt2.setOnClickListener(v -> this.dismiss());

        }
    }

    //For password Change

    class PasswordChange extends Dialog{

        public PasswordChange(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.popup_passwordchange);
            EditText ed1, ed2, ed3;
            TextView txt;
            Button bt;
            ed1 = findViewById(R.id.pass1);
            ed2 = findViewById(R.id.pass3);
            ed3 = findViewById(R.id.pass4);
            txt = findViewById(R.id.textView20);
            bt = findViewById(R.id.button12);
            this.setCancelable(false);
            txt.setOnClickListener(v -> this.dismiss());
            bt.setOnClickListener(v -> {
                String pass1 = ed1.getText().toString();
                if(pass1.isEmpty()){
                    ed1.setError("Required!");
                    return;
                }
                String pass2 = ed2.getText().toString();
                String pass3 = ed3.getText().toString();
                if(pass2.isEmpty()){
                    ed2.setError("Required!");
                    return;
                }
                if(pass3.isEmpty()){
                    ed3.setError("Required!");
                    return;
                }

                if(!pass2.equals(pass3)){
                    ed3.setError("Password not same!");
                    return;
                }

                if(pass2.length() <6){
                    ed2.setError("Minimum 6 character Required!");
                    return;
                }
                if(mAuth.getCurrentUser() == null) return;

                //check if password is correct or not
                mAuth.signInWithEmailAndPassword(emailStr, pass1).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //Password matched
                        //update password
                        mAuth.getCurrentUser().updatePassword(pass2).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                myRef.child(uid).child("password").setValue(pass2);
                                Toast.makeText(myContext, "Password updated successfully!", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(myContext, "Error! "+e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //password not matched
                        ed1.setError("Password not matched!");
                        Toast.makeText(myContext, "Wrong Password!", Toast.LENGTH_LONG).show();
                    }
                });



            });
        }
    }

    //popup loading screen
    class PopupLoading extends Dialog{

        public PopupLoading(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.popup_loading);
            this.setCancelable(false);
        }
    }


}
