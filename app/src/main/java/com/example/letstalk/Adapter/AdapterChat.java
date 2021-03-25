package com.example.letstalk.Adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letstalk.ModelClass.ModelChat;
import com.example.letstalk.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.ViewHolder>{

    private  static final int MSG_TYPE_LEFT=0;
    private  static final int MSG_TYPE_RIGHT=1;

    FirebaseUser fUser;

    Context context;
    ArrayList<ModelChat> chatList;
   // String imageUrl;


    public AdapterChat(Context context, ArrayList<ModelChat> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_RIGHT)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.row_chat_right,parent,false);
            return new ViewHolder(view);
        }
        else
        {
            View view= LayoutInflater.from(context).inflate(R.layout.row_chat_left,parent,false);
            return new ViewHolder(view);
        }
     //   return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String message=chatList.get(position).getMessage();
        String timeStamp=chatList.get(position).getTimestamp();

        Calendar cal=Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime= DateFormat.format("dd/MM/yyyy hh:mm:aa",cal).toString();

        holder.messageTv.setText(message);
        holder.timeTv.setText(dateTime);

        if(position==chatList.size()-1)
        {
            if(chatList.get(position).isSeen())
            {
                holder.seenTv.setText("seen");
            }
            else
            {
                holder.seenTv.setText("Delivered");
            }
        }
        else
        {
            holder.seenTv.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        fUser= FirebaseAuth.getInstance().getCurrentUser();
        if(chatList.get(position).getSender().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else
        {
            return MSG_TYPE_LEFT;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView profileIv;
        TextView messageTv,timeTv,seenTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileIv=itemView.findViewById(R.id.profileIv);
           messageTv =itemView.findViewById(R.id.messageTV);
            timeTv=itemView.findViewById(R.id.timeTv);
            seenTv=itemView.findViewById(R.id.seenTv);
        }
    }
}
