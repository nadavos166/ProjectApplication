package com.example.projectapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class PlantList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_list);
        FirebaseApp.initializeApp(PlantList.this);
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        FloatingActionButton add = findViewById(R.id.addplant);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View view1 = LayoutInflater.from(PlantList.this).inflate(R.layout.add_plant_dialog,null);
                TextInputLayout namelayout,placelayout,timelayout,waterlayout;
                namelayout=view1.findViewById(R.id.namelayout);
                placelayout=view1.findViewById(R.id.placelayout);
                timelayout=view1.findViewById(R.id.timelayout);
                waterlayout=view1.findViewById(R.id.wateramountlayout);
                TextInputEditText etname,etplace,ettime,etwater;
                etname=view1.findViewById(R.id.et_name);
                etplace=view1.findViewById(R.id.et_place);
                ettime=view1.findViewById(R.id.et_time);
                etwater=view1.findViewById(R.id.et_wateramount);
                AlertDialog alertDialog =  new AlertDialog.Builder(PlantList.this)
                      .setTitle("add")
                      .setView(view1)
                        .setPositiveButton("add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(Objects.requireNonNull(etname.getText()).toString().isEmpty()){
                                    namelayout.setError("this field is requiered!");
                                }
                                else if(Objects.requireNonNull(ettime.getText()).toString().isEmpty()){
                                    timelayout.setError("this field is required");
                                }
                                else{
                                    ProgressDialog dialog = new ProgressDialog(PlantList.this);
                                    dialog.setMessage("storing in database");
                                    dialog.show();
                                    Plant plant = new Plant();
                                    plant.setName(etname.getText().toString());
                                    plant.setPlace(etplace.getText().toString());
                                    plant.setTime(ettime.getText().toString());
                                    plant.setWateramount(Integer.parseInt(etwater.getText().toString()));
                                    database.getReference().child("plants").push().setValue(plant).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            dialog.dismiss();
                                            dialogInterface.dismiss();
                                            Toast.makeText(PlantList.this,"saved succefully",Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            dialog.dismiss();
                                            Toast.makeText(PlantList.this,"save failed",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create();
                alertDialog.show();
            }
        });
        TextView empty = findViewById(R.id.empty);
        RecyclerView recyclerView = findViewById(R.id.recycler);
        database.getReference().child("plants").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Plant> arraylist = new ArrayList<>();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Plant plant = dataSnapshot.getValue(Plant.class);
                    Objects.requireNonNull(plant).setKey(dataSnapshot.getKey());
                    arraylist.add(plant);

                }
                if(arraylist.isEmpty()){
                    empty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }else {
                    empty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                }
                PlantAdapter adapter= new PlantAdapter(PlantList.this,arraylist);
                recyclerView.setAdapter(adapter);
                adapter.setOnItemClickListener(new PlantAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Plant plant) {
                        View view = LayoutInflater.from(PlantList.this).inflate(R.layout.add_plant_dialog,null);
                        TextInputLayout  namelayout,placelayout,timelayout,waterlayout;
                        TextInputEditText etname,etplace,ettime,etwater;
                        etname = view.findViewById(R.id.et_name);
                        etplace=view.findViewById(R.id.et_place);
                        ettime=view.findViewById(R.id.et_time);
                        etwater=view.findViewById(R.id.et_wateramount);
                        namelayout=view.findViewById(R.id.namelayout);
                        placelayout=view.findViewById(R.id.placelayout);
                        timelayout=view.findViewById(R.id.timelayout);
                        waterlayout=view.findViewById(R.id.wateramountlayout);
                        etname.setText(plant.getName());
                        etplace.setText(plant.getPlace());
                        ettime.setText(plant.getTime());
                        etwater.setText(plant.getWateramount());
                        ProgressDialog progressDialog = new ProgressDialog(PlantList.this);
                        AlertDialog alertDialog = new AlertDialog.Builder(PlantList.this)
                                .setTitle("edit")
                                .setView(view)
                                .setPositiveButton("save", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if(Objects.requireNonNull(etname.getText()).toString().isEmpty()){
                                            namelayout.setError("this field is requiered!");
                                        }
                                        else if(Objects.requireNonNull(ettime.getText()).toString().isEmpty()){
                                            timelayout.setError("this field is required");
                                        }
                                        else{
                                            ProgressDialog dialog = new ProgressDialog(PlantList.this);
                                            progressDialog.setMessage("saving...");
                                            progressDialog.show();
                                            Plant plant = new Plant();
                                            plant.setName(etname.getText().toString());
                                            plant.setPlace(etplace.getText().toString());
                                            plant.setTime(ettime.getText().toString());
                                            plant.setWateramount(Integer.parseInt(etwater.getText().toString()));
                                            database.getReference().child("plants").child(plant.getKey()).setValue(plant).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    progressDialog.dismiss();
                                                    dialogInterface.dismiss();
                                                    Toast.makeText(PlantList.this,"saved succefully",Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(PlantList.this,"save failed",Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }

                                    }
                                })
                                .setNeutralButton("close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .setNegativeButton("delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        progressDialog.setTitle("Deleting");
                                        progressDialog.show();
                                        database.getReference().child("plants").child(plant.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Toast.makeText(PlantList.this,"Deleted Succefully",Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        progressDialog.dismiss();
                                                    }
                                                });
                                    }
                                }).create();
                        alertDialog.show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}