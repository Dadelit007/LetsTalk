package com.example.letstalk.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.letstalk.Adapter.UserAdapter;
import com.example.letstalk.ModelClass.ModelChat;
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
import java.util.HashSet;
import java.util.Set;


public class ChatFragment extends Fragment {

    RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private ArrayList<ModelUser> mUsers;
    private ArrayList<ModelChat> modelChats;
    FirebaseUser firebaseUser;
    public Set<String> wordSet=new HashSet<String>();

    private ProgressDialog pd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view=inflater.inflate(R.layout.fragment_chat,container,false);

        pd=new ProgressDialog(getContext());
        pd.setMessage("Loading...");
        pd.show();

        recyclerView= view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mUsers = new ArrayList<ModelUser>();
        modelChats = new ArrayList<ModelChat>();
        readChat();

//        pd.dismiss();

        return  view;
    }

    private void readChat() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //  mUsers.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    ModelChat modelChat = snapshot.getValue(ModelChat.class);

                    if(modelChat.getReceiver().equals(firebaseUser.getUid())||modelChat.getSender().equals(firebaseUser.getUid())){
                        {
                            if(modelChat.getReceiver().equals(firebaseUser.getUid()))
                            {
                                final String uid=modelChat.getSender();
                                int p=0;
                                for (String s : wordSet) {
                                    if(s.equals(modelChat.getSender()))
                                    {
                                        p=1;
                                    }
//                                    System.out.println(s);
                                }

                                wordSet.add(modelChat.getSender());
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");


                                if(p==0) {
                                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @SuppressLint("RestrictedApi")
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            //  mUsers.clear();
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                ModelUser modelUser = snapshot.getValue(ModelUser.class);

                                                if (modelUser.getUid().equals(uid)) {
                                                    mUsers.add(modelUser);
                                                }
                                            }

                                            userAdapter = new UserAdapter(mUsers, getActivity());
                                            recyclerView.setAdapter(userAdapter);
                                            pd.dismiss();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                            else
                            {
                                final String uid=modelChat.getReceiver();
                                int p=0;
                                for (String s : wordSet) {
                                    if(s.equals(modelChat.getReceiver()))
                                    {
                                        p=1;
                                    }
                                }
                                wordSet.add(modelChat.getReceiver());
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");


                                if(p==0) {
                                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @SuppressLint("RestrictedApi")
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            //  mUsers.clear();
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                ModelUser modelUser = snapshot.getValue(ModelUser.class);

                                                if (modelUser.getUid().equals(uid)) {
                                                    mUsers.add(modelUser);
                                                }
                                            }

                                            userAdapter = new UserAdapter(mUsers, getActivity());
                                            recyclerView.setAdapter(userAdapter);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }
                    }
                    else
                        pd.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                pd.dismiss();
            }
        });
    }
}
