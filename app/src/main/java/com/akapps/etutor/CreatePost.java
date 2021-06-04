package com.akapps.etutor;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.Objects;

public class CreatePost extends Fragment {
    EditText ed1, ed2, ed3, ed4;
    MaterialSpinner materialSpinner1, materialSpinner2;
    Button button;
    String item1 = "Pre-Primary", item2 = "less than 2500";
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    Context myContext;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_createpost, container, false);
        ed1 = view.findViewById(R.id.ed1);
        ed2 = view.findViewById(R.id.ed);
        ed3 = view.findViewById(R.id.ed2);
        ed4 = view.findViewById(R.id.ed3);
        materialSpinner1 = view.findViewById(R.id.spinner);
        materialSpinner2 = view.findViewById(R.id.spinner2);
        button = view.findViewById(R.id.button13);

        materialSpinner1.setItems("Pre-Primary", "Primary", "Lower-Secondary", "Secondary", "Higher-Secondary",
                "Admission");

        materialSpinner1.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                item1 = String.valueOf(item);
            }
        });

        materialSpinner2.setItems("Less than 2500", "2500-3000", "3000-4000", "4000-6000", "6000-8000", "8000-10000",
                "10000-15000", "15000-20000", "More than 20000");

        materialSpinner2.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                item2 = String.valueOf(item);
            }
        });

        button.setOnClickListener(v -> {
            String s1 = ed1.getText().toString();
            String s2 = ed2.getText().toString();
            String s3 = ed3.getText().toString();
            String s4 = ed4.getText().toString();

            if(s1.isEmpty()){
                ed1.setError("Title is Required!");
                return;
            }

            if(s2.isEmpty()){
                ed2.setError("Enter Post body!");
                return;
            }

            if(s3.isEmpty()){
                ed3.setError("Subject is Required!");
                return;
            }

            if(s4.isEmpty()){
                ed4.setError("Contact Details is Required!");
                return;
            }
            button.setEnabled(false);
            String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
            Post post = new Post(s1, s2, item1, s3, item2, s4, uid);
            String key = myRef.child("Posts").push().getKey();
            myRef.child("Posts").child(Objects.requireNonNull(key)).setValue(post);
            Toast.makeText(myContext, "Successfully Posted!", Toast.LENGTH_LONG).show();

        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myContext = context;
    }
}
