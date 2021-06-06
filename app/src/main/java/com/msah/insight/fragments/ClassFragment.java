package com.msah.insight.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.MenuCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.msah.insight.R;
import com.msah.insight.adapters.UserAdapter;
import com.msah.insight.models.UserModel;
import com.msah.insight.notifications.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClassFragment extends Fragment
{
    private RecyclerView recyclerView;

    private UserAdapter userAdapter;
    private List<UserModel> mUsers; // forAdapter

    private Set<String> setUsersId;

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    private NavController navController;
    private LinearLayoutManager linearLayoutManager;





    public ClassFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*TODO:note it */
        setHasOptionsMenu(true);


    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);

        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.classes_fragment, container, false);


        buildRecyclerView(view);
        initialize();
        listener();
        tokenFunc();
        return view ;
    }

    private void tokenFunc(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        updateToken(token);


                        Toast.makeText(getContext(), token, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void listener()
    {

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                setUsersId.clear();
                for(DataSnapshot snapshot1: snapshot.getChildren())
                {

                    String key = snapshot1.getKey();
                    assert key != null;
                    String[] packet = key.split("eranbatya");
                    String sender = packet[0];
                    String receiver = packet[1];


                    if(sender.equals(firebaseUser.getUid())
                            ||receiver.equals(firebaseUser.getUid()))
                    {
                        setUsersId.add(sender);
                        setUsersId.add(receiver);
                    }

                }
                setUsersId.remove(firebaseUser.getUid());
                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readChats()
    {

        mUsers= new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {

                mUsers.clear();

                //usersList --array<String>
                for(DataSnapshot snapshot1 : snapshot.getChildren() )
                {
                    UserModel user = snapshot1.getValue(UserModel.class);// user from db
                    for(String id : setUsersId)
                    {
                        if(user.getId().equals(id))
                        {
                            if(mUsers.size()!=0 )
                            {
                                boolean contain = false;
                                for (int i=0; i < mUsers.size(); i++ )
                                {
                                    if(user.getId().equals(mUsers.get(i).getId()))
                                    {
                                        contain = true;
                                    }
                                }
                                if(!contain)
                                {
                                    mUsers.add(user);
                                }
                            }
                            else{
                                mUsers.add(user);
                            }

                        }
                    }
                }


                userAdapter= new UserAdapter(mUsers,true,getContext(),navController);
                recyclerView.setAdapter(userAdapter);
                linearLayoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setLayoutManager(linearLayoutManager);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void updateToken(String token)
    {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(firebaseUser.getUid()).setValue(token1);
    }

    private void initialize()
    {
        navController = NavHostFragment.findNavController(this);
        setUsersId = new HashSet<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
    }

    private void buildRecyclerView(View view)
    {
        recyclerView = view.findViewById(R.id.recyclerViewChat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {

        super.onPause();
    }

}
