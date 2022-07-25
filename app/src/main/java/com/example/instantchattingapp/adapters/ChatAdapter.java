package com.example.instantchattingapp.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instantchattingapp.R;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter .ChatHolder>{
    @NonNull
    @Override
    public ChatAdapter.ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ChatHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ChatHolder extends RecyclerView.ViewHolder{
        TextView textMessage,textDateTime;
        ImageView userProfile;
        public ChatHolder(@NonNull View itemView) {
            super(itemView);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            textMessage = itemView.findViewById(R.id.textMessage);
            userProfile = itemView.findViewById(R.id.image_profile);
        }
    }
    void setData(){

    }
}
