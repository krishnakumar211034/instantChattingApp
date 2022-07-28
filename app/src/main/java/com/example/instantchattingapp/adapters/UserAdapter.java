package com.example.instantchattingapp.adapters;


import static android.util.Base64.decode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instantchattingapp.databinding.ItemContainerUserBinding;
import com.example.instantchattingapp.listeners.UserListener;
import com.example.instantchattingapp.models.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{
    private final List<User> arr;
    private final UserListener userListener;
    public UserAdapter(List<User> arr, UserListener userListener) {
        this.arr = arr;
        this.userListener = userListener;
    }

    @NonNull
    @Override
    // creates the UserViewHolder using inflate operation
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),parent,false);
        return new UserViewHolder(itemContainerUserBinding);
    }
    @Override
    // calls upon the method from UserViewHolder class to setdata into layout from the adapter
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setData(arr.get(position));
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder{
        private ItemContainerUserBinding binding;
        UserViewHolder(ItemContainerUserBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
        public void setData(User user){
            binding.textName.setText(user.getName());
            binding.textEmail.setText(user.getEmail() );
//            binding.userProfile.setImageBitmap(getUserImage(user.getName()));
            binding.getRoot().setOnClickListener(view -> userListener.onUserClicked(user));
        }
    }
    // converts image in the string format to Bitmap and currently it is not working
    private Bitmap getUserImage(String encodedImage){
        byte[] bytes = decode(encodedImage, Base64.NO_WRAP);
        return BitmapFactory.decodeByteArray(bytes,0, bytes.length);
    }
}










