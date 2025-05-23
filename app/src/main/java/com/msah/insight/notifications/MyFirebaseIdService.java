package com.msah.insight.notifications;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import org.jetbrains.annotations.NotNull;

public class MyFirebaseIdService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull @NotNull String s) {
        super.onNewToken(s);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        if(firebaseUser!=null)
                            updateToken(token);

                    }
                });
    }

    private void updateToken(String refreshToken)
    {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(refreshToken);
        reference.child(firebaseUser.getUid()).setValue(token);

    }
}
