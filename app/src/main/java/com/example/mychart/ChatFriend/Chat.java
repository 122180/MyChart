package com.example.mychart.ChatFriend;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.example.mychart.R;
import com.example.mychart.common.Common;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Chat extends Fragment {

private RecyclerView mRecyclerView;
private ChatAdapter mChatAdapter;
private List<ChatModel> mChatModelList;
private DatabaseReference mDatabaseReferenceUser,mDatabaseReferenceChat;
private  View progressbar;
private TextView showMessage;
private FirebaseUser currentUser;
private Query mQuery;
private List<String> listUserId;
private ChildEventListener mChildEventListener;
    LinearLayoutManager linearLayoutManager;

    public Chat() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listUserId=new ArrayList<>();
        progressbar = view.findViewById(R.id.progressBar);
        showMessage = view.findViewById(R.id.tvChatMessage);
        mRecyclerView = view.findViewById(R.id.chatRecyleview);
     linearLayoutManager=new LinearLayoutManager(getActivity());
      linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        mChatModelList = new ArrayList<>();

     mChatAdapter= new ChatAdapter(getContext(), mChatModelList);
     mRecyclerView.setAdapter(mChatAdapter);
     currentUser=FirebaseAuth.getInstance().getCurrentUser();
     mDatabaseReferenceUser = FirebaseDatabase.getInstance().getReference().child(Common.USERS);
     mDatabaseReferenceChat = FirebaseDatabase.getInstance().getReference().child(Common.CHAT).child(currentUser.getUid() );
     showMessage.setVisibility(View.GONE);
     progressbar.setVisibility(View.VISIBLE);
     mQuery = mDatabaseReferenceChat.orderByChild(Common.TIME_STAMP);

     mChildEventListener = new ChildEventListener() {
         @Override
         public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

             String UserId = snapshot.getKey();
             getChatUserFromFireBase(snapshot, true, UserId);
         }

         @Override
         public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
             getChatUserFromFireBase(snapshot, false, snapshot.getKey());

         }

         @Override
         public void onChildRemoved(@NonNull DataSnapshot snapshot) {

         }

         @Override
         public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

         }

         @Override
         public void onCancelled(@NonNull DatabaseError error) {
             progressbar.setVisibility(View.GONE);

             Toast.makeText(getActivity(), error.getMessage() + "", Toast.LENGTH_SHORT).show();
         }
     };

     mQuery.addChildEventListener(mChildEventListener);
     progressbar.setVisibility(View.VISIBLE);


        }




        private void getChatUserFromFireBase (DataSnapshot snapshot, final boolean isNew, final String UserId){

        showMessage.setVisibility(View.GONE);
        progressbar.setVisibility(View.GONE);
        final String lastMessage, UnreadCount, lsatTime;
        if(snapshot.child(Common.LAST_MESSAGE).getValue()!=null)
        lastMessage =snapshot.child(Common.LAST_MESSAGE).getValue().toString();
        else
            lastMessage="";
        if(snapshot.child(Common.LAST_MESSAGE_TIME).getValue()!=null) {
            lsatTime = snapshot.child(Common.LAST_MESSAGE_TIME).getValue().toString();

        }
        else
            lsatTime="";

        UnreadCount = snapshot.child(Common.UNREAD_COUNT).getValue()==null?"0":snapshot.child(Common.UNREAD_COUNT).getValue().toString();

        mDatabaseReferenceUser.child(UserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final String fullName = snapshot.child(Common.NAME).getValue().toString() != null ?
                        snapshot.child(Common.NAME).getValue().toString() : "";
                final String PhotoName = snapshot.child(Common.PHOTO).getValue().toString() != null ?
                        snapshot.child(Common.PHOTO).getValue().toString() : "";

                ChatModel chatModel = new ChatModel(fullName, UserId, PhotoName, UnreadCount, lastMessage, lsatTime);
                if(isNew) {
                    mChatModelList.add(chatModel);
                    listUserId.add(UserId);
                }else {
                    int indexOfClickUser=listUserId.indexOf(UserId);
                    mChatModelList.set(indexOfClickUser,chatModel);

                }
              mChatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showMessage.setVisibility(View.VISIBLE);
                progressbar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), error.getMessage() + "", Toast.LENGTH_SHORT).show();


            }
        });

    }
    @Override
    public void onDestroy() {
        mQuery.removeEventListener(mChildEventListener);
        super.onDestroy();

    }
}