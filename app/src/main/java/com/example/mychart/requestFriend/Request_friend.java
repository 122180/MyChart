package com.example.mychart.requestFriend;

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
import com.example.mychart.common.Contents;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class Request_friend extends Fragment {

private RecyclerView mRecyclerView;

private MyRequestFriend mMyRequestFriendAdpter;
private List<RequestModel> mRequestModelList;
private DatabaseReference mDatabaseReference,mDatabaseReferenceName;
private FirebaseUser currentUser;
private TextView tvfindMessage;
private View progreassBar;
    public Request_friend() {
        // Required empty public constructor
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request_friend, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView=view.findViewById(R.id.requestRecyleview);
         tvfindMessage=view.findViewById(R.id.tvfindMessage);
         progreassBar=view.findViewById(R.id.progressBar);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRequestModelList=new ArrayList<>();

        mMyRequestFriendAdpter=new MyRequestFriend(getActivity(),mRequestModelList);
        mRecyclerView.setAdapter(mMyRequestFriendAdpter);
        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseReference= FirebaseDatabase.getInstance().getReference().child(Common.FRIEND_REQUEST).child(currentUser.getUid());
        mDatabaseReferenceName=FirebaseDatabase.getInstance().getReference().child(Common.USERS);

        progreassBar.setVisibility(View.VISIBLE);
        tvfindMessage.setVisibility(View.VISIBLE);

     mDatabaseReference.addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot snapshot) {
             progreassBar.setVisibility(View.GONE);

         //I want to remove this line for checking purpose
             Log.d("onDataChange:","runfirst");
             mRequestModelList.clear();
             for (DataSnapshot ds:snapshot.getChildren())
             {
                 if(ds.exists())
                 {
                     String RequestType=ds.child(Contents.REQUEST_TYPE).getValue().toString();
                     if(RequestType.equals(Contents.RECEIVE))
                     {
                         final String userId=ds.getKey();
                         mDatabaseReferenceName.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                             @Override
                             public void onDataChange(@NonNull DataSnapshot snapshot) {
                                 String userName=snapshot.child(Common.NAME).getValue().toString();
                                 String PhotoName="";
                                 if(snapshot.child(Common.PHOTO).getValue()!=null)
                                 {
                                     PhotoName=snapshot.child(Common.PHOTO).getValue().toString();

                                 }
                                 RequestModel requestModel=new RequestModel(userName,userId,PhotoName);
                                 mRequestModelList.add(requestModel);
                                 mMyRequestFriendAdpter.notifyDataSetChanged();
                                 tvfindMessage.setVisibility(View.GONE);
                             }

                             @Override
                             public void onCancelled(@NonNull DatabaseError error) {
                                 Toast.makeText(getContext(), "Faile to fetch friend request :"+error.getMessage(), Toast.LENGTH_SHORT).show();
                                 progreassBar.setVisibility(View.GONE);
                                 tvfindMessage.setVisibility(View.VISIBLE);
                             }
                         });
                     }

                 }

             }

         }

         @Override
         public void onCancelled(@NonNull DatabaseError error) {
             Toast.makeText(getContext(), "Faile to fetch friend request :"+error.getMessage(), Toast.LENGTH_SHORT).show();
             progreassBar.setVisibility(View.GONE);
             tvfindMessage.setVisibility(View.VISIBLE);

         }
     });


    }
}