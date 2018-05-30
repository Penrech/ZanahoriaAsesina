package com.pau.enrech.adminapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ReportListActivity extends AppCompatActivity {

    private FirebaseRecyclerAdapter<Report, ReportListActivity.ReportViewHolder> adapter;
    private Query queryRef;
    private RecyclerView recyclerview;
    private FirebaseDatabase database;
    private DatabaseReference root;

    public class ReportViewHolder extends RecyclerView.ViewHolder {
        public TextView report_fecha, report_hora, report_message;
        View mView;

        ReportViewHolder(View root) {
            super(root);
            mView = root;
            report_fecha = root.findViewById(R.id.ReportDia);
            report_hora = root.findViewById(R.id.ReportHora);
            report_message  = root.findViewById(R.id.ReportTextItem);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);

        database = FirebaseDatabase.getInstance();
        root = database.getReference();
        recyclerview = findViewById(R.id.reportRecyclerView);

        queryRef = root.child("reportes").limitToLast(100);

        FirebaseRecyclerOptions<Report> options = new FirebaseRecyclerOptions.Builder<Report>()
                .setQuery(queryRef, Report.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Report, ReportListActivity.ReportViewHolder>(options) {
            @NonNull
            @Override
            public ReportListActivity.ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_item, parent, false);
                return new ReportListActivity.ReportViewHolder(root);

            }
            @Override
            protected void onBindViewHolder(@NonNull ReportListActivity.ReportViewHolder holder, final int position, @NonNull final Report model) {
                holder.report_fecha.setText(model.getDayString());
                holder.report_hora.setText(model.getHourString());
                holder.report_message.setText(model.getReportMessageString());

                    holder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                Intent intent = new Intent(ReportListActivity.this,ReportDetailActivity.class);
                                intent.putExtra("nombresReportes",model.getReportMessageString());
                                intent.putExtra("descripcion",model.message);
                                intent.putExtra("reportadorId",model.ReporterId);
                                intent.putExtra("reportadoId",model.ReportedId);
                                intent.putExtra("reportId",adapter.getRef(position).getKey());
                                startActivity(intent);
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
