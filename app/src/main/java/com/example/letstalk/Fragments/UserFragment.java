package com.example.letstalk.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.letstalk.Adapter.UserAdapter;
import com.example.letstalk.ModelClass.ModelUser;
import com.example.letstalk.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserFragment extends Fragment {

     RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private ArrayList<ModelUser> mUsers;
    FirebaseUser firebaseUser;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=  inflater.inflate(R.layout.fragment_user,container,false);


         recyclerView= view.findViewById(R.id.recycler_view);
         recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


//
//
        mUsers = new ArrayList<ModelUser>();
        readUsers();


        return view;
    }
//
    public void readUsers(){

              firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
           DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              //  mUsers.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    ModelUser modelUser = snapshot.getValue(ModelUser.class);

                    if(!modelUser.getUid().equals(firebaseUser.getUid())){
                        mUsers.add(modelUser);
                    }
                }

                userAdapter = new UserAdapter(mUsers,getActivity());
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


   }

}