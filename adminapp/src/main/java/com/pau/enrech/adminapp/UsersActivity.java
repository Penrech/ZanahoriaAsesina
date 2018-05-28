package com.pau.enrech.adminapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UsersActivity extends AppCompatActivity {

    private ConstraintLayout UsersAdminLayout;
    private RecyclerView recyclerview;
    private FirebaseDatabase database;
    private DatabaseReference root;
    private FirebaseRecyclerAdapter<User, UsersActivity.UsersViewHolder> adapter;
    private DatabaseReference jugadores;



    public class UsersViewHolder extends RecyclerView.ViewHolder {
        public TextView user_nomAp;
        public AppCompatButton user_active;

        UsersViewHolder(View root) {
            super(root);
            user_nomAp = root.findViewById(R.id.UserNomApText);
            user_active = root.findViewById(R.id.UserActiveCircle);

        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        UsersAdminLayout = findViewById(R.id.users_admin_constrain);
        database = FirebaseDatabase.getInstance();
        root = database.getReference();
        jugadores = root.child("jugadores");
        recyclerview = findViewById(R.id.UsersRecyclerView);


        Query query = jugadores.orderByChild("cognom").limitToLast(100);

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<User, UsersActivity.UsersViewHolder>(options) {
            @NonNull
            @Override
            public UsersActivity.UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
                return new UsersActivity.UsersViewHolder(root);
            }
            @Override
            protected void onBindViewHolder(@NonNull UsersActivity.UsersViewHolder holder, int position, @NonNull User model) {
                holder.user_nomAp.setText(model.getNomAp());
                ViewCompat.setBackgroundTintList(holder.user_active,ColorStateList.valueOf(getResources().getColor(model.getActive())));

            }
        };

        LinearLayoutManager manager = new LinearLayoutManager(this);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerview.getContext(),
                manager.getOrientation());


        recyclerview.setLayoutManager(manager);
        recyclerview.addItemDecoration(dividerItemDecoration);
        recyclerview.setAdapter(adapter);


    }
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}
