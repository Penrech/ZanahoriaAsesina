package com.pau.enrech.adminapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class StatisticsActivity extends AppCompatActivity {

    private ConstraintLayout StaticsAdminLayout;
    private RecyclerView recyclerview;
    private FirebaseDatabase database;
    private DatabaseReference root;
    private FirebaseRecyclerAdapter<Statistics, StaticsViewHolder> adapter;
    private DatabaseReference staticsRef;
    private DatabaseReference juego;
    private int contador;
    private int activos;
    private TextView staticsCounter;

    public class StaticsViewHolder extends RecyclerView.ViewHolder {
        public TextView statics_fecha, statics_hora, statics_message;

        StaticsViewHolder(View root) {
            super(root);
            statics_fecha = root.findViewById(R.id.UserActive);
            statics_hora = root.findViewById(R.id.StaticsHora);
            statics_message  = root.findViewById(R.id.StaticsTextItem);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.user_list_item:
                goTo(UsersActivity.class);
                break;
        }
        return true;
    }

    private  void goTo(Class activity){
        Intent intent = new Intent(this,activity);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        StaticsAdminLayout = findViewById(R.id.statics_admin_constrain);
        database = FirebaseDatabase.getInstance();
        root = database.getReference();
        juego = root.child("juego");
        staticsRef = root.child("estadisticas");
        staticsCounter = findViewById(R.id.StaticsCounterView);
        recyclerview = findViewById(R.id.StaticsRecyclerView);

        //Read cont
        juego.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                contador = dataSnapshot.child("totales").getValue(Integer.class);
                activos = dataSnapshot.child("activos").getValue(Integer.class);
                String cont = String.format("%d / %d ",activos,contador);
                staticsCounter.setText(cont);
                StaticsAdminLayout.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("error datos contador", "Failed to read value.", error.toException());
            }
        });


        Query query = staticsRef.limitToLast(100);

        FirebaseRecyclerOptions<Statistics> options = new FirebaseRecyclerOptions.Builder<Statistics>()
                .setQuery(query, Statistics.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Statistics, StaticsViewHolder>(options) {
            @NonNull
            @Override
            public StaticsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.statistics_item, parent, false);
                return new StaticsViewHolder(root);
            }
            @Override
            protected void onBindViewHolder(@NonNull StaticsViewHolder holder, int position, @NonNull Statistics model) {
                holder.statics_fecha.setText(model.getDayString());
                holder.statics_hora.setText(model.getHourString());
                holder.statics_message.setText(model.getStaticsMessageString());
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
