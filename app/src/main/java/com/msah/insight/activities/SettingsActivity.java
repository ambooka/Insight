package com.msah.insight.activities;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceFragmentCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.msah.insight.R;
import com.msah.insight.models.UserModel;

import java.util.Objects;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {
    public static final String USER_NAME = "";

    UserModel user;
    String userName;
    FirebaseUser firebaseUser;
    ImageView profileImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_settings);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        profileImage = (ImageView)findViewById(R.id.img_profile);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser== null)
            return;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                UserModel user = snapshot.getValue(UserModel.class);
                if(user!=null)
                {
                    doWork(user);

                }

            }

            private void doWork(UserModel user)
            {
                updateName(user);
                updateImgURL(user);
            }

            private void updateImgURL(UserModel user)
            {
                if (SettingsActivity.this == null||getApplicationContext()==null)
                    return;
                if(user.getImageURL().equals("default"))
                    //profileImage.setImageResource(R.mipmap.ic_launcher);
                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profileImage);

            }

            private void updateName(UserModel user)
            {
                setSupportActionBar(toolbar);
                Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

                if(user.getUserName()==null)
                    getSupportActionBar().setTitle("");
                else
                    getSupportActionBar().setTitle(user.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });







            Toast.makeText(this, userName, Toast.LENGTH_LONG).show();
    }



    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }
}