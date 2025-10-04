package com.auca.quickypay;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.auca.quickypay.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserManagement extends AppCompatActivity {

    private Button btnAddUser;
    private RecyclerView userRecyclerView;
    private LinearLayout emptyUserState;
    private DatabaseReference usersRef;
    private Toolbar toolbar;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        // Handle system bars padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(v -> finish());

        btnAddUser = findViewById(R.id.btnAddUser);
        userRecyclerView = findViewById(R.id.userRecyclerView);
        emptyUserState = findViewById(R.id.emptyUserState);

        // Setup RecyclerView
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter();
        userRecyclerView.setAdapter(userAdapter);

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        btnAddUser.setOnClickListener(v -> {
            Intent intent = new Intent(UserManagement.this, register.class);
            startActivity(intent);
        });

        loadUsersFromFirebase();
    }

    private void loadUsersFromFirebase() {
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<UserItem> userList = new ArrayList<>();
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    User user = userSnap.getValue(User.class);
                    if (user != null) {
                        userList.add(new UserItem(user, userSnap.getKey()));
                    }
                }
                userAdapter.setUsers(userList);
                if (userList.isEmpty()) {
                    userRecyclerView.setVisibility(View.GONE);
                    emptyUserState.setVisibility(View.VISIBLE);
                } else {
                    userRecyclerView.setVisibility(View.VISIBLE);
                    emptyUserState.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(UserManagement.this,
                        "Failed to load users: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUserDetails(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = findViewById(R.id.userDetailsContainer);
        dialogView.setVisibility(View.VISIBLE);

        TextView tvName = dialogView.findViewById(R.id.tvDetailName);
        TextView tvEmail = dialogView.findViewById(R.id.tvDetailEmail);
        TextView tvPassword = dialogView.findViewById(R.id.tvDetailPassword);

        tvName.setText(user.getUsername());
        tvEmail.setText(user.getEmail());
        tvPassword.setText(user.getPassword() != null && !user.getPassword().isEmpty() ? "••••••••" : "Not set");

        builder.setView(dialogView)
                .setTitle("User Details")
                .setPositiveButton("Close", (dialog, which) -> dialogView.setVisibility(View.GONE))
                .show();
    }

    private void confirmDeleteUser(User user, String userId) {
        new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete " + user.getUsername() + "?\n\nThis action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    usersRef.child(userId).removeValue()
                            .addOnSuccessListener(aVoid ->
                                    Toast.makeText(this, user.getUsername() + " deleted successfully.",
                                            Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Failed to delete user: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    // UserItem class to hold User and userId together
    private static class UserItem {
        User user;
        String userId;

        UserItem(User user, String userId) {
            this.user = user;
            this.userId = userId;
        }
    }

    // RecyclerView Adapter
    private class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
        private List<UserItem> userList = new ArrayList<>();

        public void setUsers(List<UserItem> users) {
            userList = users;
            notifyDataSetChanged();
        }

        @Override
        public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_item_layout, parent, false);
            return new UserViewHolder(view);
        }

        @Override
        public void onBindViewHolder(UserViewHolder holder, int position) {
            UserItem item = userList.get(position);
            User user = item.user;
            String userId = item.userId;

            holder.tvName.setText(user.getUsername());
            holder.tvEmail.setText(user.getEmail());
            holder.tvInitial.setText(user.getUsername() != null && !user.getUsername().isEmpty()
                    ? String.valueOf(user.getUsername().charAt(0)).toUpperCase()
                    : "?");

            holder.btnView.setOnClickListener(v -> showUserDetails(user));
            holder.btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(UserManagement.this, register.class);
                intent.putExtra("editUserId", userId);
                intent.putExtra("editUser", user);
                startActivity(intent);
            });
            holder.btnDelete.setOnClickListener(v -> confirmDeleteUser(user, userId));
        }

        @Override
        public int getItemCount() {
            return userList.size();
        }

        class UserViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvEmail, tvInitial;
            ImageButton btnView, btnEdit, btnDelete;

            UserViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvUserNameItem);
                tvEmail = itemView.findViewById(R.id.tvUserEmailItem);
                tvInitial = itemView.findViewById(R.id.tvUserInitial);
                btnView = itemView.findViewById(R.id.btnViewUser);
                btnEdit = itemView.findViewById(R.id.btnEditUser);
                btnDelete = itemView.findViewById(R.id.btnDeleteUser);
            }
        }
    }
}