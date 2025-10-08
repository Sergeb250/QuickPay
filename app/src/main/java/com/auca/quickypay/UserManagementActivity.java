package com.auca.quickypay;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.auca.quickypay.sqlite.dbHelper;

import java.util.ArrayList;
import java.util.List;

public class UserManagementActivity extends AppCompatActivity {

    private static final String TAG = "UserManagement";
    private Button btnAddUser;
    private RecyclerView userRecyclerView;
    private LinearLayout emptyUserState;
    private dbHelper databaseHelper;
    private Toolbar toolbar;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        // Handle system bars padding
        View mainView = findViewById(R.id.main);
        if (mainView == null) {
            Log.e(TAG, "Main layout (R.id.main) not found");
            Toast.makeText(this, "Layout initialization failed: Main layout not found", Toast.LENGTH_LONG).show();
            return;
        }
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize database helper
        databaseHelper = new dbHelper(this);

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        btnAddUser = findViewById(R.id.btnAddUser);
        userRecyclerView = findViewById(R.id.userRecyclerView);
        emptyUserState = findViewById(R.id.emptyUserState);

        if (toolbar == null || btnAddUser == null || userRecyclerView == null || emptyUserState == null) {
            Log.e(TAG, "View initialization failed: " +
                    "toolbar=" + toolbar + ", btnAddUser=" + btnAddUser +
                    ", userRecyclerView=" + userRecyclerView + ", emptyUserState=" + emptyUserState);
            Toast.makeText(this, "View initialization failed: One or more views not found", Toast.LENGTH_LONG).show();
            return;
        }

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Setup RecyclerView
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter();
        userRecyclerView.setAdapter(userAdapter);

        btnAddUser.setOnClickListener(v -> {
            Log.d(TAG, "Add User button clicked");
            Intent intent = new Intent(UserManagementActivity.this, UpdateUserActivity.class);
            startActivity(intent);
        });

        loadUsers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload users when returning to this activity
        loadUsers();
    }

    private void loadUsers() {
        Log.d(TAG, "Loading users from SQLite database");
        try {
            List<User> users = databaseHelper.getAllUsers();
            Log.d(TAG, "Users loaded: " + users.size());

            List<UserItem> userList = new ArrayList<>();
            for (User user : users) {
                // Use email as unique identifier since SQLite doesn't have userId
                userList.add(new UserItem(user, user.getEmail()));
                Log.d(TAG, "Loaded user: " + user.getUsername());
            }

            userAdapter.setUsers(userList);

            if (userList.isEmpty()) {
                Log.d(TAG, "No users found, showing empty state");
                userRecyclerView.setVisibility(View.GONE);
                emptyUserState.setVisibility(View.VISIBLE);
            } else {
                Log.d(TAG, "Users found: " + userList.size());
                userRecyclerView.setVisibility(View.VISIBLE);
                emptyUserState.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading users: " + e.getMessage());
            Toast.makeText(this, "Failed to load users: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void showUserDetails(User user) {
        Log.d(TAG, "Showing details for user: " + user.getUsername());

        // Create a simple AlertDialog without custom layout
        String userDetails = "Username: " + (user.getUsername() != null ? user.getUsername() : "N/A") +
                "\n\nEmail: " + (user.getEmail() != null ? user.getEmail() : "N/A") +
                "\n\nPassword: " + (user.getPassword() != null && !user.getPassword().isEmpty() ? "••••••••" : "Not set");

        new AlertDialog.Builder(this)
                .setTitle("User Details")
                .setMessage(userDetails)
                .setPositiveButton("Close", (dialog, which) -> {
                    Log.d(TAG, "User details dialog closed");
                })
                .show();
    }

    private void confirmDeleteUser(User user, String userEmail) {
        Log.d(TAG, "Confirming delete for user: " + user.getUsername());
        new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete " + user.getUsername() + "?\n\nThis action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    Log.d(TAG, "Deleting user: " + userEmail);
                    boolean success = databaseHelper.deleteUser(userEmail);
                    if (success) {
                        Toast.makeText(this, user.getUsername() + " deleted successfully.", Toast.LENGTH_SHORT).show();
                        loadUsers(); // Reload the list
                    } else {
                        Log.e(TAG, "Failed to delete user");
                        Toast.makeText(this, "Failed to delete user", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> Log.d(TAG, "Delete canceled"))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private static class UserItem {
        User user;
        String userEmail;

        UserItem(User user, String userEmail) {
            this.user = user;
            this.userEmail = userEmail;
        }
    }

    private class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
        private List<UserItem> userList = new ArrayList<>();

        public void setUsers(List<UserItem> users) {
            userList = users;
            notifyDataSetChanged();
            Log.d(TAG, "Adapter updated with " + users.size() + " users");
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
            String userEmail = item.userEmail;

            Log.d(TAG, "Binding user: " + user.getUsername() + ", position: " + position);
            holder.tvName.setText(user.getUsername() != null ? user.getUsername() : "N/A");
            holder.tvEmail.setText(user.getEmail() != null ? user.getEmail() : "N/A");
            holder.tvInitial.setText(user.getUsername() != null && !user.getUsername().isEmpty()
                    ? String.valueOf(user.getUsername().charAt(0)).toUpperCase()
                    : "?");

            holder.btnView.setOnClickListener(v -> showUserDetails(user));
            holder.btnEdit.setOnClickListener(v -> {
                Log.d(TAG, "Editing user: " + userEmail);
                Intent intent = new Intent(UserManagementActivity.this, UpdateUserActivity.class);
                intent.putExtra("editUserEmail", userEmail);
                intent.putExtra("editUserName", user.getUsername());
                intent.putExtra("editUserPassword", user.getPassword());
                startActivity(intent);
            });
            holder.btnDelete.setOnClickListener(v -> confirmDeleteUser(user, userEmail));
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

                if (tvName == null || tvEmail == null || tvInitial == null ||
                        btnView == null || btnEdit == null || btnDelete == null) {
                    Log.e(TAG, "User item view IDs not found");
                    Toast.makeText(UserManagementActivity.this,
                            "User item view initialization failed", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}