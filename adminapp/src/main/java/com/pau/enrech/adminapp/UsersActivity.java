package com.pau.enrech.adminapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.CoordinatorLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
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
    private DatabaseReference activos;
    private int activeUsers = 0;
    private EditText searchText;
    private Query queryRef;



    public class UsersViewHolder extends RecyclerView.ViewHolder{
        public TextView user_nomAp;
        public TextView user_elim;
        public AppCompatButton user_active;
        View mView;

        UsersViewHolder(View root) {
            super(root);
            mView = root;
            user_nomAp = root.findViewById(R.id.UserNomApText);
            user_active = root.findViewById(R.id.UserActiveCircle);
            user_elim = root.findViewById(R.id.descalificandoUserText);

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
        activos = root.child("juego").child("activos");
        recyclerview = findViewById(R.id.UsersRecyclerView);
        searchText = findViewById(R.id.UsersSearchInput);

        /*searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String QueryText;
                    QueryText = s.toString();
                    Log.d("countValue", "onTextChanged: valor de count "+count);
                    if (count == 0){
                        queryRef = jugadores.orderByChild("cognom").limitToLast(100);

                    }
                    else{
                        QueryText = QueryText.substring(0,1).toUpperCase() + QueryText.substring(1);
                        queryRef = jugadores.orderByChild("nom").startAt(QueryText).endAt(QueryText + "\uf8ff");
                    }
                    Log.d("QueryText", "onTextChanged: valor de queryText "+QueryText);
                    searchListUser(queryRef);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        //Read active
        activos.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                activeUsers = dataSnapshot.getValue(Integer.class);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("error datos activos", "Failed to read value.", error.toException());
            }
        });

        queryRef = jugadores.orderByChild("cognom").limitToLast(100);
        searchListUser(queryRef);

    }

    public void searchForUser(View view){
        String queryText;
        queryText = searchText.getText().toString();
        if(queryText.length() == 0){
            queryRef = jugadores.orderByChild("cognom").limitToLast(100);
        }
        else{
            queryText = queryText.substring(0,1).toUpperCase() + queryText.substring(1);
            queryRef = jugadores.orderByChild("nom").startAt(queryText).endAt(queryText + "\uf8ff");
        }
        adapter.stopListening();
        searchListUser(queryRef);

    }

    public void searchListUser(Query queryToSearch){
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(queryToSearch, User.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<User, UsersActivity.UsersViewHolder>(options) {
            @NonNull
            @Override
            public UsersActivity.UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
                return new UsersActivity.UsersViewHolder(root);

            }
            @Override
            protected void onBindViewHolder(@NonNull UsersActivity.UsersViewHolder holder, final int position, @NonNull final User model) {
                holder.user_nomAp.setText(model.getNomAp());
                ViewCompat.setBackgroundTintList(holder.user_active,ColorStateList.valueOf(getResources().getColor(model.getActive())));
                if(model.active == model.active.ELIMINATING){
                    holder.user_elim.setVisibility(View.VISIBLE);
                }
                else{
                    holder.user_elim.setVisibility(View.GONE);
                }
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(model.active != model.active.ELIMINATING && model.active != model.active.ELIMINATED){
                            eliminateUser(model.getNomAp(),adapter.getRef(position));
                        }
                    }
                });
            }
        };

        LinearLayoutManager manager = new LinearLayoutManager(this);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerview.getContext(),
                manager.getOrientation());


        recyclerview.setLayoutManager(manager);
        recyclerview.addItemDecoration(dividerItemDecoration);
        recyclerview.setAdapter(adapter);
        adapter.startListening();
    }

    public void eliminateUser(String nomAp, final DatabaseReference ref){
        final Snackbar snackbar1 = Snackbar
                .make(recyclerview, "Error descalificando al jugador", Snackbar.LENGTH_LONG);
        final Snackbar snackbar2 = Snackbar
                .make(recyclerview, "No puedes descalificar al ganador", Snackbar.LENGTH_LONG);

        String msg = "¿Descalificar a " +nomAp+ " ?";
        String title = "Descalificar jugador";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(activeUsers < 1){
                    snackbar1.show();
                }
                else if(activeUsers == 1){
                    snackbar2.show();
                }
                else{
                    ref.child("active").setValue("ELIMINATING");
                }
            }
        });
        builder.setNegativeButton(android.R.string.no, null);
        builder.create().show();

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
