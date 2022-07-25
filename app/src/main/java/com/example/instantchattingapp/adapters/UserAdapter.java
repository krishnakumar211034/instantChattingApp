package com.example.instantchattingapp.adapters;

import static android.util.Base64.decode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instantchattingapp.R;
import com.example.instantchattingapp.listeners.UserListener;
import com.example.instantchattingapp.models.User;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ChatHolder>{
        private ArrayList<User> arr;
        private Context context;
        private final UserListener userListener;
        public UserAdapter(ArrayList<User> arr, Context context, UserListener userListener) {
            this.arr = arr;
            this.context = context;
            this.userListener = userListener;
        }
        public ChatHolder onCreateViewHolder(ViewGroup parent,int viewType){
            View inflater = LayoutInflater.from(context).inflate(R.layout.item_container_user,parent,false);
            ChatHolder holder =new ChatHolder(inflater);
            return holder;
        }
        class ChatHolder extends RecyclerView.ViewHolder {
            ConstraintLayout root;
            TextView name, email;
            ImageView img;

            public ChatHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.textName);
                email = itemView.findViewById(R.id.textEmail);
                img = itemView.findViewById(R.id.userProfile);
                root = (ConstraintLayout) itemView.getRootView();
            }
        }
    public void onBindViewHolder(ChatHolder holder, int position) {
            int index=position;
            holder.name.setText(arr.get(position).getName());
            holder.email.setText(arr.get(position).getEmail());
            holder.img.setImageBitmap(getUserImage(arr.get(position).getImage()));
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    userListener.onUserClicked(arr.get(index));
                }
            });
        }
        public int getItemCount() {
            return arr.size();
        }
        private Bitmap getUserImage(String encodedImage){
            byte[] bytes = decode(encodedImage, android.util.Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes,0, bytes.length);
        }
}
