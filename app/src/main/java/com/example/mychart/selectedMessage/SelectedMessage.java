package com.example.mychart.selectedMessage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.mychart.Extra;
import com.example.mychart.R;
import com.example.mychart.common.Common;
import com.example.mychart.common.Contents;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SelectedMessage extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private View pbBar;
    private List<SeltedModel> mList;
    private SelectedAdpter mSelectedAdpter;
    private DatabaseReference mDatabaseReferenceUser;
    private  DatabaseReference mDatabaseReferenceChat;
    private ValueEventListener mValueEventListener;
    private FirebaseUser CurrentUserId;
    private String selectedMessage,selectedMessageType,selectedMessageId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_message);
        if(getIntent().hasExtra(Extra.Message))
        {
            selectedMessage=getIntent().getStringExtra(Extra.Message);
            selectedMessageType=getIntent().getStringExtra(Extra.MessageType);
            selectedMessageId=getIntent().getStringExtra(Extra.MessageId);
        }
        mRecyclerView = findViewById(R.id.recycleseleted);
        pbBar = findViewById(R.id.pbbar);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mList = new ArrayList<>();
        mSelectedAdpter = new SelectedAdpter(mList, this);
        mRecyclerView.setAdapter(mSelectedAdpter);
        pbBar.setVisibility(View.VISIBLE);

        CurrentUserId = FirebaseAuth.getInstance().getCurrentUser();
         mDatabaseReferenceChat = FirebaseDatabase.getInstance().getReference().child(Common.CHAT).child(CurrentUserId.getUid());
        mDatabaseReferenceUser = FirebaseDatabase.getInstance().getReference().child(Common.USERS);

        mValueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pbBar.setVisibility(View.GONE);
               for(DataSnapshot ds:snapshot.getChildren())
               {
                 final String userId=ds.getKey();
                 mDatabaseReferenceUser.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot snapshot) {
                         String UserName=snapshot.child(Common.NAME).getValue().toString()!=null?
                                 snapshot.child(Common.NAME).getValue().toString():"";
                         String PhotoName=snapshot.child(Common.PHOTO).getValue().toString()!=null?
                                 snapshot.child(Common.PHOTO).getValue().toString():"";
                         SeltedModel seltedModel=new SeltedModel(userId,UserName,PhotoName);
                         mList.add(seltedModel);
                        mSelectedAdpter.notifyDataSetChanged();


                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError error) {
                         Toast.makeText(SelectedMessage.this, R.string.failed_to_fetch_user, Toast.LENGTH_SHORT).show();

                     }
                 });
               }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SelectedMessage.this, R.string.failed_to_fetch_user, Toast.LENGTH_SHORT).show();

            }
        };
mDatabaseReferenceChat.addValueEventListener(mValueEventListener);
    }

    public void returnSeletedFriend(String UserName,String UserId,String PhotoName)
    {

        mDatabaseReferenceChat.removeEventListener(mValueEventListener);
        Intent intent=new Intent();

        intent.putExtra(Extra.USER_NAME,UserName);
        intent.putExtra(Extra.USER_KEY,UserId);
        intent.putExtra(Extra.PHOTO_NAME,PhotoName);
        intent.putExtra(Extra.Message,selectedMessage);
        intent.putExtra(Extra.MessageId,selectedMessageId);
        intent.putExtra(Extra.MessageType,selectedMessageType);
        setResult(Activity.RESULT_OK,intent);//to return the data to back activity
        finish();
    }
}