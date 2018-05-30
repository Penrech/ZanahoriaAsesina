package com.pau.enrech.adminapp;

import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GameActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference root;
    private DatabaseReference juego;
    private Button sRegister, sGame, sReset;
    private Game datosJuego;
    private TextView gameStateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        database = FirebaseDatabase.getInstance();
        root = database.getReference();
        juego = database.getReference("juego");
        sRegister = findViewById(R.id.btn_StartRegistration);
        sGame = findViewById(R.id.btn_StartGame);
        sReset = findViewById(R.id.btn_ReStartGameData);
        gameStateText = findViewById(R.id.gameStateText);

        juego.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               Game value = dataSnapshot.getValue(Game.class);
               datosJuego = value;
               switch (datosJuego.estado){
                   case INACTIVE:
                       gameStateText.setText("Estado: Inactivo");
                       sGame.setText("Iniciar juego");
                       sGame.setEnabled(true);
                       sRegister.setText("Iniciar registro");
                       break;
                   case REGISTRATION:
                       gameStateText.setText("Estado: Registro activo...");
                       sRegister.setText("Pausar registro");
                       break;
                   case ACTIVATING:
                       gameStateText.setText("Estado: Iniciando juego...");
                       sGame.setEnabled(false);
                       sGame.setText("Iniciando...");
                       break;
                   case ACTIVE:
                       gameStateText.setText("Estado: Juego activo");
                       sGame.setEnabled(true);
                       sGame.setText("Finalizar juego");
                       break;
                   case OVER:
                       gameStateText.setText("Estado: Juego finalizado");
                       sGame.setText("Iniciar juego");
                       sGame.setEnabled(true);
                       sRegister.setText("Iniciar registro");
                       break;
                   case REESTABLISHING:
                       gameStateText.setText("Estado: Restableciendo los datos del juego");
                       break;
                   case REGISTRATING:
                       gameStateText.setText("Estado: Iniciando registro...");
                       break;

               }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("error datos estado", "Failed to read value.", databaseError.toException());
            }
        });


    }

    public void startRegister(View view){
        if (datosJuego != null){
            switch (datosJuego.estado){
                case INACTIVE:
                    popDialog("Vas a iniciar el periodo de registros. ¿Continuar","" +
                            "Iniciar registros",juego,"REGISTRATION");
                    break;
                case REGISTRATION:
                    if (datosJuego.totales < 3){
                        popDialog("Se han registro menos de 3 jugadores y estas apunto " +
                                "de finalizar el periodo de registros. ¿Continuar?","Finalizar " +
                                "registros",juego,"INACTIVE");
                    }
                    else{
                        popDialog("Vas a finalizar el periodo de registros. ¿Continuar?","" +
                            "Finalizar registros",juego,"INACTIVE");
                    }
                    break;
                case ACTIVE:
                    popDialog("No puedes iniciar el periodo de registro mientras el juego está" +
                            " activo","Iniciar registro",juego,null);
                    break;
                case OVER:
                    popDialog("El juego ha finalizado y debe reiniciarse para permitir esta acción","Juego finalizado",juego,null);
                    break;
                default:
                    popDialog("No puedes realizar esta acción en este momento","Iniciar registro",juego,null);
                break;
            }
        }

    }
    public void startGame(View view){
        if (datosJuego != null){
            switch (datosJuego.estado){
                case INACTIVE:
                    if (datosJuego.totales < 3){
                        popDialog("No puedes iniciar el juego ya que hay menos de 3 jugadores","" +
                                "Iniciar juego",juego,null);
                    }
                    else{
                    popDialog("Vas a iniciar el juego, una vez iniciado no podrá ser pausado," +
                            " solo podra ser finalizado. ¿Continuar?","" +
                            "Iniciar juego",juego,"ACTIVATING");
                    }
                    break;
                case REGISTRATION:
                    popDialog("El periodo de registros está activado, debes finalizarlo antes " +
                            "de poder iniciar el juego","" +
                            "Iniciar juego",juego,null);
                    break;
                case ACTIVE:
                    popDialog("Estas apunto de finalizar el juego, una vez finalizado no " +
                            "podrá ser reanudado. ¿Continuar?","Finalizar juego",juego,"OVER");
                    break;
                case OVER:
                    popDialog("El juego ha finalizado y debe reiniciarse para permitir esta acción","Juego finalizado",juego,null);
                    break;
                default:
                    popDialog("No puedes realizar esta acción en este momento","Iniciar registro",juego,null);
                    break;
            }
        }
    }
    public void restartData(View view){
        if (datosJuego != null){
            switch (datosJuego.estado){
                case OVER:
                    popDialog("Estas a punto de restablecer todos los datos del juego, ¿Continuar?","Resetear juego",juego,"REESTABLISHING");
                break;
                case ACTIVE:
                    popDialog("Debes finalizar el juego antes de poder realizar esta acción","Resetear juego",juego,null);
                    break;
                default:
                    popDialog("No puedes realizar esta acción en este momento","Resetear juego",juego,null);
                break;
            }
        }
    }

    public void popDialog(String message, String Title, final DatabaseReference ref, final String newVal){
        String msg = message;
        String title = Title;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        if (newVal != null) {
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                        ref.child("estado").setValue(newVal);
                }
            });
            builder.setNegativeButton(android.R.string.no, null);
        }
        else{
            builder.setPositiveButton(android.R.string.ok,null);
        }
        builder.create().show();
    }
}

