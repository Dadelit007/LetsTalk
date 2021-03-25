package com.example.letstalk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    public ImageView Image;
    public TextView Username,Useremail;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mRef;
    
   // StorageReference storageReference;
//    String storagePath="Users_Profile_Images/";

    FloatingActionButton fab;
    ProgressDialog pd;

//    Uri image_uri;
    
    String ProfilePhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setupUIView();



        firebaseAuth = FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        mRef=firebaseDatabase.getReference("Users");

        Query query = mRef.orderByChild("email").equalTo(user.getEmail());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                    String name = "" + ds.child("name").getValue();
                    String email = "" + ds.child("email").getValue();
//                    String image=""+ds.child("image").getValue();

                    Useremail.setText(email);
                    Username.setText(name);
                  //  Picasso.get().load(image).into(Image);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditProfileDialog();
            }
        });
    }

    private void setupUIView(){
        Username = findViewById(R.id.username);
        Useremail = findViewById(R.id.email);
        Image=findViewById(R.id.pic);
        fab=findViewById(R.id.edit);
        pd=new ProgressDialog(ProfileActivity.this);
    }

    private void showEditProfileDialog()
    {
        String options[]={"Edit Profile Picture","Edit name"};

        AlertDialog.Builder builder= new AlertDialog.Builder(ProfileActivity.this);

        builder.setTitle("Choose Action");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0)
                {
                    pd.setMessage("Update Profile Picture");
                    ProfilePhoto="image";
                    Toast.makeText(ProfileActivity.this, "Use Edit Name for change Name", Toast.LENGTH_LONG).show();
                  //  showImagePicDialog();

                }
                else if(i==1){

                    pd.setMessage("Update Name");
                    showNameUpdateDialog("name");
                }
            }
        });
        builder.create().show();
    }
//
    private void showNameUpdateDialog(final String key)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Update "+key);

        LinearLayout linearLayout =new LinearLayout(ProfileActivity.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);

        final EditText editText =new EditText(ProfileActivity.this);
        editText.setHint("Enter "+key);
        linearLayout.addView(editText);

        builder.setView(linearLayout);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String values=editText.getText().toString().trim();

                if(!TextUtils.isEmpty(values)){
                    pd.show();
                    HashMap<String,Object> result=new HashMap<>();
                    result.put(key,values);

                    mRef.child(user.getUid()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();

//                            startActivity(new Intent(ProfileActivity.this, StartActivity.class));
                            Toast.makeText(ProfileActivity.this, "Updated...", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(getIntent());
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                }
                            });
                }
                else {
                    Toast.makeText(ProfileActivity.this, "Please enter"+key, Toast.LENGTH_SHORT).show();
                }

            }

        });

        builder.create().show();
    }
}
