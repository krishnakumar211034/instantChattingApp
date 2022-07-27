package com.example.instantchattingapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instantchattingapp.R;
import com.example.instantchattingapp.models.ChatMessage;

import java.util.ArrayList;


public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Bitmap recieverProfileImage;
    private final ArrayList<ChatMessage> chatMessages;
    private final Context context;
    private final String SenderId;
    private final int VIEW_TYPE_SENT = 1;
    private final int VIEW_TYPE_RECIEVE = 2;

    public ChatAdapter(Context context, ArrayList<ChatMessage> chatMessages, Bitmap recieverProfileImage, String senderId) {
        this.recieverProfileImage = recieverProfileImage;
        this.chatMessages = chatMessages;
        this.context = context;
        SenderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==VIEW_TYPE_SENT){
            return new sentMessageViewHolder(LayoutInflater.from(context).inflate(R.layout.sent_message_container, parent, false));
        } else {
            return new recievedMessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recieved_message_container, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position)== VIEW_TYPE_SENT){
            ((sentMessageViewHolder)holder).setData(chatMessages.get(position));
        } else {
            ((recievedMessageViewHolder)holder).setData(chatMessages.get(position),recieverProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(chatMessages.get(position).getSenderId().equals(SenderId)){
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECIEVE;
        }
    }

    private class sentMessageViewHolder extends RecyclerView.ViewHolder{
        private TextView message,dateTime;
        public sentMessageViewHolder(View view) {
            super(view);
            message = view.findViewById(R.id.textMessage);
            dateTime = view.findViewById(R.id.textDateTime);
        }
        public void setData(ChatMessage chatMessage){
            message.setText(chatMessage.getMessage());
            dateTime.setText(chatMessage.getDateTime());
        }
    }
    private class recievedMessageViewHolder extends RecyclerView.ViewHolder{
        private TextView message,dateTime;
        private ImageView profile;
        public recievedMessageViewHolder(View view) {
            super(view);
            message = view.findViewById(R.id.textMessage);
            dateTime = view.findViewById(R.id.textDateTime);
            profile = view.findViewById(R.id.userProfile);
        }
        public void setData(ChatMessage chatMessage,Bitmap recieverProfileImage){
             message.setText(chatMessage.getMessage());
            dateTime.setText(chatMessage.getDateTime());
            profile.setImageBitmap(recieverProfileImage);
        }
    }
}