package com.pau.enrech.zanahoriaasesina;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TargetActivity extends AppCompatActivity {

    private ConstraintLayout frameWinner,frameTarget;
    private TextView nameSurname;
    private TextView edad;
    private TextView penya;
    private TextView winRank;
    private String jugadorId = "jugador2";
    private ImageView img;
    private User user;
    private User target;
    private Button eliminate_btn;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);
        // Write a message to the database
        database = FirebaseDatabase.getInstance();
        DatabaseReference jugadores = database.getReference("jugadores/"+jugadorId);
        // Read from the database
        jugadores.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User value = dataSnapshot.getValue(User.class);
                Log.d("datos usuario", "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("error datos usuario", "Failed to read value.", error.toException());
            }
        });



        frameTarget = findViewById(R.id.frame_target);
        frameWinner = findViewById(R.id.frame_winner);
        winRank = findViewById(R.id.win_rank);
        eliminate_btn = findViewById(R.id.target_eliminate);
        img = findViewById(R.id.target_photo);
        nameSurname = findViewById(R.id.target_nameSurname);
        edad = findViewById(R.id.target_age);
        penya = findViewById(R.id.target_penya);

        frameWinner.setVisibility(View.GONE);
        frameTarget.setVisibility(View.VISIBLE);

        user = UserDatabase.getUserFromId("Pau");
        target = UserDatabase.getUserFromId(user.target);

        nameSurname.setText(String.format("%s %s",target.nom,target.cognom));
        edad.setText(String.format("%d",target.age));
        penya.setText(String.format("%s",target.penya));


    }

    public void eliminateUser(View view){
        target = UserDatabase.eliminateUser(user, target);
        if (target == null) {
            winRank.setText(String.format("%d / %d",user.ranking,UserDatabase.getNumUsers()));
            frameWinner.setVisibility(View.VISIBLE);
            frameTarget.setVisibility(View.GONE);
            eliminate_btn.setEnabled(false);
        }
        else {
            nameSurname.setText(String.format("%s %s", target.nom, target.cognom));
            edad.setText(String.format("%d", target.age));
            penya.setText(String.format("%s", target.penya));
        }
    }

}
