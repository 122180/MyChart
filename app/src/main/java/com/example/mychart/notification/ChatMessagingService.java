package com.example.mychart.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.mychart.Login.Login;
import com.example.mychart.R;
import com.example.mychart.common.Common;
import com.example.mychart.common.Util;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class ChatMessagingService extends FirebaseMessagingService {


    @Override
    public void onNewToken(@NonNull String newToken) {
        super.onNewToken(newToken);
        Util.updateDeviseToken(this,newToken);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String title=remoteMessage.getData().get(Common.NOTIFICATION_TITTLE);
        String message=remoteMessage.getData().get(Common.NOTIFICATION_MESSAGE);
        Intent intentSendMessageActivity=new Intent(this, Login.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intentSendMessageActivity,PendingIntent.FLAG_ONE_SHOT);
        final NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Uri defaultSound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final NotificationCompat.Builder notificationbuilder;
        if(Build.VERSION.SDK_INT>=26)
        {
            NotificationChannel channel=new NotificationChannel(Common.CHANNEL_ID,Common.CHANNEL_NAME,NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(Common.DESC);
            notificationManager.createNotificationChannel(channel);
            notificationbuilder=new NotificationCompat.Builder(this,Common.CHANNEL_ID);
        }else
            notificationbuilder=new NotificationCompat.Builder(this);
        notificationbuilder.setSmallIcon(R.drawable.ic_bubble);
        notificationbuilder.setColor(getResources().getColor(R.color.colorPrimary));
        notificationbuilder.setContentTitle(title);
        notificationbuilder.setAutoCancel(true);
        notificationbuilder.setSound(defaultSound);
        notificationbuilder.setContentIntent(pendingIntent);
        if(message.startsWith("https://firebasestorage."))
        {
            try
            {
                final NotificationCompat.BigPictureStyle bigPictureStyle=new NotificationCompat.BigPictureStyle();
                Glide.with(this)
                        .asBitmap()
                        .load(message)
                        .into(new CustomTarget<Bitmap>(200,100) {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                bigPictureStyle.bigPicture(resource);
                                notificationbuilder.setStyle(bigPictureStyle);
                                notificationManager.notify(999, notificationbuilder.build());

                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });
            }catch (Exception e)
            {

                notificationbuilder.setContentText("new file received");
            }
        }

            else {
            notificationbuilder.setContentTitle(message);
            notificationManager.notify(999, notificationbuilder.build());
        }

    }
}
