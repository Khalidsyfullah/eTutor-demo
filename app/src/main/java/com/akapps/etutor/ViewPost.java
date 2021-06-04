package com.akapps.etutor;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewPost extends Fragment {
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
    GridView gridView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_viewpost, container, false);
        gridView = view.findViewById(R.id.grid_lay);

        //Retrieve all posts
        myRef.child("Posts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Post> list = new ArrayList<>();
                if(!snapshot.exists()){
                    gridView.setAdapter(new CustomAdaper(list, true));
                }
                else{
                    for(DataSnapshot ds : snapshot.getChildren()){
                        Post post = ds.getValue(Post.class);
                        list.add(post);
                    }
                    gridView.setAdapter(new CustomAdaper(list, false));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    //Base adapter class

    class CustomAdaper extends BaseAdapter{
        ArrayList<Post> list;
        boolean flag;

        public CustomAdaper(ArrayList<Post> list, boolean flag) {
            this.list = list;
            this.flag = flag;
        }

        @Override
        public int getCount() {
            if(flag){
                return 1;
            }
            else return list.size();
        }

        @Override
        public Object getItem(int position) {
            if(flag){
                return null;
            }
            else return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(flag){
                @SuppressLint({"ViewHolder", "InflateParams"}) View view = getLayoutInflater().inflate(R.layout.grid_null, null);
                return view;
            }
            @SuppressLint({"ViewHolder", "InflateParams"}) View view = getLayoutInflater().inflate(R.layout.grid_posts, null);
            TextView tx1, tx2, tx3;
            Button bt;
            tx1 = view.findViewById(R.id.textView27);
            tx2 = view.findViewById(R.id.textView28);
            tx3 = view.findViewById(R.id.textView30);
            bt = view.findViewById(R.id.button14);
            Post curpost = list.get(position);
            String title = curpost.getTitle();
            String details = curpost.getPost();

            //html tags
            String add_info = "<b>Student Category: </b><br>"+ curpost.getCategory()+
                    "<br><b>Required Subjects: </b><br>" + curpost.getSubjects()+
                    "<br><b>Salary Range: </b><br>" + curpost.getSalary()+
                    "<br><b>Contact Details: </b><br>" + curpost.getContact();

            tx1.setText(title);
            tx2.setText(details);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                tx3.setText(Html.fromHtml(add_info, Html.FROM_HTML_MODE_COMPACT));
            }else {
                tx3.setText(Html.fromHtml(add_info));
            }

            bt.setOnClickListener(v -> {
                ((CenterPage) requireActivity()).showDetails(title, details, add_info, curpost.getUid());
            });

            return view;
        }
    }
}
