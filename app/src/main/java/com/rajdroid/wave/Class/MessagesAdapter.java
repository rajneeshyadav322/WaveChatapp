package com.rajdroid.wave.Class;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.rajdroid.wave.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter {


    Context context;
    ArrayList<Messages> messagesArrayList;

    int ITEM_SEND =1;
    int ITEM_RECEIVE = 2;



    public MessagesAdapter(Context context, ArrayList<Messages> messagesArrayList) {
        this.context = context;
        this.messagesArrayList = messagesArrayList;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_SEND) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_chat_layout, parent, false);
            return new SenderViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.reciever_chat_layout, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {

        Messages messages = messagesArrayList.get(position);
        if(holder.getClass() == SenderViewHolder.class) {
            SenderViewHolder senderViewHolder = (SenderViewHolder)holder;
            senderViewHolder.textViewMessage.setText(messages.getMessage());
            senderViewHolder.timeOfMessage.setText(messages.getCurrentTime());
        }
        else {
            ReceiverViewHolder receiverViewHolder = (ReceiverViewHolder)holder;
            receiverViewHolder.textViewMessage.setText(messages.getMessage());
            receiverViewHolder.timeOfMessage.setText(messages.getCurrentTime());
        }
    }


    @Override
    public int getItemViewType(int position) {

        Messages messages = messagesArrayList.get(position);
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderId())) {
            return ITEM_SEND;
        }
        else {
            return ITEM_RECEIVE;
        }
    }


    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }



    class SenderViewHolder extends RecyclerView.ViewHolder {

        TextView textViewMessage;
        TextView timeOfMessage;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.senderMessage);
            timeOfMessage = itemView.findViewById(R.id.messageTime);
        }
    }

    class ReceiverViewHolder extends RecyclerView.ViewHolder {

        TextView textViewMessage;
        TextView timeOfMessage;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.receiverMessage);
            timeOfMessage = itemView.findViewById(R.id.receiverMessageTime);
        }
    }
}
