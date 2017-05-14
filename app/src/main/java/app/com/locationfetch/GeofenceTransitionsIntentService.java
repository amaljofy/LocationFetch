/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.com.locationfetch;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceTransitionsIntentService extends IntentService {
    private static final String TAG = "fenceTransition";


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GeofenceTransitionsIntentService(String name) {
        super(name);
    }

    public GeofenceTransitionsIntentService() {
        super("GeofenceTransitionsIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, "Service started");
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }
        Log.e(TAG, "Event no errors");

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ) {
            Log.e(TAG, "Transition confirmed");
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            // Create a detail message with Geofences received

            sendNotification("ENTER INSIDE");
            Log.e(TAG," : "+triggeringGeofences.get(0).getRequestId());



        }
        else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)
        {
            sendNotification("EXIT OUTSIDE");
        }
        else {

            Log.e(TAG, "Error handling trigger");
        }
    }


    private void sendNotification(String s)
    {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);


        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Location Push")
                .setContentText(s)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }


}