package com.pau.enrech.adminapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Constructor;

public class ReportDetailActivity extends AppCompatActivity {

    private TextView textHeader,textMessage,textReporterName,textReportedName,textReporterPhone,textReportedPhone;
    private String  idReportador, idReportado, idReporte;
    private Button btnTextReporter, btnTextReported;
    private ScrollView frameDetails;
    private int actives;
    private ConstraintLayout frameLoading;
    private FirebaseDatabase database;
    private DatabaseReference root, reportador,reportado, activosRef,reporte;
    private User reportadorData, reportadoData;
    private Report reportData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_detail);

        textHeader = findViewById(R.id.reportDetailHeaderText);
        textMessage = findViewById(R.id.reportDetailMessageText);
        textReporterName= findViewById(R.id.reportDetailNomReporter);
        textReportedName = findViewById(R.id.reportDetailNomReported);
        textReporterPhone= findViewById(R.id.reportDetailPhoneReporter);
        textReportedPhone = findViewById(R.id.reportDetailPhoneReported);
        btnTextReporter = findViewById(R.id.reportDetailEliminateReporter);
        btnTextReported = findViewById(R.id.reportDetailEliminateReported);
        frameDetails = findViewById(R.id.reportFrameDetails);
        frameLoading = findViewById(R.id.reportFrameLoading);

        Intent intent = getIntent();
        textHeader.setText(intent.getStringExtra("nombresReportes"));
        textMessage.setText(intent.getStringExtra("descripcion"));
        idReportador = intent.getStringExtra("reportadorId");
        idReportado = intent.getStringExtra("reportadoId");
        idReporte = intent.getStringExtra("reportId");

        database = FirebaseDatabase.getInstance();
        root = database.getReference();
        reportador = root.child("jugadores").child(idReportador);
        reportado = root.child("jugadores").child(idReportado);
        activosRef = root.child("juego").child("activos");
        reporte = root.child("reportes").child(idReporte);

        initFirstListener();
    }

    public void initFirstListener(){
        reportador.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reportadorData = dataSnapshot.getValue(User.class);
                textReporterName.setText(reportadorData.getNomAp());
                textReporterPhone.setText(String.format("%d",reportadorData.phone));
                btnTextReporter.setText("Eliminar "+reportadorData.getNomAp());
                initSecondListener();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void initSecondListener(){
        reportado.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reportadoData = dataSnapshot.getValue(User.class);
                textReportedName.setText(reportadoData.getNomAp());
                textReportedPhone.setText(String.format("%d",reportadoData.phone));
                btnTextReported.setText("Eliminar "+reportadoData.getNomAp());
                initThirdListener();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void initThirdListener(){
        activosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                actives = dataSnapshot.getValue(Integer.class);
                initForthListener();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void initForthListener(){
        reporte.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() !=null){
                    reportData = dataSnapshot.getValue(Report.class);
                }
                else{
                    reportData = null;
                }
                frameLoading.setVisibility(View.GONE);
                frameDetails.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void EliminateReporter(View view){
        String msg = "¿Eliminar a "+reportadorData.getNomAp()+" ?";
        String title = "Eliminar a un jugador";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(reportadorData.active == reportadorData.active.ACTIVE || reportadorData.active == reportadorData.active.LIMBO ){
                    reportador.child("active").setValue("ELIMINATING");
                    root.child("reportes").child(idReporte).setValue(null);
                    finish();
                }
                else{
                    Context context = getApplicationContext();
                    CharSequence text = "Error borrando jugador";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    finish();
                }
            }
        });
        builder.setNegativeButton(android.R.string.no, null);
        builder.create().show();

    }
    public void EliminateReported(View view){
        String msg = "¿Eliminar a "+reportadoData.getNomAp()+" ?";
        String title = "Eliminar a un jugador";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(reportadoData.active == reportadoData.active.ACTIVE || reportadoData.active == reportadoData.active.LIMBO ){
                    reportado.child("active").setValue("ELIMINATING");
                    root.child("reportes").child(idReporte).setValue(null);
                    finish();
                }
                else {
                    Context context = getApplicationContext();
                    CharSequence text = "Error borrando jugador";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    finish();
                }
            }
        });
        builder.setNegativeButton(android.R.string.no, null);
        builder.create().show();

    }
    public void EliminateBoth(View view){
        if(actives !=2){
            String msg = "¿Eliminar a ambos jugadores?";
            String title = "Eliminar a ambos jugadores";
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(title);
            builder.setMessage(msg);
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if((reportadoData.active == reportadoData.active.ACTIVE || reportadoData.active == reportadoData.active.LIMBO )
                            && (reportadoData.active == reportadoData.active.ACTIVE || reportadoData.active == reportadoData.active.LIMBO )){
                        reportador.child("active").setValue("ELIMINATING");
                        reportado.child("active").setValue("ELIMINATING");
                        root.child("reportes").child(idReporte).setValue(null);
                        finish();
                    }
                    else {
                        Context context = getApplicationContext();
                        CharSequence text = "Error borrando jugadores";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                        finish();
                    }
                }
            });
            builder.setNegativeButton(android.R.string.no, null);
            builder.create().show();

        }
        else{
            popDialog("No puedes eliminar a ambos jugadores, ya que son los únicos que quedan activos","Borrar ambos jugadores",null,null);
        }


    }
    public void EliminateReport(View view){
        String msg = "¿Borrar el reporte sin eliminar a ningún jugador?";
        String title = "Borrar reporte";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (reportData == null){
                    finish();
                }else{
                    root.child("reportes").child(idReporte).setValue(null);
                    finish();}
            }
        });
        builder.setNegativeButton(android.R.string.no, null);
        builder.create().show();

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
