package com.pau.enrech.zanahoriaasesina;

import android.content.DialogInterface;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

public class TargetActivity extends AppCompatActivity {

    private ConstraintLayout frameWinner,frameTarget,frameEliminated,frameLimbo;
    private TextView nameSurname;
    private TextView edad;
    private TextView penya;
    private TextView winRank;
    private TextView loseRank;
    private String jugadorId = "jugador1";
    private String targetId;
    private String killerId;
    private ImageView img;
    private User user;
    private User target;
    private Button eliminate_btn;
    private FirebaseDatabase database;
    private DatabaseReference root;
    private DatabaseReference targetData;
    private DatabaseReference jugadores;
    private DatabaseReference juego;
    private DatabaseReference JugadoresActivos;
    private int contador;
    private int activos;
    private ValueEventListener usersListener;
    private ValueEventListener targetsListener;
    private ValueEventListener killerListener;
    private Query getKiller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);
        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        root = database.getReference();
        jugadores = database.getReference("jugadores/"+jugadorId);
        juego = database.getReference("juego");
        JugadoresActivos = database.getReference("juego/activos");

        frameTarget = findViewById(R.id.frame_target);
        frameWinner = findViewById(R.id.frame_winner);
        frameEliminated = findViewById(R.id.frame_loser);
        frameLimbo = findViewById(R.id.confirm_elimination);

        loseRank = findViewById(R.id.lose_rank);
        winRank = findViewById(R.id.win_rank);
        eliminate_btn = findViewById(R.id.target_eliminate);
        img = findViewById(R.id.target_photo);
        nameSurname = findViewById(R.id.target_nameSurname);
        edad = findViewById(R.id.target_age);
        penya = findViewById(R.id.target_penya);

        frameWinner.setVisibility(View.GONE);
        frameTarget.setVisibility(View.VISIBLE);

        //Read cont
        juego.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                contador = dataSnapshot.child("totales").getValue(Integer.class);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("error datos contador", "Failed to read value.", error.toException());
            }
        });

        initialListeners();
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        jugadores.removeEventListener(usersListener);
        if (targetData != null) {
            targetData.removeEventListener(targetsListener);
        }
        if (getKiller != null) {
            getKiller.removeEventListener(killerListener);
        }
        switch (item.getItemId()){
            case R.id.jugador_pau:
                jugadorId = "jugador2";
                jugadores = database.getReference("jugadores/"+jugadorId);
                break;
            case  R.id.jugador_laura:
                jugadorId = "jugador3";
                jugadores = database.getReference("jugadores/"+jugadorId);
                break;
            case  R.id.jugador_daniel:
                jugadorId = "jugador1";
                jugadores = database.getReference("jugadores/"+jugadorId);
                break;

        }
        initialListeners();
        return true;
    }*/

    public void initialListeners(){
        // Read players
        usersListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User value = dataSnapshot.getValue(User.class);
                user = value;
                if (user.active == user.active.ACTIVE)
                {
                    frameEliminated.setVisibility(View.GONE);
                    frameWinner.setVisibility(View.GONE);
                    frameTarget.setVisibility(View.VISIBLE);
                    frameLimbo.setVisibility(View.GONE);
                    targetId = value.target;
                    targetData = database.getReference("jugadores/"+targetId);
                    updateData();
                }
                else if(user.active == user.active.LIMBO){
                    frameEliminated.setVisibility(View.GONE);
                    frameWinner.setVisibility(View.GONE);
                    frameTarget.setVisibility(View.GONE);
                    frameLimbo.setVisibility(View.VISIBLE);

                }
                else if(user.active == user.active.ELIMINATED){
                    loseRank.setText(String.format("%d / %d",user.ranking,contador));
                    frameEliminated.setVisibility(View.VISIBLE);
                    frameWinner.setVisibility(View.GONE);
                    frameTarget.setVisibility(View.GONE);
                    frameLimbo.setVisibility(View.GONE);

                }
                else if(user.active == user.active.WINNER){
                    winRank.setText(String.format("%d / %d",user.ranking,contador));
                    frameEliminated.setVisibility(View.GONE);
                    frameWinner.setVisibility(View.VISIBLE);
                    frameTarget.setVisibility(View.GONE);
                    frameLimbo.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("error datos usuario", "Failed to read value.", error.toException());
            }
        };
        jugadores.addValueEventListener(usersListener);
    }

    public void updateData(){
        targetsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User value = dataSnapshot.getValue(User.class);
                target = value;
                nameSurname.setText(String.format("%s %s",target.nom,target.cognom));
                edad.setText(String.format("%d",target.age));
                penya.setText(String.format("%s",target.penya));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("error datos target", "Failed to read value.", error.toException());
            }
        };
        targetData.addValueEventListener(targetsListener);
    }

    public void demandUserElimination(View view){
        if (target.active == target.active.LIMBO){
            String msg = target.nom + " " + target.cognom + " ya tiene pendiente una solicitud de eliminación, " +
                    "debes esperar a que la resuelva antes de poder manderle otra. En caso de que tarde demasiado en " +
                    "resolverla, puedes enviar un reporte a la administración para que medie entre vosotr@s.";
            String title = "Solicitud en tramite";
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(title);
            builder.setMessage(msg);
            builder.setPositiveButton(android.R.string.ok, null);
            builder.create().show();
        }
        else {
            String msg = "¿Has eliminado a " + target.nom + " " + target.cognom + " ?";
            String title = "Solicitar eliminación";
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(title);
            builder.setMessage(msg);
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    root.child("jugadores").child(targetId).child("active").setValue("LIMBO");
                }
            });
            builder.setNegativeButton(android.R.string.no, null);
            builder.create().show();
        }

    }
    public void acceptUserElimination(View view){
        String msg = "¿Aceptar haber sido eliminad@?";
        String title = "Confirmar eliminación";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                preEliminateUser();
            }
        });
        builder.setNegativeButton(android.R.string.no, null);
        builder.create().show();

    }
    public void declineUserElimination(View view){
        String msg = "¿Rechazar haber sido eliminad@?";
        String title = "Rechazar eliminación";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                root.child("jugadores").child(jugadorId).child("active").setValue("ACTIVE");
            }
        });
        builder.setNegativeButton(android.R.string.no, null);
        builder.create().show();
    }

    public void preEliminateUser(){
        killerListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot objSnapshot: dataSnapshot.getChildren()) {
                    killerId = objSnapshot.getKey();
                }
                eliminateUser();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        getKiller = root.child("jugadores").orderByChild("target").equalTo(jugadorId);
        getKiller.addListenerForSingleValueEvent(killerListener);

    }
    public void eliminateUser(){

            final Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/jugadores/" + killerId + "/target", user.target);
            childUpdates.put("/jugadores/" + jugadorId + "/target", null);
            childUpdates.put("/jugadores/" + jugadorId + "/active", "ELIMINATED");



            JugadoresActivos.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                    int activeCont = mutableData.getValue(Integer.class);
                    mutableData.setValue(activeCont-1);
                    return Transaction.success(mutableData);

            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue(Integer.class) == 1){
                        childUpdates.put("/jugadores/" + killerId + "/ranking", dataSnapshot.getValue(Integer.class));
                        childUpdates.put("/jugadores/" + killerId + "/active", "WINNER");
                        childUpdates.put("/jugadores/" + jugadorId + "/ranking", dataSnapshot.getValue(Integer.class)+1);
                    }
                    else{
                        childUpdates.put("/jugadores/" + jugadorId + "/ranking", dataSnapshot.getValue(Integer.class)+1);
                    }
                    root.updateChildren(childUpdates);
                // Transaction completed
                Log.d("transacción", "postTransaction:onComplete:" + databaseError);
            }
        });
    }

}
