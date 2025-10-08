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

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class UserManagementActivity extends AppCompatActivity {

    private static final String TAG = "UserManagement";
    private Button btnAddUser, btnReport;
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
            Toast.makeText(this, "Layout initialization failed", Toast.LENGTH_LONG).show();
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
        btnReport = findViewById(R.id.btnReport);
        userRecyclerView = findViewById(R.id.userRecyclerView);
        emptyUserState = findViewById(R.id.emptyUserState);

        if (toolbar == null || btnAddUser == null || btnReport == null ||
                userRecyclerView == null || emptyUserState == null) {
            Log.e(TAG, "View initialization failed: Some views not found");
            Toast.makeText(this, "Layout error", Toast.LENGTH_LONG).show();
            return;
        }

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // RecyclerView setup
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter();
        userRecyclerView.setAdapter(userAdapter);

        // Add user
        btnAddUser.setOnClickListener(v -> {
            Intent intent = new Intent(UserManagementActivity.this, UpdateUserActivity.class);
            startActivity(intent);
        });

        // Generate CSV report
        btnReport.setOnClickListener(v -> generateCSVReport());

        // Load users
        loadUsers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers();
    }

    private void loadUsers() {
        try {
            List<User> users = databaseHelper.getAllUsers();
            List<UserItem> userList = new ArrayList<>();
            for (User user : users) {
                userList.add(new UserItem(user, user.getEmail()));
            }

            userAdapter.setUsers(userList);

            if (userList.isEmpty()) {
                userRecyclerView.setVisibility(View.GONE);
                emptyUserState.setVisibility(View.VISIBLE);
            } else {
                userRecyclerView.setVisibility(View.VISIBLE);
                emptyUserState.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Failed to load users: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void showUserDetails(User user) {
        String userDetails = "Username: " + (user.getUsername() != null ? user.getUsername() : "N/A") +
                "\n\nEmail: " + (user.getEmail() != null ? user.getEmail() : "N/A") +
                "\n\nPassword: " + (user.getPassword() != null && !user.getPassword().isEmpty() ? "••••••" : "Not set");

        new AlertDialog.Builder(this)
                .setTitle("User Details")
                .setMessage(userDetails)
                .setPositiveButton("Close", null)
                .show();
    }

    private void confirmDeleteUser(User user, String userEmail) {
        new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete " + user.getUsername() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    boolean success = databaseHelper.deleteUser(userEmail);
                    if (success) {
                        Toast.makeText(this, user.getUsername() + " deleted successfully.", Toast.LENGTH_SHORT).show();
                        loadUsers();
                    } else {
                        Toast.makeText(this, "Failed to delete user", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // CSV REPORT
    private void generateCSVReport() {
        try {
            List<User> users = databaseHelper.getAllUsers();
            if (users.isEmpty()) {
                Toast.makeText(this, "No users to export.", Toast.LENGTH_SHORT).show();
                return;
            }

            File csvFile = new File(getExternalFilesDir(null), "User_Report.csv");
            FileWriter writer = new FileWriter(csvFile);

            // Header
            writer.append("Username,Email,Password\n");

            // Data
            for (User user : users) {
                writer.append(escapeCSV(user.getUsername())).append(",")
                        .append(escapeCSV(user.getEmail())).append(",")
                        .append(escapeCSV(user.getPassword())).append("\n");
            }

            writer.flush();
            writer.close();

            Toast.makeText(this, "CSV saved: " + csvFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
            Log.d(TAG, "CSV generated at: " + csvFile.getAbsolutePath());

        } catch (Exception e) {
            Log.e(TAG, "CSV generation failed: " + e.getMessage());
            Toast.makeText(this, "Failed to generate CSV: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String escapeCSV(String text) {
        if (text == null) return "";
        if (text.contains(",") || text.contains("\"")) {
            text = "\"" + text.replace("\"", "\"\"") + "\"";
        }
        return text;
    }

    // RECYCLER ADAPTER
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

            holder.tvName.setText(user.getUsername());
            holder.tvEmail.setText(user.getEmail());
            holder.tvInitial.setText(user.getUsername() != null && !user.getUsername().isEmpty()
                    ? String.valueOf(user.getUsername().charAt(0)).toUpperCase()
                    : "?");

            holder.btnView.setOnClickListener(v -> showUserDetails(user));
            holder.btnEdit.setOnClickListener(v -> {
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
            }
        }
    }
}
