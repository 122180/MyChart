package com.example.mychart.sendMessage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mychart.Extra;
import com.example.mychart.NetworkMessage;
import com.example.mychart.R;
import com.example.mychart.common.Common;
import com.example.mychart.common.Contents;
import com.example.mychart.common.Util;
import com.example.mychart.selectedMessage.SelectedMessage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;

public class SendMessage extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUESTCODE_GALLRY =100 ;
    private static final int REQUEST_CODE_TO_PIC_IMAGE=101;
    private static final int REQUEST_CODE_TO_CAPTURE_IMAGE=102;
    private static final int REQUEST_CODE_TO_VIDEO=103;
    private static final int REQUEST_CODE_FORWARD =104 ;
    private EditText typeMessage;
    private ImageView attechFile;
    private  ImageView sendmessage;
    private FirebaseAuth mFirebaseAuth;
    private String currentUserid,chatUserId;
    private DatabaseReference mDatabaseReference;
     private RecyclerView mRecyclerView;
     private SwipeRefreshLayout mSwipeRefreshLayout;
     private List<MessageModel> mMessageModelList;
     private MessageAdpter mMessageAdpter;
     private int CurrentPage=1;
     private static final int RECORD_PER_PAGE=30;
     private ChildEventListener mChildEventListener;
     private DatabaseReference mDatabaseReferenceMessge;
     private  View view;
     private BottomSheetDialog mBottomSheetDialog;
     private LinearLayout llProgressbar;

     private TextView tvUser_Name;
     private ImageView profile_image;
     private String User_Name;
     private String PHOTO_NAME;
     private TextView OnlineStatus;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);


        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.setTitle("");

            ViewGroup viewGroup=(ViewGroup)getLayoutInflater().inflate(R.layout.custom_actionbar,null);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setElevation(0);
            actionBar.setCustomView(viewGroup);
            actionBar.setDisplayOptions(actionBar.getDisplayOptions()|ActionBar.DISPLAY_SHOW_CUSTOM);

        }


        //<!--ToDo intilize to all View on SendMessage
        typeMessage=findViewById(R.id.edtmessage);
        attechFile=findViewById(R.id.attech);
        sendmessage=findViewById(R.id.sendMessage);
        llProgressbar=findViewById(R.id.llProgressbar);
        mRecyclerView=findViewById(R.id.messageRecycleView);
        mSwipeRefreshLayout=findViewById(R.id.swipReferesh);
        tvUser_Name=findViewById(R.id.username);
        profile_image =findViewById(R.id.custom_profile_image);
        OnlineStatus=findViewById(R.id.online);
        //<!--Todo here is end of all view


        mFirebaseAuth=FirebaseAuth.getInstance();
        currentUserid=mFirebaseAuth.getCurrentUser().getUid();
        mDatabaseReference=FirebaseDatabase.getInstance().getReference();


        if(getIntent().hasExtra(Extra.CHAT_ID))
            chatUserId=getIntent().getStringExtra(Extra.CHAT_ID);
        if(getIntent().hasExtra(Extra.USER_NAME))
            User_Name=getIntent().getStringExtra(Extra.USER_NAME);
        if(getIntent().hasExtra(Extra.PHOTO_NAME))
            PHOTO_NAME=getIntent().getStringExtra(Extra.PHOTO_NAME);

        tvUser_Name.setText(User_Name);
        if(!TextUtils.isEmpty(PHOTO_NAME))
        {
            StorageReference storageReference=FirebaseStorage.getInstance().getReference().child(Common.PROFILEIMAGE+"/"+PHOTO_NAME);

            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    Glide.with(SendMessage.this)
                            .load(uri)
                            .placeholder(R.drawable.default_profile)
                            .error(R.drawable.gallery)
                            .into(profile_image);
                }
            });

        }


       //Listener for message Option
        sendmessage.setOnClickListener(this);
        attechFile.setOnClickListener(this);



        mMessageModelList=new ArrayList<>();
        mMessageAdpter=new MessageAdpter(this,mMessageModelList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mMessageAdpter);
        Toast.makeText(this, "load message work", Toast.LENGTH_SHORT).show();
        loadMessage();
        mRecyclerView.scrollToPosition(mMessageModelList.size()-1);
        mSwipeRefreshLayout.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                CurrentPage++;
                loadMessage();
            }
        });
        mDatabaseReference.child(Common.CHAT).child(currentUserid).child(chatUserId).child(Common.UNREAD_COUNT).setValue(0);

     //Listener for BottomSheet
        mBottomSheetDialog=new BottomSheetDialog(this);
         view=getLayoutInflater().inflate(R.layout.attech_view,null);
       view.findViewById(R.id.llcamera).setOnClickListener(this);
      view.findViewById(R.id.llGallery).setOnClickListener(this);
       view.findViewById(R.id.llVideo).setOnClickListener(this);
       view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               mBottomSheetDialog.dismiss();
           }
       });
       mBottomSheetDialog.setContentView(view);

     if(getIntent().hasExtra(Extra.Message)&&getIntent().hasExtra(Extra.MessageId)&&getIntent().hasExtra(Extra.MessageType))
     {
         String Message=getIntent().getStringExtra(Extra.Message);
         String MessageId=getIntent().getStringExtra(Extra.MessageId);
         final String MessageType=getIntent().getStringExtra(Extra.MessageType);

         DatabaseReference mDataRefernce=FirebaseDatabase.getInstance().getReference().child(Common.MESSAGES).child(currentUserid).child(chatUserId).push();
         final String newMessageId=mDataRefernce.getKey();
         if(MessageType.equals(Contents.Type_text))
         {
             SendMessagetoFriend(Message,MessageType,newMessageId);
         }
        else
         {
             StorageReference rootRef=FirebaseStorage.getInstance().getReference();
             String FolderName=MessageType.equals(Common.VIDEO)?Common.VIDEO:Common.IMAGE;
             String oldName=FolderName.equals(Common.VIDEO)?MessageId+".mp4":MessageId+".jpg";
             String newFileName=FolderName.equals(Common.VIDEO)?newMessageId+".mp4":newMessageId+".jpg";

             final String localFilePath=getExternalFilesDir(null).getAbsolutePath()+"/"+oldName;
             final File localFile=new File(localFilePath);
             final StorageReference newFileRef=rootRef.child(FolderName).child(newFileName);
             rootRef.child(FolderName).child(oldName).getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                 @Override
                 public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                     UploadTask uploadTask=newFileRef.putFile(Uri.fromFile(localFile));
                     UploadProgress(uploadTask,newFileRef,newMessageId,MessageType);

                 }
             });


         }
     }
      DatabaseReference databaseReferenceUsers=mDatabaseReference.child(Common.USERS).child(chatUserId);
     databaseReferenceUsers.addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot snapshot) {
             String status="";
             if(snapshot.child(Common.ONLINE).getValue()!=null)
             {
                 status=snapshot.child(Common.ONLINE).getValue().toString();
                 Log.d("status",status);
             }
             if(status.equals("true")) {
                 OnlineStatus.setText(Common.STATUS_ONLINE);
             }else
             {
                 OnlineStatus.setText(Common.STATUS_OffONLINE);
             }

         }

         @Override
         public void onCancelled(@NonNull DatabaseError error) {

         }
     });

  typeMessage.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
          DatabaseReference currentUser=mDatabaseReference.child(Common.CHAT).child(currentUserid).child(chatUserId);
          if(s.toString().matches(""))
          {
              currentUser.child(Common.TYPING).setValue(Common.TYPING_STOPPED);
          }else
          {
              currentUser.child(Common.TYPING).setValue(Common.TYPING_STARTED);
          }

      }
  });
  DatabaseReference chatUserRef=mDatabaseReference.child(Common.CHAT).child(chatUserId).child(currentUserid);
  chatUserRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
          if(snapshot.child(Common.TYPING).getValue()!=null)
          {
              String typingstatus=snapshot.child(Common.TYPING).getValue().toString();
              if(typingstatus.equals(Common.TYPING_STARTED))
                  OnlineStatus.setText(Common.STATUS_TYPING);
              else
                  OnlineStatus.setText(Common.STATUS_ONLINE);
          }

      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
  });

    }




    private void SendMessagetoFriend(final String msg, final String msgType, String pushid)
    {
        try {
            if(!msg.equals(""))
            {
                HashMap hashMap=new HashMap();
                hashMap.put(Common.MESSAGE_ID,pushid);
                hashMap.put(Common.MESSAGE,msg);
                hashMap.put(Common.MESSAGE_TYPE,msgType);
                hashMap.put(Common.FROM,currentUserid);
                hashMap.put(Common.MESSAGE_TIME, ServerValue.TIMESTAMP);

                String currentUserRef=Common.MESSAGES+"/"+currentUserid+"/"+chatUserId;
                String chatUserRef=Common.MESSAGES+"/"+chatUserId+"/"+currentUserid;
                HashMap messaageUserMap=new HashMap();
                messaageUserMap.put(currentUserRef+"/"+pushid,hashMap);
                messaageUserMap.put(chatUserRef+"/"+pushid,hashMap);
                typeMessage.setText("");

                mDatabaseReference.updateChildren(messaageUserMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if(error!=null)
                        {
                            Toast.makeText(SendMessage.this, error.getMessage()+"", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            String title="";
                            if(msgType.equals(Contents.Type_text))
                                title="new Message";
                            else if(msgType.equals(Common.IMAGE))
                                title="new Message";
                            else if(msgType.equals(Common.VIDEO))
                                title="new video";

                          Util.sendNotification(SendMessage.this,title,msg,chatUserId);
                          String lastMessage=!title.equals("new Message")?title:msg;
                         Util.upadateChatDetail(SendMessage.this,currentUserid,chatUserId,lastMessage);

                            Toast.makeText(SendMessage.this, "Message sent", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
            else {
                Toast.makeText(this, "input the message", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e)
        {
            Toast.makeText(this, e.getMessage()+"", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMessage()
    {
        mMessageModelList.clear();
        mDatabaseReferenceMessge=mDatabaseReference.child(Common.MESSAGES).child(currentUserid).child(chatUserId);
        Query messagequery=mDatabaseReferenceMessge.limitToLast(CurrentPage*RECORD_PER_PAGE);
        if(mChildEventListener!=null)
            messagequery.removeEventListener(mChildEventListener);
        mChildEventListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                MessageModel messageModel=snapshot.getValue(MessageModel.class);
                mMessageModelList.add(messageModel);
                mMessageAdpter.notifyDataSetChanged();
                mRecyclerView.scrollToPosition(mMessageModelList.size()-1);
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                loadMessage();

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mSwipeRefreshLayout.setRefreshing(false);

            }
        };
          messagequery.addChildEventListener(mChildEventListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.sendMessage:
                if(Util.ConnectionAvailable(this))
                {
                  DatabaseReference Reference=mDatabaseReference.child(Common.MESSAGES).child(currentUserid).child(chatUserId).push();
                    String pushId=Reference.getKey();

                    SendMessagetoFriend(typeMessage.getText().toString().trim(), Contents.Type_text,pushId);

                }
                else
                {
                    startActivity(new Intent( SendMessage.this, NetworkMessage.class));
                    Toast.makeText(this, "no Intenet", Toast.LENGTH_SHORT).show();
                }


                break;

            case R.id.attech:
                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                {
                    if(mBottomSheetDialog!=null)
                        mBottomSheetDialog.show();

                }
                else
                {
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUESTCODE_GALLRY);
                }
                InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                if(inputMethodManager!=null)
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);

                break;
            case R.id.llcamera:
                mBottomSheetDialog.dismiss();
                Intent intentCamera=new Intent(ACTION_IMAGE_CAPTURE);
                startActivityForResult(intentCamera,REQUEST_CODE_TO_CAPTURE_IMAGE);

                break;
            case R.id.llGallery:
                mBottomSheetDialog.dismiss();
                Intent intentGallery=new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentGallery,REQUEST_CODE_TO_PIC_IMAGE);
                break;
            case R.id.llVideo:
                mBottomSheetDialog.dismiss();
                Intent intentVideo=new Intent(Intent.ACTION_PICK,
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentVideo,REQUEST_CODE_TO_VIDEO);
                break;

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUESTCODE_GALLRY)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                if(mBottomSheetDialog!=null)
                    mBottomSheetDialog.show();

            }
            else {
                Toast.makeText(this, "permission required to access files", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {
            if(requestCode==REQUEST_CODE_TO_CAPTURE_IMAGE)
            {
                Bitmap bitmap=(Bitmap)data.getExtras().get("data");
                ByteArrayOutputStream outputStream=new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                upLoadByteFile(outputStream,Common.IMAGE);

            }
            else if(requestCode==REQUEST_CODE_TO_PIC_IMAGE)
            {
                Uri uri=data.getData();
                upLoadfile(uri,Common.IMAGE);
            }
            else if(requestCode==REQUEST_CODE_TO_VIDEO)
            {
                Uri uri=data.getData();
                upLoadfile(uri,Common.VIDEO);

            }else if(requestCode==REQUEST_CODE_FORWARD)
            {
                Intent intent=new Intent(this,SendMessage.class);
                intent.putExtra(Extra.USER_KEY,data.getStringExtra(Extra.USER_KEY));
                intent.putExtra(Extra.USER_NAME,data.getStringExtra(Extra.USER_NAME));
                intent.putExtra(Extra.PHOTO_NAME,data.getStringExtra(Extra.PHOTO_NAME));
                intent.putExtra(Extra.Message,data.getStringExtra(Extra.Message));
                intent.putExtra(Extra.MessageId,data.getStringExtra(Extra.MessageId));
                intent.putExtra(Extra.MessageType,data.getStringExtra(Extra.MessageType));
                startActivity(intent);
                finish();



            }
        }
    }

    private void upLoadfile(Uri uri,String MessageType){
       DatabaseReference mDataBaseref=mDatabaseReference.child(Common.MESSAGES).child(currentUserid).child(chatUserId).push();
       String pushId=mDataBaseref.getKey();
        StorageReference storageReference= FirebaseStorage.getInstance().getReference();
        String FolderName=MessageType.equals(Common.VIDEO)?Common.VIDEO:Common.IMAGE;
        String FIleName=MessageType.equals(Common.VIDEO)?pushId+".mp4":pushId+".jpg";
        StorageReference FileRef=storageReference.child(FolderName).child(FIleName);
        UploadTask uploadTask=FileRef.putFile(uri);
        UploadProgress(uploadTask,FileRef,pushId,MessageType);


    }
    private void upLoadByteFile(ByteArrayOutputStream outputStream,String MessageType)
    {
        DatabaseReference mDataBaseref=mDatabaseReference.child(Common.MESSAGES).child(currentUserid).child(chatUserId).push();
        String pushId=mDataBaseref.getKey();
        StorageReference storageReference= FirebaseStorage.getInstance().getReference();
        String FolderName=MessageType.equals(Common.VIDEO)?Common.VIDEO:Common.IMAGE;
        String FIleName=MessageType.equals(Common.VIDEO)?pushId+".mp4":pushId+".jpg";
        StorageReference FileRef=storageReference.child(FolderName).child(FIleName);
       UploadTask uploadTask= FileRef.putBytes(outputStream.toByteArray());
       UploadProgress(uploadTask,FileRef,pushId,MessageType);
    }

    private void UploadProgress(final UploadTask task, final StorageReference fllePath, final String pushId, final String MessageType)
    {
        final View view=getLayoutInflater().inflate(R.layout.file_progress,null);
        final ProgressBar progressBar=view.findViewById(R.id.pbbar);
        final TextView textBar=view.findViewById(R.id.textBar);
        final ImageView ivPlay=view.findViewById(R.id.ivPlay);
        final ImageView ivPause=view.findViewById(R.id.ivPause);
        ImageView ivCancel=view.findViewById(R.id.ivCancel);
        ivPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.pause();
                ivPause.setVisibility(View.GONE);
                ivPlay.setVisibility(View.VISIBLE);
            }
        });
        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              task.resume();
              ivPlay.setVisibility(View.GONE);
              ivPause.setVisibility(View.VISIBLE);

            }
        });
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.cancel();

            }
        });
        llProgressbar.addView(view);
        textBar.setText(getString(R.string.upload_progressbar,MessageType,"0"));
        task.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
               double progress=(100.0*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
               progressBar.setProgress((int)progress);
                textBar.setText(getString(R.string.upload_progressbar,MessageType,String.valueOf(progressBar.getProgress())));
            }
        });

        task.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                llProgressbar.removeView(view);
                if(task.isSuccessful())
                {
                    fllePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl=uri.toString();
                          SendMessagetoFriend(downloadUrl,MessageType,pushId);
                        }
                    });
                }
            }
        });
    task.addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Toast.makeText(SendMessage.this, getString(R.string.failed_tp_upload,e.getMessage()), Toast.LENGTH_SHORT).show();
        }
    });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);

    }
    public void deleteMessage(final String MessageType, final String MessageId)
    {
       DatabaseReference reference=mDatabaseReference.child(Common.MESSAGES).child(chatUserId).child(currentUserid).child(MessageId);
       reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {
               if(task.isSuccessful())
               {
                   DatabaseReference reference2=mDatabaseReference.child(Common.MESSAGES).child(currentUserid).child(chatUserId).child(MessageId);
                   reference2.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if(task.isSuccessful())
                           {
                               Toast.makeText(SendMessage.this, "message delete successfull", Toast.LENGTH_SHORT).show();
                             if(!MessageType.equals(Contents.Type_text))
                             {
                                 StorageReference rootRef=FirebaseStorage.getInstance().getReference();
                                 String FolderName=MessageType.equals(Common.VIDEO)?Common.VIDEO:Common.IMAGE;
                                 String FileName=FolderName.equals(Common.VIDEO)?MessageId+".mp4":MessageId+".mp4";
                                 StorageReference FileRef=rootRef.child(FolderName).child(FileName);
                                 FileRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                     @Override
                                     public void onComplete(@NonNull Task<Void> task) {
                                         if(task.isSuccessful())
                                         {
                                             Toast.makeText(SendMessage.this, "message delete successfull delete", Toast.LENGTH_SHORT).show();
                                         }else
                                         {
                                             Toast.makeText(SendMessage.this, getString(R.string.message_failed_to_delete,task.getException()), Toast.LENGTH_SHORT).show();

                                         }

                                     }
                                 });

                             }
                           }
                           else
                           {
                               Toast.makeText(SendMessage.this, getString(R.string.message_failed_to_delete,task.getException()), Toast.LENGTH_SHORT).show();

                           }

                       }
                   });

               }else
               {
                   Toast.makeText(SendMessage.this, getString(R.string.message_failed_to_delete,task.getException()), Toast.LENGTH_SHORT).show();
               }

           }
       });



    }

    public void DownLoadFile(final String MessageType, String MessageId, final boolean isShare)
    {
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
        }else
        {
            String FolderName=MessageType.equals(Common.VIDEO)?Common.VIDEO:Common.IMAGE;
            String FileName=FolderName.equals(FolderName)?MessageId+ ".mp4":MessageId+ ".jpg";
            StorageReference fileRef=FirebaseStorage.getInstance().getReference().child(FolderName).child(FileName);
            final String localPath=getExternalFilesDir(null).getAbsolutePath()+"/"+FileName;
            File localFile=new File(localPath);
            try {

                if(localFile.exists()||localFile.createNewFile())
                {
                    final FileDownloadTask fileDownloadTask=fileRef.getFile(localFile);

                        final View view=getLayoutInflater().inflate(R.layout.file_progress,null);
                        final ProgressBar progressBar=view.findViewById(R.id.pbbar);
                        final TextView textBar=view.findViewById(R.id.textBar);
                        final ImageView ivPlay=view.findViewById(R.id.ivPlay);
                        final ImageView ivPause=view.findViewById(R.id.ivPause);
                        ImageView ivCancel=view.findViewById(R.id.ivCancel);
                        ivPause.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                fileDownloadTask.pause();
                                ivPause.setVisibility(View.GONE);
                                ivPlay.setVisibility(View.VISIBLE);
                            }
                        });
                        ivPlay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                fileDownloadTask.resume();
                                ivPlay.setVisibility(View.GONE);
                                ivPause.setVisibility(View.VISIBLE);

                            }
                        });
                        ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                fileDownloadTask.cancel();

                            }
                        });
                        llProgressbar.addView(view);
                        textBar.setText(getString(R.string.Download_progressbar,MessageType,"0"));
                        fileDownloadTask.addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull FileDownloadTask.TaskSnapshot snapshot) {
                                double progress=(100.0*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                                progressBar.setProgress((int)progress);
                                textBar.setText(getString(R.string.Download_progressbar,MessageType,String.valueOf(progressBar.getProgress())));
                            }
                        });

                        fileDownloadTask.addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                                llProgressbar.removeView(view);
                                if(task.isSuccessful())
                                {
                                    if(isShare)
                                    {
                                      Intent shareIntent=new Intent();
                                      shareIntent.setAction(Intent.ACTION_SEND);
                                      shareIntent.putExtra(Intent.EXTRA_STREAM,Uri.parse(localPath));
                                      if(MessageType.equals(Common.VIDEO))
                                          shareIntent.setType("video/mp4");
                                      if(MessageType.equals(Common.IMAGE))
                                          shareIntent.setType("image/jpg");
                                      startActivity(Intent.createChooser(shareIntent,getString(R.string.share_with)));
                                    }else {


                                        Snackbar snackbar = Snackbar.make(llProgressbar, getString(R.string.file_downlod_successfully),
                                                Snackbar.LENGTH_INDEFINITE);

                                        snackbar.setAction(R.string.view, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Uri uri = Uri.parse(localPath);
                                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                if (MessageType.equals(Common.VIDEO)) {
                                                    intent.setDataAndType(uri, "video/mp4");

                                                } else if (MessageType.equals(Common.IMAGE)) {
                                                    intent.setDataAndType(uri, "image/jpg");
                                                }
                                                startActivity(intent);


                                            }
                                        });
                                        snackbar.show();
                                    }
                                }
                            }
                        });

                        fileDownloadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SendMessage.this, getString(R.string.failed_to_download,e.getMessage()), Toast.LENGTH_SHORT).show();
                            }
                        });



                }else
                {
                    Toast.makeText(this, getString(R.string.failed_to_store), Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e)
            {
                Toast.makeText(this,getString(R.string.faile_to_download,e.getMessage()), Toast.LENGTH_SHORT).show();
            }



        }


    }

    public void ForWord(String selectedMessage, String selectMessageType, String selectMessageId) {
        Intent intent=new Intent(this, SelectedMessage.class);
        intent.putExtra(Extra.Message,selectedMessage);
        intent.putExtra(Extra.MessageId,selectMessageId);
        intent.putExtra(Extra.MessageType,selectMessageType);

        startActivityForResult(intent,REQUEST_CODE_FORWARD);
    }

    @Override
    public void onBackPressed() {
        mDatabaseReference.child(Common.CHAT).child(currentUserid).child(chatUserId).child(Common.UNREAD_COUNT).setValue(0);

        super.onBackPressed();
    }
}