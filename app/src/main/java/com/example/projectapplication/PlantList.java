package com.example.projectapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class PlantList extends AppCompatActivity {
    private Uri imageUri;
    private static final int IMAGE_CAPTURE_CODE = 100;
    private static final int IMAGE_PICK_CODE = 101; // Adding this line for gallery
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 102;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_list);


        FirebaseApp.initializeApp(PlantList.this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FloatingActionButton add = findViewById(R.id.addplant);
        Uri imageUri;



        add.setOnClickListener(view -> openCamera());


        TextView empty = findViewById(R.id.empty);
        RecyclerView recyclerView = findViewById(R.id.recycler);
        PlantAdapter adapter = new PlantAdapter(PlantList.this,new ArrayList<Plant>());
        recyclerView.setAdapter(adapter);


        database.getReference().child("plants").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Plant> arraylist = new ArrayList<>();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Plant plant = dataSnapshot.getValue(Plant.class);
                    Objects.requireNonNull(plant).setKey(dataSnapshot.getKey());
                    arraylist.add(plant);
                }
                adapter.updateData(arraylist); // Assuming your adapter has a method to update its data


                if(arraylist.isEmpty()){
                    empty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }else {
                    empty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                }
                ((PlantAdapter) adapter).setOnItemClickListener(new PlantAdapter.OnItemClickListener() {
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
                        //(Integer.parseInt(etwater.getText().toString()));
                        etwater.setText(Integer.parseInt(String.valueOf(plant.getWateramount())));
                        Log.e("XXXXXXX", "before new progressDialog");


                        ProgressDialog progressDialog = new ProgressDialog(PlantList.this);


                        Log.e("XXXXXXX", "before Alert dialog builder");

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

                                            Log.e("XXXXXXX", "before progress dialog");

                                            ProgressDialog dialog = new ProgressDialog(PlantList.this);
                                            progressDialog.setMessage("saving...");
                                            progressDialog.show();

                                            Log.e("XXXXXXX", "here");
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
    private void schedulePlantNotification(Plant plant) {
        Calendar calendar = parseTimeToCalendar(plant.getTime());
        if (calendar != null) {
            Intent alarmIntent = new Intent(this, AlarmReceiver.class);
            alarmIntent.putExtra("PLANT_NAME", plant.getName());
            alarmIntent.putExtra("PLANT_PLACE", plant.getPlace());
            alarmIntent.putExtra("PLANT_IMAGE_URL", plant.getImageUrl());

            PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(
                    this, plant.getKey().hashCode(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmPendingIntent);
        }
    }

    // Method to convert time string to Calendar
    public static Calendar parseTimeToCalendar(String time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.US);
        try {
            Date date = format.parse(time);
            Calendar calendar = Calendar.getInstance();
            if (date != null) {
                calendar.setTime(date);
                if (calendar.before(Calendar.getInstance())) {
                    calendar.add(Calendar.DATE, 1);
                }
            }
            return calendar;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("CameraResult", "Request code: " + requestCode + ", Result code: " + resultCode);


            showAddPlantDialog(imageUri.toString());

    }
    private void showAddPlantDialog(String imageUri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.add_plant_dialog, null);
        TextInputEditText etName = dialogView.findViewById(R.id.et_name);
        TextInputEditText etPlace = dialogView.findViewById(R.id.et_place);
        TextInputEditText etTime = dialogView.findViewById(R.id.et_time);
        TextInputEditText etWaterAmount = dialogView.findViewById(R.id.et_wateramount);
        builder.setView(dialogView)
                .setPositiveButton("Add", (dialogInterface, i) -> {
                    String name = etName.getText().toString();
                    String place = etPlace.getText().toString();
                    String time = etTime.getText().toString();
                    int waterAmount = Integer.parseInt(etWaterAmount.getText().toString());

                    Plant plant = new Plant();
                    plant.setName(name);
                    plant.setPlace(place);
                    plant.setTime(time);
                    plant.setWateramount(waterAmount);
                    plant.setImageUrl(imageUri);

                    FirebaseDatabase.getInstance().getReference().child("plants").push().setValue(plant);
                    dialogInterface.dismiss();
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel())
                .show();
    }

    private void openCamera() {
        // Check both CAMERA and WRITE_EXTERNAL_STORAGE permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Request the CAMERA and WRITE_EXTERNAL_STORAGE permissions
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
            // Permission has already been granted, open the camera
            launchCamera();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
                launchCamera();
            } else {
                // Permission denied
                Toast.makeText(this, "Camera permission is necessary", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void launchCamera() {
        File imagePath = new File(getExternalFilesDir(null), "Images");
        if (!imagePath.exists()) imagePath.mkdirs();
        String imageName = "plant_" + System.currentTimeMillis() + ".jpg";
        File newFile = new File(imagePath, imageName);
        imageUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", newFile);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }

}