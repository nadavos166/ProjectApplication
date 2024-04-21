package com.example.projectapplication;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class AddPlantDialog extends DialogFragment{

    TextInputEditText etName ;
    TextInputEditText etPlace ;
    TextInputEditText etTime ;
    TextInputEditText etWaterAmount ;
    TextInputEditText dateEt;
    private Calendar selectedDate;

    private Uri imageUri;

    private Plant plant;

    public AddPlantDialog(Uri imageUri, Plant plant){
        this.imageUri = imageUri;
        this.plant = plant;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getContext()).inflate(R.layout.add_plant_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
         etName = view.findViewById(R.id.et_name);
         etPlace = view.findViewById(R.id.et_place);
         etTime = view.findViewById(R.id.et_time);
         etWaterAmount = view.findViewById(R.id.et_wateramount);
         dateEt = view.findViewById(R.id.dateEt);
        Button saveBT = view.findViewById(R.id.save);
        saveBT.setOnClickListener(v -> savePlant());
        dateEt.setOnClickListener(v->openDatePicker());
        selectedDate = Calendar.getInstance();
        if (plant != null){
            etName.setText(plant.getName());
            etPlace.setText(plant.getPlace());
            etTime.setText(plant.getTime());
            etWaterAmount.setText(String.valueOf(plant.getWateramount()));
            dateEt.setText(Long.toString(plant.getDate()));
        }
    }

    private void savePlant() {
        new AlertDialog.Builder(getActivity())
                .setPositiveButton("Add", (dialogInterface, i) -> {
                    String name = etName.getText().toString();
                    String place = etPlace.getText().toString();
                    String time = etTime.getText().toString();
                    int waterAmount = Integer.parseInt(etWaterAmount.getText().toString());

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        String userId = user.getUid();
                        DatabaseReference userPlantsRef = FirebaseDatabase.getInstance().getReference()
                                .child("users").child(userId).child("plants");
                        String plantId = userPlantsRef.push().getKey(); // Create a new plant ID
                        Plant plant = new Plant(name, place, selectedDate.getTimeInMillis(), time, waterAmount, ""); // Assuming you have a constructor and handling image separately
                        if (imageUri != null) {
                            plant.setImageUrl(imageUri.toString());
                            imageUri = null; // Reset imageUri after use
                        } else {
                            plant.setImageUrl(""); // Set a default or placeholder image URL
                        }

                        userPlantsRef.child(plantId).setValue(plant)
                                .addOnSuccessListener(aVoid -> {
                                    registerToAlert(plant);
                                    Toast.makeText(getActivity(), "Plant added successfully", Toast.LENGTH_SHORT).show();
                                    dismiss();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getActivity(), "Failed to add plant", Toast.LENGTH_SHORT).show();
                                });
                    }

                    dialogInterface.dismiss();
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                    imageUri = null; // Reset imageUri if cancelled
                    dialogInterface.cancel();
                }).show();
    }

    private void registerToAlert(Plant plant) {
        Calendar calendar = plant.parseDateToCalendar();
        if (calendar != null) {
            Intent alarmIntent = new Intent(getActivity(), AlarmReceiver.class);
            alarmIntent.putExtra("PLANT_NAME", plant.getName());
            alarmIntent.putExtra("PLANT_PLACE", plant.getPlace());
            alarmIntent.putExtra("PLANT_IMAGE_URL", plant.getImageUrl());

            PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(
                    getActivity(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);

            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

            try {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmPendingIntent);

            }catch (Exception e){

            }
        }
    }

    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                selectedDate.clear();
                selectedDate.set(i, i1, i2);
                dateEt.setText(i+"/"+i1+"/"+i2);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.MATCH_PARENT);

    }
}
