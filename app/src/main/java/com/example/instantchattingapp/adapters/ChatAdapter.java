package com.example.instantchattingapp.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instantchattingapp.databinding.RecievedMessageContainerBinding;
import com.example.instantchattingapp.databinding.SentMessageContainerBinding;
import com.example.instantchattingapp.models.ChatMessage;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Bitmap recieverProfileImage;
    private final ArrayList<ChatMessage> chatMessages;
    private final String SenderId;
    private final int VIEW_TYPE_SENT = 1;
    private final int VIEW_TYPE_RECIEVE = 2;

    public ChatAdapter(Bitmap recieverProfileImage, ArrayList<ChatMessage> chatMessages, String senderId) {
        this.recieverProfileImage = recieverProfileImage;
        this.chatMessages = chatMessages;
        SenderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==VIEW_TYPE_SENT){
            return new sentMessageViewHolder(SentMessageContainerBinding.inflate(
                    LayoutInflater.from(parent.getContext()),
                    parent,
                    false));
        } else {
            return new recievedMessageViewHolder(RecievedMessageContainerBinding.inflate(
                    LayoutInflater.from(parent.getContext()),
                    parent,
                    false));
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

    public static class sentMessageViewHolder extends RecyclerView.ViewHolder{
        private SentMessageContainerBinding binding;
        public sentMessageViewHolder(SentMessageContainerBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
        private void setData(ChatMessage chatMessage){
            binding.textMessage.setText(chatMessage.getMessage());
            binding.textDateTime.setText(chatMessage.getDateTime());
        }
    }
    public static class recievedMessageViewHolder extends RecyclerView.ViewHolder{
        private RecievedMessageContainerBinding binding;
        public recievedMessageViewHolder(RecievedMessageContainerBinding binding){
            super(binding.getRoot());
            this.binding=binding;
        }
        private void setData(ChatMessage chatMessage,Bitmap recieverProfileImage){
            binding.textMessage.setText(chatMessage.getMessage());
            binding.textDateTime.setText(chatMessage.getDateTime());
            binding.userProfile.setImageBitmap(recieverProfileImage);
        }
    }
}
