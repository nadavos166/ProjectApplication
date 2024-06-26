package com.example.projectapplication;
import static android.Manifest.permission.POST_NOTIFICATIONS;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.view.ViewGroup;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
public class PlantList extends AppCompatActivity
{
    private Uri imageUri;
    private static final int IMAGE_CAPTURE_CODE = 100;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 102;
    private FirebaseAuth auth;  // Added Firebase Auth for user-specific data
    private DatabaseReference userPlantsRef;  // Added to keep reference to user's plants
    private ArrayList<Plant> arraylist;
    private FirebaseDatabase database;
    private FirebaseUser user;
    private ActivityResultLauncher<String> notificationLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted ->
            {
        if (!isGranted)
        {
            Toast.makeText(PlantList.this, getString(R.string.no_permission), Toast.LENGTH_SHORT).show();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_list);
        notificationLauncher.launch(POST_NOTIFICATIONS);
        FirebaseApp.initializeApp(PlantList.this);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();  // Initialize FirebaseAuth instance
        user = auth.getCurrentUser();  // Get current logged in user
        FloatingActionButton add = findViewById(R.id.addplant);
        TextView empty = findViewById(R.id.empty);
        RecyclerView recyclerView = findViewById(R.id.recycler);
        PlantAdapter adapter = new PlantAdapter(PlantList.this, new ArrayList<Plant>());
        adapter.setOnItemClickListener(new PlantAdapter.OnItemClickListener()
        {
            @Override
            public void onClick(int position, Plant plant)
            {
                showAddPlantDialog(plant);
            }
        });
        adapter.setDeleteClickListener(new PlantAdapter.OnItemClickListener()
        {
            @Override
            public void onClick(int position, Plant plant)
            {
                deleteItem(position, plant);
            }
        });
        recyclerView.setAdapter(adapter);
        if (user != null)
        {
            userPlantsRef = database.getReference().child("users").child(user.getUid()).child("plants");  // Change to user-specific path
            userPlantsRef.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                     arraylist = new ArrayList<Plant>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Plant plant = dataSnapshot.getValue(Plant.class);
                        Objects.requireNonNull(plant).setKey(dataSnapshot.getKey());
                        arraylist.add(plant);
                    }
                    adapter.updateData(arraylist);
                    if (arraylist.isEmpty())
                    {
                        empty.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        empty.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(PlantList.this, "Failed to load plants", Toast.LENGTH_SHORT).show();
                }
            });
        }

        add.setOnClickListener(view -> openCamera());
    }

    private void deleteItem(int position, Plant plant) {
        arraylist.remove(position);
        database.getReference().child("users").child(user.getUid()).child("plants")
                .child(plant.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(PlantList.this, getString(R.string.remove_item), Toast.LENGTH_SHORT).show();
                    }
                });  // Change to user-specific path
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
            try{
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmPendingIntent);
            }catch (SecurityException e){}
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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CAPTURE_CODE && resultCode == RESULT_OK) {
            // imageUri is already set in the launchCamera method
            showAddPlantDialog(null);
        } else {
            imageUri = null; // Reset or handle the case where image capture failed
        }
    }

    private void showAddPlantDialog(Plant selectedPlant) {
        new AddPlantDialog(imageUri, selectedPlant).show(getSupportFragmentManager(), "");
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