package com.msah.insight;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.msah.insight.activities.SettingsActivity;
import com.msah.insight.fragments.QuizFragment;
import com.msah.insight.test.TestActivity;
import com.msah.insight.fragments.SolutionsFragment;
import com.msah.insight.utils.SharedPreference;
import com.msah.insight.utils.SyncedBoardManager;

import java.io.File;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static SharedPreference sharedPreference;
    File directory;
    private NavController navController;
    public static FragmentManager fragmentManager;
    public static Class fragClass;
    private static FirebaseDatabase firebaseDatabase;
    private FirebaseUser firebaseUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(firebaseDatabase==null){
            firebaseDatabase=FirebaseDatabase.getInstance();
            firebaseDatabase.setPersistenceEnabled(true);
        }
        SyncedBoardManager.setContext(this);
        setContentView(R.layout.activity_main);

      /**  if(savedInstanceState==null){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
       **/



        sharedPreference = new SharedPreference(this);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        directory = new File(this.getExternalFilesDir(null), "Notes");
        boolean workFolder = directory.mkdirs();
        fragmentManager = getSupportFragmentManager();
        navController = Navigation.findNavController(this,R.id.nav_host_fragment);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.settings:
                Intent settingsIntent =  new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;

            case R.id.logout:
                //sharedPreference.setUser("");
                //Intent test =  new Intent(this, com.msah.insight.test.TestQsns.class);
               // startActivity(test);
                navController.navigate(R.id.action_home_page_to_quiz);

                return true;
            case R.id.test:
               Intent test =  new Intent(this, TestActivity.class);
                startActivity(test);
            case R.id.editText_search:

        }
        return false;
    }

    @Override
    public void onBackPressed()
    {

        int currentDestinationId = navController.getCurrentDestination().getId();

        if(currentDestinationId==R.id.Login)
        {
            finish();
            return;
        }
        if(currentDestinationId == R.id.homePage
                ||currentDestinationId == navController.getGraph().getStartDestination()) //if we state in homeFragment-back button affect to finish app
        {
            setStatus();
            moveTaskToBack(true);
            return;
        }

        if(currentDestinationId == R.id.msgFragment)
        {
            // back to pre fra+gment
            navController.navigate(R.id.action_msg_to_homeFragment);
             refreshFragment();
            sharedPreference.setUser("");
            return;
        }
        if(currentDestinationId == R.id.usersFragment)
        {
            // back to pre fragment
            refreshFragment();
            sharedPreference.setUser("");
            navController.navigate(R.id.action_users_to_homeFragment);
            return;

        }
        navController.popBackStack();
    }
    private void refreshFragment()
    {
        int currFragmentId = navController.getCurrentDestination().getId();
        NavOptions navOptions = new NavOptions.Builder()
                .setLaunchSingleTop(true)
                .build();
        navController.navigate(currFragmentId, null, navOptions);// refresh to same frag
    }


    @Override
    protected void onStart()
    {
        super.onStart();
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {// controoller- nvaController , dest- the locate that i am here on map,arg - like put extra on activity
        });
    }
    private Task<Void> setStatus()
    {
        HashMap<String, Object> hashMap = new HashMap();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // task is like promise
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid());
        hashMap.put("status", "offline");

        return reference.updateChildren(hashMap);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}