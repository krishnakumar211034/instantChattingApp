package com.example.instantchattingapp.activities;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import com.example.instantchattingapp.adapters.ChatAdapter;
import com.example.instantchattingapp.databinding.ActivityChatBinding;
import com.example.instantchattingapp.models.ChatMessage;
import com.example.instantchattingapp.models.User;
import com.example.instantchattingapp.utilities.Constants;
import com.example.instantchattingapp.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class chatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private User reciever;
    private ArrayList<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();
        loadingUserDetails();
        init();
        listenMessage();
    }
    private void setListener(){
        binding.imageBack.setOnClickListener(view -> onBackPressed());
        binding.sendButton.setOnClickListener(view -> sendMessage());
    }
    private void sendMessage()  {
        HashMap<String,Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECIEVER_ID,reciever.getId());
        message.put(Constants.KEY_MESSAGE,binding.inputMessage.getText().toString());
        binding.inputMessage.setText(null);
        message.put(Constants.KEY_TIMESTAMP,new Date());
        firebaseFirestore.collection(Constants.KEY_COLLECTION_CHAT).add(message);
    }
    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    public void init(){
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<ChatMessage>();
        chatAdapter = new ChatAdapter(getApplicationContext(),chatMessages,
                getBitmapFromencodedString(reciever.getImage()),
                preferenceManager.getString(Constants.KEY_USER_ID));
        binding.chatRecyclerView.setAdapter(chatAdapter);
        firebaseFirestore = FirebaseFirestore.getInstance();
    }
    private Bitmap getBitmapFromencodedString(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0, bytes.length);
    }
    private void loadingUserDetails(){
        reciever = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.userName.setText(reciever.getName());
    }

    //didn't understand this portion of the code
        private final EventListener<QuerySnapshot> eventListener = (value, error) ->{
        if(error!=null) return;
        if(value!=null){
            int count=chatMessages.size();
            for(DocumentChange  documentChange : value.getDocumentChanges()){
                if(documentChange.getType() == DocumentChange.Type.ADDED){
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setSenderId(documentChange.getDocument().getString(Constants.KEY_SENDER_ID));
                    chatMessage.setRecieverId(documentChange.getDocument().getString(Constants.KEY_RECIEVER_ID));
                    chatMessage.setMessage(documentChange.getDocument().getString(Constants.KEY_MESSAGE));
                    chatMessage.setDateTime(getReadableDateTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP)));
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    chatMessages.add(chatMessage);
                }
            }
            Collections.sort(chatMessages, (obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            if(count==0) {
                chatAdapter.notifyDataSetChanged();
            } else {
                chatAdapter.notifyItemRangeInserted(chatMessages.size(),chatMessages.size());
                binding.chatRecyclerView.scrollToPosition(chatMessages.size()-1);
            }
//            binding.chatRecyclerView.setVisibility(View.VISIBLE);
        }
        binding.progressBar.setVisibility(View.GONE);
    };
    private void listenMessage(){
        firebaseFirestore.collection(Constants.KEY_COLLECTION_CHAT).
                whereEqualTo(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_SENDER_ID)).
                whereEqualTo(Constants.KEY_RECIEVER_ID,reciever.getId()).
                addSnapshotListener(eventListener);
        firebaseFirestore.collection(Constants.KEY_COLLECTION_CHAT).
                whereEqualTo(Constants.KEY_SENDER_ID,reciever.getId()).
                whereEqualTo(Constants.KEY_RECIEVER_ID,preferenceManager.getString(Constants.KEY_SENDER_ID)).
                addSnapshotListener(eventListener);
    }
}