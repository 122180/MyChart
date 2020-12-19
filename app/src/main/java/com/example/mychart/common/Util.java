package com.example.mychart.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mychart.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Util {

    public static boolean ConnectionAvailable(Context context)
    {
        ConnectivityManager connectivityManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager!=null && connectivityManager.getActiveNetworkInfo()!=null)
        {
            return connectivityManager.getActiveNetworkInfo().isAvailable();
        }
        else
        {
            return false;
        }
    }

    public static void updateDeviseToken(final Context context, String token)
    {
        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser!=null)
        {
            DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();
        DatabaseReference tokenRef=rootRef.child(Common.TOKENS).child(currentUser.getUid());

        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put(Common.DEVICE_TOKEN,token);
        tokenRef.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful())
                {
                    Toast.makeText(context, R.string.failed_to_store_device_Token, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    }

public static void sendNotification(final Context context, final String title, final String message, String userId)
{
    DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseReference=rootRef.child(Common.TOKENS).child(userId);
    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if(snapshot.child(Common.DEVICE_TOKEN).getValue()!=null)
            {
                String deviceToken=snapshot.child(Common.DEVICE_TOKEN).getValue().toString();
                JSONObject notiFication=new JSONObject();
                JSONObject notiFicationData=new JSONObject();
                try
                {
                    notiFicationData.put(Common.NOTIFICATION_TITTLE,title);
                    notiFicationData.put(Common.NOTIFICATION_MESSAGE,message);

                    notiFication.put(Common.NOTIFICATION_TO,deviceToken);
                    notiFication.put(Common.NOTIFICATION_DATA,notiFicationData);
                    String fcmAPiUrl="https://fcm.googleapis.com/fcm/send/";
                    final String conentType="application/json";
                    Response.Listener successlistener=new Response.Listener() {
                        @Override
                        public void onResponse(Object response) {
                            Toast.makeText(context, "notification sent", Toast.LENGTH_SHORT).show();

                        }
                    };
                    Response.ErrorListener errorListener=new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context, "failed to send notification :"+error.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    };

                    JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(fcmAPiUrl,notiFication,
                            successlistener,errorListener)
                    {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String,String> parms= new HashMap<>();
                            parms.put("Authorization","key=" + Common.FIREBSESERVER_KEY);
                            parms.put("Sender","id" + Common.FIREBASE_SENDER_ID);
                            parms.put("Content-Type",conentType);
                            return parms;
                        }
                    };
                    RequestQueue requestQueue=
                            Volley.newRequestQueue(context);
                    requestQueue.add(jsonObjectRequest);

                }catch (JSONException e)

                {
                    Toast.makeText(context, "failed exception notification"+e.getMessage(), Toast.LENGTH_SHORT).show();

                }



            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(context, " notification cancelled"+error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    });
}
  public static void upadateChatDetail(final Context context, final String CurrentUserId, final String ChatUserId,final String lastMessage)
  {
      final DatabaseReference rootRef= FirebaseDatabase.getInstance().getReference();
      DatabaseReference chatRef=rootRef.child(Common.CHAT).child(ChatUserId).child(CurrentUserId);
      chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot) {
              String currentcount="0";
              if(snapshot.child(Common.UNREAD_COUNT).getValue()!=null)
              {
                  currentcount=snapshot.child(Common.UNREAD_COUNT).getValue().toString();
                  Map chatMap=new HashMap();
                  chatMap.put(Common.TIME_STAMP, ServerValue.TIMESTAMP);
                  chatMap.put(Common.UNREAD_COUNT,Integer.valueOf(currentcount)+1);
                  chatMap.put(Common.LAST_MESSAGE,lastMessage);
                  chatMap.put(Common.LAST_MESSAGE_TIME,ServerValue.TIMESTAMP);

                  Map chatUserMAp=new HashMap();
                  chatUserMAp.put(Common.CHAT+"/"+ChatUserId+"/"+CurrentUserId,chatMap);

                  rootRef.updateChildren(chatUserMAp, new DatabaseReference.CompletionListener() {
                      @Override
                      public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                          if(error!=null)
                          {
                              Toast.makeText(context, context.getString(R.string.Someting_went_wrong,error.getMessage()), Toast.LENGTH_SHORT).show();
                          }else
                          {
                              Toast.makeText(context, "user count increase", Toast.LENGTH_SHORT).show();
                          }

                      }
                  });
              }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {
              Toast.makeText(context, context.getString(R.string.Someting_went_wrong,error.getMessage()), Toast.LENGTH_SHORT).show();

          }
      });
  }
  public static String timeAgo(long time)
  {
      int SECOND_MILI=1000;
      int MINUTE_MILI=SECOND_MILI*60;
      int HOUR_MILI=MINUTE_MILI*60;
      int DAY_MILI=24*HOUR_MILI;

      time *=1000;

      long now=System.currentTimeMillis();
      final long diff=now-time;
      if(time>now||time<=0)
      {
          return "now";
      }

      if(diff<MINUTE_MILI)
      {
          return "just now";
      }else if(diff<2*MINUTE_MILI)
          return "a minute ago";
      else if(diff<59*MINUTE_MILI)
          return diff/MINUTE_MILI+ " minute ago";

      else if(diff<90*MINUTE_MILI)
          return "an hour ago";
      else if(diff<24*HOUR_MILI)
          return diff/HOUR_MILI+" hours ago";
      else if(diff<48*HOUR_MILI)
          return "yesterday";
      else
          return diff/DAY_MILI+" days ago";


  }
}
