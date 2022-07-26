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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class chatActivity extends BaseActivity {
    private ActivityChatBinding binding;
    private User receiver;
    private ArrayList<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore firebaseFirestore;
    private String conversionId = null;

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
        message.put(Constants.KEY_RECEIVER_ID, receiver.getId());
        message.put(Constants.KEY_MESSAGE,binding.inputMessage.getText().toString());
        message.put(Constants.KEY_TIMESTAMP,new Date());
        firebaseFirestore.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        if(conversionId != null) {
            updateConversion(binding.inputMessage.getText().toString());
        }
        else {
            HashMap<String, Object> conversion = new HashMap<>();
            conversion.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
            conversion.put(Constants.KEY_SENDER_NAME, preferenceManager.getString(Constants.KEY_NAME));
            conversion.put(Constants.KEY_SENDER_IMAGE, preferenceManager.getString(Constants.KEY_IMAGE));
            conversion.put(Constants.KEY_RECEIVER_ID, receiver.getId());
            conversion.put(Constants.KEY_RECEIVER_NAME, receiver.getName());
            conversion.put(Constants.KEY_RECEIVER_IMAGE, receiver.getImage());
            conversion.put(Constants.KEY_LAST_MESSAGE, binding.inputMessage.getText().toString());
            conversion.put(Constants.KEY_TIMESTAMP, new Date());
            addConversation(conversion);
        }
        binding.inputMessage.setText(null);
    }
    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }
    private void init(){
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<ChatMessage>();
        chatAdapter = new ChatAdapter(getBitmapFromencodedString(receiver.getImage()),
                chatMessages, preferenceManager.getString(Constants.KEY_USER_ID));
        binding.chatRecyclerView.setAdapter(chatAdapter);
        firebaseFirestore = FirebaseFirestore.getInstance();
    }
    private Bitmap getBitmapFromencodedString(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0, bytes.length);
    }
    private void loadingUserDetails(){
        receiver = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.userName.setText(receiver.getName());
    }
    //didn't understand this portion of the code
    private final OnCompleteListener<QuerySnapshot> completeListener = task -> {
        if(task.isSuccessful() && task.getResult()!=null && task.getResult().getDocuments().size()>0){
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversionId = documentSnapshot.getId();
        }
    };
    private final EventListener<QuerySnapshot> eventListener = (value, error) ->{
        if(error!=null) return;
        if(value!=null){
            int count=chatMessages.size();
            for(DocumentChange  documentChange : value.getDocumentChanges()){
                if(documentChange.getType() == DocumentChange.Type.ADDED){
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setSenderId(documentChange.getDocument().getString(Constants.KEY_SENDER_ID));
                    chatMessage.setReceiverId(documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID));
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
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size()-1);
            }
            binding.chatRecyclerView.setVisibility(View.VISIBLE);
        }
        binding.progressBar.setVisibility(View.GONE);
        if(conversionId == null) checkForConversion();
    };
    private void addConversation(HashMap<String, Object> conversion){
        firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS)
                .add(conversion)
                .addOnSuccessListener(documentReference -> conversionId = documentReference.getId());
    }
    private void updateConversion(String message){
        DocumentReference documentReference =
                firebaseFirestore.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(conversionId);
        documentReference.update(
                Constants.KEY_LAST_MESSAGE, message,
                Constants.KEY_TIMESTAMP, new Date());
    }
    // loads the messages sent and received for a specific user
    private void listenMessage(){
        firebaseFirestore.collection(Constants.KEY_COLLECTION_CHAT).
                whereEqualTo(Constants.KEY_SENDER_ID,preferenceManager.getString(Constants.KEY_USER_ID)).
                whereEqualTo(Constants.KEY_RECEIVER_ID, receiver.getId()).
                addSnapshotListener(eventListener);
        firebaseFirestore.collection(Constants.KEY_COLLECTION_CHAT).
                whereEqualTo(Constants.KEY_SENDER_ID, receiver.getId()).
                whereEqualTo(Constants.KEY_RECEIVER_ID,preferenceManager.getString(Constants.KEY_USER_ID)).
                addSnapshotListener(eventListener);
    }
    private void checkForConversion(){
        if(chatMessages.size()!=0){
            checkForConversionRemotely(preferenceManager.getString(
                    Constants.KEY_USER_ID), receiver.getId());
            checkForConversionRemotely(receiver.getId(), preferenceManager.getString(
                    Constants.KEY_USER_ID));
        }
    }
    private void checkForConversionRemotely(String senderId, String receiverId){
        firebaseFirestore.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID,senderId)
                .whereEqualTo(Constants.KEY_RECEIVER_ID,receiverId)
                .get().addOnCompleteListener(completeListener);
    }
}