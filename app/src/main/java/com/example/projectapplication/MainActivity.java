package com.example.projectapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class MainActivity extends AppCompatActivity
{
    FirebaseAuth auth;
    GoogleSignInClient googleSignInClient;
    ShapeableImageView iamgeview;
    TextView name,mail;
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>()
    {
        @Override
        public void onActivityResult(ActivityResult result)
        {
            Log.e("XXXXXXXXXXX", "+onActivityResult");
            if(result.getResultCode() == RESULT_OK){
                Log.e("XXXXXXXXXXX", "+RESULT_OK");
                Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try
                {
                    GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(),null);
                    Log.e("XXXXXXXXXXX", "+authCredential");
                    Log.e("XXXXXXXXXXX", authCredential.toString());
                    auth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            Log.e("XXXXXXXXXXX", "+onComplete");
                            if(task.isSuccessful()){
                                Log.e("XXXXXXXXXXX", "+isSuccessful");
                                auth = FirebaseAuth.getInstance();
                                Glide.with(MainActivity.this).load(Objects.requireNonNull(auth.getCurrentUser()).getPhotoUrl()).into(iamgeview);
                                name.setText(auth.getCurrentUser().getDisplayName());
                                mail.setText(auth.getCurrentUser().getEmail());
                                Toast.makeText(MainActivity.this,"signed in succefully",Toast.LENGTH_SHORT).show();
                            } else
                            {
                                Log.e("XXXXXXXXXXX", "+failed");
                                Toast.makeText(MainActivity.this,"signed in failed" + task.getException(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (ApiException e)
                {
                    e.printStackTrace();
                }
            }else
            {
                Log.e("XXXXXXXXXXX",result.toString());
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        iamgeview = findViewById(R.id.profileImage);
        name = findViewById(R.id.nametv);
        mail = findViewById(R.id.mailtv);
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();
        googleSignInClient= GoogleSignIn.getClient(MainActivity.this,options);
        auth = FirebaseAuth.getInstance();
        SignInButton signInButton = findViewById(R.id.signIn);
        signInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent intent = googleSignInClient.getSignInIntent();
                activityResultLauncher.launch(intent);
            }
        });
        Button plantlistbutton;
        plantlistbutton =findViewById(R.id.btn_gotoplantlist);
        MaterialButton signout = findViewById(R.id.signOut);
        signout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener()
                {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
                    {
                        if(firebaseAuth.getCurrentUser()==null)
                        {
                            googleSignInClient.signOut().addOnSuccessListener(new OnSuccessListener<Void>()
                            {
                                @Override
                                public void onSuccess(Void unused)
                                {
                                    Toast.makeText(MainActivity.this,"signed out succefully",Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(MainActivity.this,MainActivity.class));
                                }
                            });
                        }
                    }
                });
                FirebaseAuth.getInstance().signOut();
            }
        });
        plantlistbutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(auth.getCurrentUser() != null)
                {
                    Intent intent = new Intent(MainActivity.this, PlantList.class);
                    startActivity(intent);
                }
            }
        });
        if(auth.getCurrentUser() != null)
        {
            Glide.with(MainActivity.this).load(Objects.requireNonNull(auth.getCurrentUser()).getPhotoUrl()).into(iamgeview);
            name.setText(auth.getCurrentUser().getDisplayName());
            mail.setText(auth.getCurrentUser().getEmail());
        }
    }


}