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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserManagementActivity extends AppCompatActivity {

    private static final String TAG = "UserManagement";
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

        // Initialize Firebase with fresh instance
        try {
            usersRef = FirebaseDatabase.getInstance().getReference("Users");
            Log.d(TAG, "Firebase initialized, usersRef: " + usersRef.toString());
        } catch (Exception e) {
            Log.e(TAG, "Firebase initialization failed: " + e.getMessage());
            Toast.makeText(this, "Firebase initialization failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        btnAddUser.setOnClickListener(v -> {
            Log.d(TAG, "Add User button clicked");
            Intent intent = new Intent(UserManagementActivity.this, register.class);
            startActivity(intent);
        });

        loadUsersFromFirebase();
    }

    private void loadUsersFromFirebase() {
        Log.d(TAG, "Loading users from Firebase");
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d(TAG, "Snapshot received, children count: " + snapshot.getChildrenCount());
                List<UserItem> userList = new ArrayList<>();
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    Log.d(TAG, "User data: " + userSnap.getValue());
                    try {
                        // Check if the data is a Map (object) before deserializing
                        if (userSnap.getValue() instanceof String) {
                            Log.w(TAG, "" + userSnap.getKey() + ", found String: " + userSnap.getValue());
                            Toast.makeText(UserManagementActivity.this,
                                    ": " + userSnap.getKey(), Toast.LENGTH_SHORT).show();
                            continue;
                        }
                        User user = userSnap.getValue(User.class);
                        if (user != null) {
                            userList.add(new UserItem(user, userSnap.getKey()));
                            Log.d(TAG, "Parsed user: " + user.toString());
                        } else {
                            Log.w(TAG, "Failed to parse user for key: " + userSnap.getKey() + ", user is null");
                            Toast.makeText(UserManagementActivity.this,
                                    "Failed to parse user data for key: " + userSnap.getKey(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing user for key: " + userSnap.getKey() + ", error: " + e.getMessage());
                        Toast.makeText(UserManagementActivity.this,
                                "Error parsing user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                userAdapter.setUsers(userList);
                if (userList.isEmpty()) {
                    Log.d(TAG, "No valid users found, showing empty state");
                    userRecyclerView.setVisibility(View.GONE);
                    emptyUserState.setVisibility(View.VISIBLE);
                } else {
                    Log.d(TAG, "Users found: " + userList.size());
                    userRecyclerView.setVisibility(View.VISIBLE);
                    emptyUserState.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Failed to load users: " + error.getMessage());
                Toast.makeText(UserManagementActivity.this,
                        "Failed to load users: " + error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showUserDetails(User user) {
        Log.d(TAG, "Showing details for user: " + user.getUsername());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = findViewById(R.id.userDetailsContainer);
        if (dialogView == null) {
            Log.e(TAG, "userDetailsContainer not found");
            Toast.makeText(this, "User details layout not found", Toast.LENGTH_LONG).show();
            return;
        }
        dialogView.setVisibility(View.VISIBLE);

        TextView tvName = dialogView.findViewById(R.id.tvDetailName);
        TextView tvEmail = dialogView.findViewById(R.id.tvDetailEmail);
        TextView tvPassword = dialogView.findViewById(R.id.tvDetailPassword);

        if (tvName == null || tvEmail == null || tvPassword == null) {
            Log.e(TAG, "Dialog view IDs not found");
            dialogView.setVisibility(View.GONE);
            Toast.makeText(this, "User details fields not found", Toast.LENGTH_LONG).show();
            return;
        }

        tvName.setText(user.getUsername() != null ? user.getUsername() : "N/A");
        tvEmail.setText(user.getEmail() != null ? user.getEmail() : "N/A");
        tvPassword.setText(user.getPassword() != null && !user.getPassword().isEmpty() ? "••••••••" : "Not set");

        builder.setView(dialogView)
                .setTitle("User Details")
                .setPositiveButton("Close", (dialog, which) -> {
                    Log.d(TAG, "User details dialog closed");
                    dialogView.setVisibility(View.GONE);
                })
                .show();
    }

    private void confirmDeleteUser(User user, String userId) {
        Log.d(TAG, "Confirming delete for user: " + user.getUsername());
        new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete " + user.getUsername() + "?\n\nThis action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    Log.d(TAG, "Deleting user: " + userId);
                    usersRef.child(userId).removeValue()
                            .addOnSuccessListener(aVoid ->
                                    Toast.makeText(this, user.getUsername() + " deleted successfully.",
                                            Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to delete user: " + e.getMessage());
                                Toast.makeText(this, "Failed to delete user: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            });
                })
                .setNegativeButton("Cancel", (dialog, which) -> Log.d(TAG, "Delete canceled"))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private static class UserItem {
        User user;
        String userId;

        UserItem(User user, String userId) {
            this.user = user;
            this.userId = userId;
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
            String userId = item.userId;

            Log.d(TAG, "Binding user: " + user.getUsername() + ", position: " + position);
            holder.tvName.setText(user.getUsername() != null ? user.getUsername() : "N/A");
            holder.tvEmail.setText(user.getEmail() != null ? user.getEmail() : "N/A");
            holder.tvInitial.setText(user.getUsername() != null && !user.getUsername().isEmpty()
                    ? String.valueOf(user.getUsername().charAt(0)).toUpperCase()
                    : "?");

            holder.btnView.setOnClickListener(v -> showUserDetails(user));
            holder.btnEdit.setOnClickListener(v -> {
                Log.d(TAG, "Editing user: " + userId);
                Intent intent = new Intent(UserManagementActivity.this, register.class);
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