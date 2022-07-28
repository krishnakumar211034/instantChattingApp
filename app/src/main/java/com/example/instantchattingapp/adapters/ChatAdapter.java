package com.example.instantchattingapp.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instantchattingapp.databinding.RecievedMessageContainerBinding;
import com.example.instantchattingapp.databinding.SentMessageContainerBinding;
import com.example.instantchattingapp.models.ChatMessage;

import java.util.ArrayList;


public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final Bitmap receiverProfileImage;
    private final ArrayList<ChatMessage> chatMessages;
    private final String SenderId;
    //        private final Context context;
    private final int VIEW_TYPE_SENT = 1;
    private final int VIEW_TYPE_RECIEVE = 2;

    public ChatAdapter(Bitmap receiverProfileImage, ArrayList<ChatMessage> chatMessages, String senderId) {
        this.receiverProfileImage = receiverProfileImage;
        this.chatMessages = chatMessages;
        SenderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==VIEW_TYPE_SENT){
            return new SentMessageViewHolder(
                    SentMessageContainerBinding.inflate(
                            LayoutInflater.from(parent.getContext()),parent,false));
        } else {
            return new ReceivedMessageViewHolder(
                    RecievedMessageContainerBinding.inflate(
                            LayoutInflater.from(parent.getContext()),parent,false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position)== VIEW_TYPE_SENT){
                ((SentMessageViewHolder)holder).setData(chatMessages.get(position));
            } else {
                ((ReceivedMessageViewHolder)holder).setData(chatMessages.get(position),receiverProfileImage);
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

    // recycler view handling sent messages
    static class SentMessageViewHolder extends RecyclerView.ViewHolder{
        private SentMessageContainerBinding binding;
        public SentMessageViewHolder(SentMessageContainerBinding sent) {
            super(sent.getRoot());
            binding = sent;
        }
        public void setData(ChatMessage chatMessage){
            binding.textMessage.setText(chatMessage.getMessage());
            binding.textDateTime.setText(chatMessage.getDateTime());
        }
    }
    // recycler view handling received messages with Image view
    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder{
        private RecievedMessageContainerBinding binding;
        public ReceivedMessageViewHolder(RecievedMessageContainerBinding sent) {
            super(sent.getRoot());
            binding = sent;
        }
        public void setData(ChatMessage chatMessage, Bitmap receiverProfileImage){
            binding.textMessage.setText(chatMessage.getMessage());
            binding.textDateTime.setText(chatMessage.getDateTime());
            binding.userProfile.setImageBitmap(receiverProfileImage);
        }
    }
}



//
//        public ChatAdapter(Context context, ArrayList<ChatMessage> chatMessages, Bitmap recieverProfileImage, String senderId) {
//            this.receiverProfileImage = recieverProfileImage;
//            this.chatMessages = chatMessages;
//            this.context = context;
//            SenderId = senderId;
//        }
//
//        @NonNull
//        @Override
//        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//
//        }
//
//        @Override
//        public int getItemCount() {
//            return chatMessages.size();
//        }
//
//        @Override
//        public int getItemViewType(int position) {

//        }
//
//        private class sentMessageViewHolder extends RecyclerView.ViewHolder{
//            private TextView message,dateTime;
//            public sentMessageViewHolder(View view) {
//                super(view);
//                message = view.findViewById(R.id.textMessage);
//                dateTime = view.findViewById(R.id.textDateTime);
//            }

//        }
//        private class recievedMessageViewHolder extends RecyclerView.ViewHolder{
//            private TextView message,dateTime;
//            private ImageView profile;
//            public recievedMessageViewHolder(View view) {
//                super(view);
//                message = view.findViewById(R.id.textMessage);
//                dateTime = view.findViewById(R.id.textDateTime);
//                profile = view.findViewById(R.id.userProfile);
//            }
//            public void setData(ChatMessage chatMessage,Bitmap recieverProfileImage){
//                 message.setText(chatMessage.getMessage());
//                dateTime.setText(chatMessage.getDateTime());
//                profile.setImageBitmap(recieverProfileImage);
//            }
//        }
//}