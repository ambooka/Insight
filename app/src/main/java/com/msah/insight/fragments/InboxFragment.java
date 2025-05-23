package com.msah.insight.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.msah.insight.Interfaces.APIService;
import com.msah.insight.MainActivity;
import com.msah.insight.R;
import com.msah.insight.adapters.MessageAdapter;
import com.msah.insight.models.MessageModel;
import com.msah.insight.models.UserModel;
import com.msah.insight.notifications.Client;
import com.msah.insight.notifications.Data;
import com.msah.insight.notifications.MyResponse;
import com.msah.insight.notifications.Sender;
import com.msah.insight.notifications.Token;
import com.msah.insight.views.CircleImageView;
import com.msah.insight.views.IllustratorView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
/**
 * A simple {@link Fragment} subclass.
 */
public class InboxFragment extends Fragment {
    private CircleImageView profileImage;
    private TextView userName;
    private FirebaseUser firebaseUser;
    private DatabaseReference ref;

    private HashMap<String, Object> hashMap = new HashMap<>();

    private ImageView mBtnSend, btnDraw;
    private EditText mTextSend;
    private List<MessageModel> chatList;
    private MessageAdapter messageAdapter;
    private RecyclerView recyclerView;

    private NavController navController;

    private APIService apiService;
    private boolean notify = false;

    private DatabaseReference referenceUser;


    public InboxFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inbox, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindWithXML(view);
        buildRecyclerView(view);
        toolBar(view);
        readUserFromDB();
        listeners();

    }



    private void buildRecyclerView(View view) {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void listeners() {



        mBtnSend.setOnClickListener(v -> {
            notify= true;
            String msg = mTextSend.getText().toString();
            if (!msg.equals("")) {
                sendMessage(firebaseUser.getUid()
                        , MainActivity.sharedPreference.getIdUser()
                        , msg);
            } else {
                Toast.makeText(getContext()
                        , "You cant send empty message",
                        Toast.LENGTH_SHORT).show();
            }

            mTextSend.setText("");
        });

        btnDraw.setOnClickListener(v ->{
          //  Intent drawIntent = new Intent(getContext(), IllustratorActivity.class);
          //  startActivity(drawIntent);
        });
    }

    private void readUserFromDB() {
        String userId = MainActivity.sharedPreference.getIdUser();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(getContext()==null)
                    return;

                UserModel user = snapshot.getValue(UserModel.class);
                userName.setText(user.getUserName());
                if (user.getImageURL().equals("default"))
                    profileImage.setImageResource(R.mipmap.ic_launcher);
                else
                    Glide.with(getContext()).load(user.getImageURL()).into(profileImage);

                readMessage(userId, firebaseUser.getUid());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        seenMessage(userId);

    }

    private void sendMessage(String sender, String receiver, String message)
    {
        String key ;
        if(sender.compareTo(receiver)>0)
            key = receiver+ "eranbatya"+sender;
        else
            key = sender+ "eranbatya"+receiver;


        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("seen", false);
        ref = FirebaseDatabase.getInstance().getReference("Chats/"+key);
        ref.push().setValue(hashMap);


        final String msg = message;
        ref= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel user = snapshot.getValue(UserModel.class);
                if(notify)
                    sendNotification(receiver,user.getUserName(),msg);
                notify = false;



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    private void sendNotification(String receiver, String userName, String msg)
    {
        String userId = MainActivity.sharedPreference.getIdUser();
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1: snapshot.getChildren())
                {
                    Token token = snapshot1.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, userName+ ": "+ msg,"New Message",userId);
                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code() == 200)
                                        if(response.body().success!= 1)
                                        {
                                            Toast.makeText(getContext(),"Failed", Toast.LENGTH_SHORT).show();
                                        }

                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void readMessage(final String myid, final String userid) {
        chatList = new ArrayList<>();

        ref = FirebaseDatabase.getInstance().getReference("Chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                        MessageModel chat = snapshot1.getValue(MessageModel.class);
                        if ((chat.getSender().equals(myid) && chat.getReceiver().equals(userid)
                                || (chat.getSender().equals(userid) && chat.getReceiver().equals(myid)))) {
                            chatList.add(chat);

                        }
                    }


                }
                messageAdapter = new MessageAdapter(chatList, getContext(), navController);
                recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void toolBar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_msg);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    private void bindWithXML(View view)
    {
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        navController = NavHostFragment.findNavController(this);
        profileImage = view.findViewById(R.id.profileImage_msg);
        userName = view.findViewById(R.id.username_msg);
        mBtnSend = view.findViewById(R.id.btnSend);
        btnDraw = view.findViewById(R.id.btn_draw);
        mTextSend = view.findViewById(R.id.textSend);
        recyclerView = view.findViewById(R.id.recyclerViewMsg);

    }

    private void status(String status) {

        ref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap = new HashMap();
        hashMap.put("status",status);
        ref.updateChildren(hashMap);

    }

    private void seenMessage(String userid){

        ref = FirebaseDatabase.getInstance().getReference("Chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren())
                    for(DataSnapshot snapshot2:snapshot1.getChildren())
                    {

                        MessageModel chat = snapshot2.getValue(MessageModel.class);

                        if(chat.getReceiver().equals(firebaseUser.getUid())&&
                                chat.getSender().equals(userid)  )
                        {
                            checkStatus(chat, snapshot2);
                        }
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void checkStatus(MessageModel chat, DataSnapshot snapshot2)
    {
        String idReciever = chat.getReceiver();
        referenceUser = FirebaseDatabase.getInstance().getReference("Users/"+idReciever);
        referenceUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                UserModel user = snapshot.getValue(UserModel.class);
                if(user.getStatus().equals("online"))
                {
                    snapshot2.getRef().child("seen").setValue(true);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




    @Override
    public void onResume() {
        super.onResume();
        status("online");

    }

    @Override
    public void onPause() {
        super.onPause();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

    }
}

