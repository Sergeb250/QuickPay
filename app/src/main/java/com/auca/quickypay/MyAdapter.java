package com.auca.quickypay;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.auca.quickypay.Model.User;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<User> userList;

    // Constructor
    public MyAdapter(List<User> userList) {
        this.userList = userList;
    }

    // ViewHolder class
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfileAvatar;
        TextView tvUserName, tvUserEmail;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileAvatar = itemView.findViewById(R.id.ivProfileAvatar);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item_layout, parent, false); // Use your layout file name
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = userList.get(position);
        holder.tvUserName.setText(user.getUsername());
        holder.tvUserEmail.setText(user.getEmail());

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
