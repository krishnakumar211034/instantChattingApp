package com.example.instantchattingapp.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instantchattingapp.databinding.ItemContainerRecentConversationBinding;
import com.example.instantchattingapp.listeners.ConversionListener;
import com.example.instantchattingapp.models.ChatMessage;
import com.example.instantchattingapp.models.User;

import java.util.ArrayList;


public class RecentConversionAdapter extends RecyclerView.Adapter<RecentConversionAdapter.ConversionViewHolder> {
    private final ArrayList<ChatMessage> chatMessages;
    private ConversionListener conversionListener;

    public RecentConversionAdapter(ArrayList<ChatMessage> chatMessages, ConversionListener conversionListener) {
        this.chatMessages = chatMessages;
        this.conversionListener = conversionListener;
    }

    @NonNull
    @Override
    public RecentConversionAdapter.ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversionViewHolder(
                ItemContainerRecentConversationBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecentConversionAdapter.ConversionViewHolder holder, int position) {
        holder.setData(chatMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ConversionViewHolder extends RecyclerView.ViewHolder{
    private ItemContainerRecentConversationBinding binding;
        public ConversionViewHolder(ItemContainerRecentConversationBinding itemContainerRecentConversationBinding) {
            super(itemContainerRecentConversationBinding.getRoot());
            binding = itemContainerRecentConversationBinding;
        }
        public void setData(ChatMessage chatMessage){
            binding.textName.setText(chatMessage.getConversionName());
            binding.textRecentMessage.setText(chatMessage.getMessage());
            // binding.userProfile.setImageBitmap(getConversionImage(chatMessage.getConversionImage()));
            binding.getRoot().setOnClickListener(view ->{
                User user = new User();
                user.setId(chatMessage.getConversionId());
                user.setImage(chatMessage.getConversionImage());
                user.setName(chatMessage.getConversionName());
                conversionListener.onConversionClicked(user);
            });
        }
    }
    private Bitmap getConversionImage(String encodedImage){
        byte bytes[] = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0, bytes.length);
    }
}
