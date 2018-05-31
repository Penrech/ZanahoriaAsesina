package com.pau.enrech.zanahoriaasesina;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;

public class TargetActivity extends AppCompatActivity {

    private ConstraintLayout frameWinner,frameTarget,frameEliminated,frameLimbo,frameLoading, frameOver;
    private TextView nameSurname;
    private TextView edad;
    private TextView penya;
    private TextView winRank;
    private TextView loseRank;
    private TextView overTextWinner, overTextRanking, overTextInfo;
    private String jugadorId = "jugador3";
    private String targetId;
    private String killerId;
    private ImageView img;
    private User user;
    private User target;
    private User killer;
    private Button eliminate_btn;
    private FirebaseDatabase database;
    private DatabaseReference root;
    private DatabaseReference targetData;
    private DatabaseReference jugadores;
    private DatabaseReference juego;
    private DatabaseReference winner;
    private int contador;
    private Game.gStates gameState;
    private Game.gStates prevGameState;
    private Game gameData;
    private int activos;
    private ValueEventListener usersListener;
    private ValueEventListener targetsListener;
    private ValueEventListener winnerListener;
    private Query getKiller;

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);
        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        root = database.getReference();
        jugadores = database.getReference("jugadores/"+jugadorId);
        juego = database.getReference("juego");


        frameTarget = findViewById(R.id.frame_target);
        frameWinner = findViewById(R.id.frame_winner);
        frameEliminated = findViewById(R.id.frame_loser);
        frameLimbo = findViewById(R.id.confirm_elimination);
        frameLoading = findViewById(R.id.frame_loading);
        frameOver = findViewById(R.id.frame_endGame);

        loseRank = findViewById(R.id.lose_rank);
        winRank = findViewById(R.id.win_rank);
        eliminate_btn = findViewById(R.id.target_eliminate);
        img = findViewById(R.id.target_photo);
        nameSurname = findViewById(R.id.target_name);
        edad = findViewById(R.id.target_age);
        penya = findViewById(R.id.target_penya);
        overTextWinner = findViewById(R.id.endGameWinner);
        overTextRanking = findViewById(R.id.endGamePosition);
        overTextInfo = findViewById(R.id.endGameText);


        juego.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                contador = dataSnapshot.child("totales").getValue(Integer.class);
                activos = dataSnapshot.child("activos").getValue(Integer.class);
                Game Value = dataSnapshot.getValue(Game.class);
                gameData = Value;
                gameState = Value.estado;
                if(prevGameState == null){
                    prevGameState = gameState;
                    initialListeners();
                }
                else if (prevGameState != gameState){
                    prevGameState = gameState;
                    checkGameState();
                }
                Log.d("PrevGameState", "prevGameState: "+prevGameState);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("error datos contador", "Failed to read value.", error.toException());
            }
        });


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
            case R.id.statics_menu_item:
                goTo(StatisticsActivity.class);
                break;
        }
        return true;
    }

    private  void goTo(Class activity){
        Intent intent = new Intent(this,activity);
        startActivity(intent);
    }


    public void initialListeners(){
        // Read players
        usersListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                frameLoading.setVisibility(View.GONE);
                User value = dataSnapshot.getValue(User.class);
                user = value;
                checkGameState();

            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("error datos usuario", "Failed to read value.", error.toException());
            }
        };
        jugadores.addValueEventListener(usersListener);

    }



    public void checkUserState(){
        if (user.active == user.active.ACTIVE)
        {
            showFrame(frameTarget);
            targetId = user.target;
            targetData = database.getReference("jugadores/"+targetId);
            updateData();
            Log.d("Entrada a user active", "valor de user "+user.nom);
        }
        if (user.active == user.active.ELIMINATING){
            showFrame(frameLoading);
            Log.d("Entrada a eliminating", "valor de user "+user.nom);
        }
        if(user.active == user.active.LIMBO){
            showFrame(frameLimbo);
            Log.d("Entrada a limbo", "valor de user "+user.nom);

        }
        if(user.active == user.active.ELIMINATED){
            loseRank.setText(String.format("Has quedado el %d de %d jugadores",user.ranking,contador));
            showFrame(frameEliminated);
            Log.d("Entrada a eliminated", "valor de user "+user.nom);

        }

    }

    public void checkGameState(){
        if (gameState == Game.gStates.ACTIVE){
            checkUserState();
        }
        else if(gameState == Game.gStates.OVER){
            if(targetData != null) {
                targetData.removeEventListener(targetsListener);
            }
            overTextInfo.setText("El juego ha finalizado");
            Query checkWinner = root.child("jugadores").orderByChild("active").equalTo("WINNER");
            checkWinner.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() != null){
                        User ganador = null;
                        for (DataSnapshot jugador: dataSnapshot.getChildren()){
                            ganador = jugador.getValue(User.class);
                        }
                        if(user.active == ganador.active){
                            overTextWinner.setText("¡Enhorabuena!, eres el ganador");
                            overTextRanking.setText("Has sido el primero de "+gameData.totales+" jugadores");
                        }
                        else{
                            overTextWinner.setText("El ganador es "+ganador.nom+" "+ganador.cognom);
                            overTextRanking.setText("Has sido eliminado y has quedado el "+user.ranking+" de "+gameData.totales+" jugadores");
                        }
                    }
                    else{
                        overTextWinner.setText("Sin un ganador");
                        if(user.ranking == -1){
                            overTextRanking.setText("No has llegado a ser eliminado, quedando entre los "+gameData.activos+" primeros");
                        }
                        else{
                            overTextRanking.setText("Has sido eliminado y has quedado el "+user.ranking+" de "+gameData.totales+" jugadores");
                        }
                    }
                    showFrame(frameOver);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("error datos winner", "Failed to read value.", databaseError.toException());
                }
            });
        }
        else{
            overTextInfo.setText("El juego se encuentra inactivo actualmente");
            overTextRanking.setText("");
            overTextWinner.setText("");
            showFrame(frameOver);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (jugadores != null){
        jugadores.removeEventListener(usersListener);}
        if (targetData != null){
            targetData.removeEventListener(targetsListener);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
       // initialListeners();
    }

    public void updateData(){
        targetsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User value = dataSnapshot.getValue(User.class);
                target = value;
                if (target.active == target.active.ELIMINATING){
                    showFrame(frameLoading);
                }
                else{
                    nameSurname.setText(String.format("%s %s",target.nom,target.cognom));
                    edad.setText(String.format("%d",target.age));
                    penya.setText(String.format("%s",target.penya));
                    loadImageFromUrl(target.img,img);
                }

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
    public void startReport(View view){
        Intent intent = new Intent(this,ReportActivity.class);
        intent.putExtra("nombreAReportar",target.getNomAp());
        intent.putExtra("nombreReportador",user.getNomAp());
        intent.putExtra("idAReportar",targetId);
        intent.putExtra("idUser",jugadorId);
        startActivity(intent);
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
                root.child("jugadores").child(jugadorId).child("active").setValue("ELIMINATING");
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

    public void showFrame(ConstraintLayout frame){
        ConstraintLayout[] frames = new ConstraintLayout[]{frameTarget,frameEliminated,frameLoading,frameLimbo,frameWinner,frameOver};
        for (ConstraintLayout layout : frames){
            if(layout.getId() == frame.getId()){
                layout.setVisibility(View.VISIBLE);
            }
            else{
               layout.setVisibility(View.GONE);
            }
            Log.d(frame.getId()+"-"+layout.getId(), layout+" visibility: "+layout.getVisibility());
        }
    }

    public void loadImageFromUrl(String url,ImageView image){
        new DownloadImageTask(image)
                .execute(url);
    }


}
