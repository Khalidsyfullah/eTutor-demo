package com.akapps.etutor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailsPost extends Fragment {
    TextView title, post, add_info, name, email, phn_num;
    CircleImageView circleImageView;
    StorageReference stRef = FirebaseStorage.getInstance().getReference();
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    String s1, s2, s3, s4;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.details_post, container, false);
        title = view.findViewById(R.id.textView31);
        post = view.findViewById(R.id.textView32);
        add_info = view.findViewById(R.id.textView34);
        name = view.findViewById(R.id.textView36);
        email = view.findViewById(R.id.textView37);
        phn_num = view.findViewById(R.id.textView38);
        circleImageView = view.findViewById(R.id.profile_image3);

        s1 = ((CenterPage) requireActivity()).title;
        s2 = ((CenterPage) requireActivity()).post;
        s3 = ((CenterPage) requireActivity()).details;
        s4 = ((CenterPage) requireActivity()).user_uid;

        title.setText(s1);
        post.setText(s2);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            add_info.setText(Html.fromHtml(s3, Html.FROM_HTML_MODE_COMPACT));
        }else {
            add_info.setText(Html.fromHtml(s3));
        }

        //Find user info

        myRef.child(s4).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Profile profile = snapshot.getValue(Profile.class);
                if(profile == null) return;
                name.setText(profile.getName());
                email.setText(profile.getEmail());
                phn_num.setText(profile.getPhonenumber());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //Find profile picture from storage
        final long TEN_MEGABYTE = 10 * 1024 * 1024;
        stRef.child(s4).getBytes(TEN_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                circleImageView.setImageBitmap(bmp);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Image load failed......
            }
        });

        return view;
    }
}
