package com.example.letstalk.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letstalk.ChatActivity;
import com.example.letstalk.ModelClass.ModelUser;
import com.example.letstalk.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private ArrayList<ModelUser> mUsers ;
    private Context mcontext;

    public UserAdapter(ArrayList<ModelUser> mUsers, Context mcontext) {
        this.mUsers = mUsers;
        this.mcontext = mcontext;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(mcontext).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);

    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

//        User user = mUsers.get(position);
//        holder.username.setText(user.getName());
//            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        final String hisUID=mUsers.get(position).getUid();
        String userImage=mUsers.get(position).getImage();
        String userName=mUsers.get(position).getName();
        String userEmail=mUsers.get(position).getEmail();

        holder.username.setText(userName);
        try {
            Picasso.get().load(userImage)
                    .placeholder(R.drawable.letstalk)
                    .into(holder.profile_image);
        }
        catch (Exception e){

        }
          holder.itemView.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  Intent intent=new Intent(mcontext, ChatActivity.class);
                  intent.putExtra("hisUid",hisUID);
                  mcontext.startActivity(intent);
              }
          });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView profile_image;
        public TextView username;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username =itemView.findViewById(R.id.username);
            profile_image =itemView.findViewById(R.id.profile_image);
        }
    }

}
