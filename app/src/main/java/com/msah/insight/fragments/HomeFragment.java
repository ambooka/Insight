package com.msah.insight.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayoutMediator;
import com.msah.insight.activities.NotesActivity;
import com.msah.insight.R;
import com.msah.insight.adapters.ViewPagerAdapter;
import com.msah.insight.models.MessageModel;
import com.msah.insight.models.UserModel;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment
{

    private FirebaseUser firebaseUser;
    private BadgeDrawable badgeWithNumber;
    private FloatingActionButton addClass;
    private FloatingActionButton addNote;
    private FloatingActionButton addSolution;
    ViewPagerAdapter viewPagerAdapter;


    @Override
    public void onStart() {
        super.onStart();

    }

    public HomeFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        View view  = inflater.inflate(R.layout.fragment_home, container, false);
        Toolbar toolbar =  view.findViewById(R.id.toolbar_home);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        return view;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = NavHostFragment.findNavController(this);
        FloatingActionButton contacts = view.findViewById(R.id.add_chat);
        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                navController.navigate(R.id.action_home_page_to_users);
            }
        });
        createFragments(view);
        storage();

        dbConnection(view);

    }

    private void storage() {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    private void createFragments(View view )
    {

        TabLayout tabLayout = view.findViewById(R.id.home_tabs);
        ViewPager2 viewPager =view.findViewById(R.id.home_viewpager);
     // ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(((getActivity())).getSupportFragmentManager());
         viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        tab.setText("Tab " + (position + 1));
                        switch (position){
                            case 0:
                                tab.setText("Class");
                                break;
                            case 1:
                                tab.setText("Notes");
                                break;
                            case 2:
                                tab.setText("Boards");
                                break;
                        }
                    }
                }).attach();

        badgeWithNumber = Objects.requireNonNull(tabLayout.getTabAt(0)).getOrCreateBadge();
        badgeWithNumber.setVisible(false);
        addClass = view.findViewById(R.id.add_chat);
        addNote = view.findViewById(R.id.add_note);
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Add note title");

// Set up the input
                final EditText input = new EditText(getContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setHint("Notes title");
                builder.setView(input);

// Set up the buttons
                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String    noteTitle = input.getText().toString();
                        if (TextUtils.isEmpty(noteTitle)){
                            Snackbar.make(view, "Invalid title", Snackbar.LENGTH_SHORT).show();
                        }


                        Intent notesIntent = new Intent(getContext(), NotesActivity.class);
                        notesIntent.putExtra(NotesActivity.NOTE_TITLE, noteTitle);
                        startActivity(notesIntent);

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

            }
        });
        addSolution = (FloatingActionButton)view.findViewById(R.id.add_solution);
        addSolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0){
                    addClass.show();
                    addNote.hide();
                    addSolution.hide();
                }
                else if(tab.getPosition() == 1){
                    addClass.hide();
                    addNote.show();
                    addSolution.hide();
                }
                else if(tab.getPosition() == 2){
                    addSolution.show();
                    addClass.hide();
                    addNote.hide();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if(getActivity() == null)
        {
            return;
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(getActivity()==null||firebaseUser==null)
                    return;
                int unread = 0;
                for(DataSnapshot snapshot1: snapshot.getChildren())
                    for(DataSnapshot snapshot2: snapshot1.getChildren())
                    {
                        MessageModel chat = snapshot2.getValue(MessageModel.class);
                        assert chat != null;
                        if(chat.getReceiver().equals(firebaseUser.getUid())
                                && !chat.isSeen() )
                            unread++;

                    }
                if(unread == 0)
                    badgeWithNumber.setVisible(false);
                else
                    badgeWithNumber.setNumber(unread);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void dbConnection(View view)
    {
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
                createFragments(view);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void status(String status) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser== null)
            return;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap = new HashMap();
        hashMap.put("status",status);

        reference.updateChildren(hashMap);

    }

    @Override
    public void onPause() {
        super.onPause();
        status("offline");
   }

    @Override
    public void onDestroy() {
        super.onDestroy();
        status("offline");
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
