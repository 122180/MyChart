package com.example.mychart.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mychart.Login.Login;
import com.example.mychart.R;
import com.example.mychart.changepassword.Change_Password;
import com.example.mychart.common.Common;
import com.example.mychart.common.Contents;
import com.example.mychart.signup.SignUp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class User_Profile extends AppCompatActivity {
    private static final int REQUESTCODE =100;
    private TextInputEditText eName,eEmail;
    private ImageView ivProfile;


    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    private Uri localFileUri,serverFileUri;
    private View progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__profile);

        mFirebaseAuth=FirebaseAuth.getInstance();

        mStorageReference= FirebaseStorage.getInstance().getReference();
        progressbar=findViewById(R.id.progressbarshow);
        eName=findViewById(R.id.name);
        eEmail=findViewById(R.id.email_id);
        ivProfile=findViewById(R.id.imageiews);


    }


    @Override
    protected void onStart() {
        super.onStart();

        mFirebaseUser=mFirebaseAuth.getCurrentUser();
        if(mFirebaseUser!=null)
        {
            eName.setText(mFirebaseUser.getDisplayName());
            eEmail.setText(mFirebaseUser.getEmail());
            serverFileUri=mFirebaseUser.getPhotoUrl();
            if(serverFileUri!=null)
            {

                Glide.with(this)
                        .load(serverFileUri)
                        .placeholder(R.drawable.default_profile)
                        .error(R.drawable.default_profile)
                        .into(ivProfile);
            }


        }


    }

    public void Save(View view) {

        if(eName.getText().toString().trim().equals(""))
        {
            eName.setError("can't blank");
        }
        else if(eEmail.getText().toString().trim().equals(""))
            eEmail.setError("can't empty");
       else
        {
            if(localFileUri!=null) {
                UpdateWithPicture();
            }

            else
                UpdateOnlyName();
        }
    }

    public void Changeprofilepic(View view) {
        if(serverFileUri==null)
        {
            selectImage();
        }
        else {
           final PopupMenu popupMenu=new PopupMenu(this, view);

            getMenuInflater().inflate(R.menu.menu_picture,popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int id=item.getItemId();
                    if (R.id.changepicture==id) {
                        selectImage();


                    }
                    else if(R.id.remove==id)
                    {
                        RemovePhoto();
                    }
                    return false;
                }


            });
            popupMenu.show();


        }

    }


    private void selectImage()

    {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
        {
            Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,REQUESTCODE);
        }
        else
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUESTCODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUESTCODE)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,REQUESTCODE);
            }
            else
            {
                Toast.makeText(this, "premissio deny", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUESTCODE)
        {
            if (resultCode==RESULT_OK) {
                localFileUri= data.getData();

                ivProfile.setImageURI(localFileUri);



            }
        }
    }

    //<!-- TODO Update Only userName of user
    private  void UpdateOnlyName()
    {
        progressbar.setVisibility(View.VISIBLE);
        mFirebaseUser=mFirebaseAuth.getCurrentUser();
        UserProfileChangeRequest userProfileChangeRequest=new UserProfileChangeRequest.Builder()
                .setDisplayName(eName.getText().toString().trim())
                .build();

        mFirebaseUser.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    String userId=mFirebaseUser.getUid();
                    mDatabaseReference= FirebaseDatabase.getInstance().getReference().child(Common.USERS);
                    HashMap hashMap=new HashMap<>();
                    hashMap.put(Common.NAME,eName.getText().toString().trim());


                    mDatabaseReference.child(userId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressbar.setVisibility(View.GONE);
                            if(task.isSuccessful())
                                Toast.makeText(User_Profile.this, "user profile updated", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(User_Profile.this, "failed to update Info", Toast.LENGTH_SHORT).show();
                        }
                    });


                }

                else
                    Toast.makeText(User_Profile.this,
                            "failed to update the user :%1$s"+task.getException(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    //<--TODo Update userProfile with picture

    private  void UpdateWithPicture()
    {
        progressbar.setVisibility(View.VISIBLE);
        final String fileName="/imagee"+mFirebaseUser.getUid()+".jpg";
        final StorageReference fileRef=mStorageReference.child(Common.PROFILEIMAGE).child(fileName);
        fileRef.putFile(localFileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful())
                {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.e("uri:",uri.toString());
                            serverFileUri=uri;
                            UserProfileChangeRequest userProfileChangeRequest=new UserProfileChangeRequest.Builder()
                                    .setDisplayName(eName.getText().toString().trim())
                                    .setPhotoUri(serverFileUri)
                                    .build();


                            mFirebaseUser.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        String userId=mFirebaseUser.getUid();
                                        mDatabaseReference= FirebaseDatabase.getInstance().getReference().child(Common.USERS);
                                        HashMap hashMap=new HashMap<>();
                                        hashMap.put(Common.NAME,eName.getText().toString().trim());
                                        hashMap.put(Common.PHOTO,fileName);
                                        Log.d("serverfilre:",fileName);
                                        mDatabaseReference.child(userId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progressbar.setVisibility(View.GONE);
                                                if(task.isSuccessful())
                                                {
                                                    Toast.makeText(User_Profile.this,
                                                            "profile updated", Toast.LENGTH_SHORT).show();
                                                }

                                                else
                                                    Toast.makeText(User_Profile.this,
                                                            "failed to update Info", Toast.LENGTH_SHORT).show();
                                            }
                                        });


                                    }
                                    else
                                        Toast.makeText(User_Profile.this,
                                                "failed to update the user :%1$s"+task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }) ;
                }
                else {
                    Toast.makeText(User_Profile.this,
                            "failed to upload", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void RemovePhoto()
    {
        progressbar.setVisibility(View.VISIBLE);
        UserProfileChangeRequest userProfileChangeRequest=new UserProfileChangeRequest.Builder()
                .setDisplayName(eName.getText().toString().trim())
                .setPhotoUri(null)
                .build();

        mFirebaseUser.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    String userId=mFirebaseUser.getUid();
                    mDatabaseReference= FirebaseDatabase.getInstance().getReference().child(Common.USERS);
                    HashMap<String,String> hashMap=new HashMap<>();
                    hashMap.put(Common.NAME,eName.getText().toString().trim());
                    hashMap.put(Common.PHOTO,"");
                    mDatabaseReference.child(userId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressbar.setVisibility(View.GONE);
                            if(task.isSuccessful())
                            {
                                Toast.makeText(User_Profile.this, "Removed", Toast.LENGTH_SHORT).show();

                            }

                            else
                                Toast.makeText(User_Profile.this, "failed to update Info", Toast.LENGTH_SHORT).show();
                        }
                    });


                }

                else
                    Toast.makeText(User_Profile.this,
                            "failed to update the user :%1$s"+task.getException(), Toast.LENGTH_SHORT).show();
            }
        });

    }



   public void Logout(View V)
   {
       DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();
       FirebaseUser currentUser=mFirebaseAuth.getCurrentUser();
       DatabaseReference removeToke=rootRef.child(Common.TOKENS).child(currentUser.getUid());
       removeToke.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {
               if(task.isSuccessful())
               {
                   mFirebaseAuth.signOut();
                   startActivity(new Intent(User_Profile.this,Login.class));
                   finish();
               }else
               {
                   Toast.makeText(User_Profile.this, "Fsiled to delete token", Toast.LENGTH_SHORT).show();
               }

           }
       });

   }


    public void goChamgePassword(View view) {
        startActivity(new Intent(User_Profile.this, Change_Password.class));
    }
}