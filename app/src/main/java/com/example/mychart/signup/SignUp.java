package com.example.mychart.signup;

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
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.mychart.Login.Login;
import com.example.mychart.MainActivity;
import com.example.mychart.NetworkMessage;
import com.example.mychart.R;
import com.example.mychart.common.Common;
import com.example.mychart.common.Contents;
import com.example.mychart.common.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.InstallationTokenResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class SignUp extends AppCompatActivity {
    private static final int REQUESTCODE = 100;
    private TextInputEditText eName, eEmail, ePassword, eConformPassword;
    private ImageView ivProfile;
    private String name, email, password, conformpassword;
    private LinearLayout mLinearLayout;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    private Uri localFileUri, serverFileUri;
    private View progressbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mStorageReference = FirebaseStorage.getInstance().getReference();
        mLinearLayout = findViewById(R.id.linearlayout);
        eName = findViewById(R.id.name);
        eEmail = findViewById(R.id.email_id);
        ePassword = findViewById(R.id.password);
        eConformPassword = findViewById(R.id.conformpassword);
        ivProfile = findViewById(R.id.imageiews);
        progressbar = findViewById(R.id.progressbarshow);


        mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this, Login.class));
                finish();
            }
        });
    }

    public void setSignUp(View view) {
          if(Util.ConnectionAvailable(this)) {


              name = eName.getText().toString().trim();
              email = eEmail.getText().toString().trim();
              password = ePassword.getText().toString().trim();
              conformpassword = eConformPassword.getText().toString().trim();

              if (name.equals("")) {
                  eName.setError("Empty");
              } else if (email.equals("")) {
                  eEmail.setError("Empty");
              } else if (password.equals(""))
                  ePassword.setError("Empty");
              else if (conformpassword.equals(""))
                  eConformPassword.setError("Empty");
              else if (!conformpassword.equals(password))
                  eConformPassword.setError("password is not matched");
                else if(password.length()<6)
                  ePassword.setError("password is not less 7");

              else {
                  progressbar.setVisibility(View.VISIBLE);
                  createUserAccount(email, password);


              }
          }else
          {
              startActivity(new Intent(SignUp.this, NetworkMessage.class));
          }
    }

    private void createUserAccount(String email, String password) {
        mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    mFirebaseUser = mFirebaseAuth.getCurrentUser();
                    if (localFileUri != null)
                        UpdateWithPicture();
                    else
                        UpdateOnlyName();

                } else {
                    Toast.makeText(SignUp.this, task.getException() + "", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    //<!-- TODO Update Only userName of user
    private void UpdateOnlyName() {

        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(eName.getText().toString().trim())
                .build();

        mFirebaseUser.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    String userId = mFirebaseUser.getUid();
                    mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(Common.USERS);
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put(Common.NAME, eName.getText().toString().trim());
                    hashMap.put(Common.EMAIL, eEmail.getText().toString().trim());
                    hashMap.put(Common.ONLINE, "true");
                    hashMap.put(Common.PHOTO, "");
                    mDatabaseReference.child(userId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressbar.setVisibility(View.GONE);
                            if (task.isSuccessful())
                            {
                                startActivity(new Intent(SignUp.this, MainActivity.class));
                                finish();
                                new Login().finish();
                                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                    @Override
                                    public void onSuccess(InstanceIdResult instanceIdResult) {
                                        Util.updateDeviseToken(SignUp.this,instanceIdResult.getToken());
                                    }
                                });
                                Toast.makeText(SignUp.this, "signup Successfull", Toast.LENGTH_SHORT).show();

                            }

                            else
                                Toast.makeText(SignUp.this, "failed to update Info", Toast.LENGTH_SHORT).show();
                        }
                    });


                } else
                    Toast.makeText(SignUp.this,
                            "failed to update the user :%1$s" + task.getException(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    //<--TODo Update userProfile with picture

    private void UpdateWithPicture() {

            final String fileName ="/imagee"+mFirebaseUser.getUid() + ".jpg";
          //  final StorageReference fileRef = mStorageReference.child(Common.PROFILEIMAGE).child("/imagee"+fileName);
            final StorageReference fileRef = mStorageReference.child(Common.PROFILEIMAGE).child(fileName);
            fileRef.putFile(localFileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {

                    if (task.isSuccessful()) {
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.e("uri:",uri.toString());
                                serverFileUri = uri;

                                UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(eName.getText().toString().trim())
                                        .setPhotoUri(serverFileUri)
                                        .build();


                                mFirebaseUser.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            String userId = mFirebaseUser.getUid();
                                            mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(Common.USERS);
                                            HashMap<String, String> hashMap = new HashMap<>();
                                            hashMap.put(Common.NAME, eName.getText().toString().trim());
                                            hashMap.put(Common.EMAIL, eEmail.getText().toString().trim());
                                            hashMap.put(Common.ONLINE, "true");
                                           // hashMap.put(Common.PHOTO, serverFileUri.getPath());
                                            hashMap.put(Common.PHOTO, fileName);
                                            mDatabaseReference.child(userId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressbar.setVisibility(View.GONE);
                                                    if (task.isSuccessful())
                                                    {
                                                        FirebaseInstallations.getInstance().getToken(true).addOnSuccessListener(new OnSuccessListener<InstallationTokenResult>() {
                                                            @Override
                                                            public void onSuccess(InstallationTokenResult installationTokenResult) {

                                                                Util.updateDeviseToken(SignUp.this,installationTokenResult.getToken());

                                                            }
                                                        });


                                                        startActivity(new Intent(SignUp.this, MainActivity.class));
                                                        new Login().finish();
                                                        finish();

                                                        Toast.makeText(SignUp.this, "signup Successfull", Toast.LENGTH_SHORT).show();
                                                    }

                                                    else
                                                        Toast.makeText(SignUp.this, "failed to update Info", Toast.LENGTH_SHORT).show();
                                                }
                                            });


                                        } else
                                            Toast.makeText(SignUp.this,
                                                    "failed to update the user :%1$s" + task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        });
                    } else {
                        Toast.makeText(SignUp.this, "failed to upload", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    }

    public void selectImage(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUESTCODE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUESTCODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUESTCODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUESTCODE);
            } else {
                Toast.makeText(this, "premissio deny", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE) {
            if (resultCode == RESULT_OK) {
                localFileUri = data.getData();
                ivProfile.setImageURI(localFileUri);


            }
        }
    }
}