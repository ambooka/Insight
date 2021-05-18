package com.msah.insight.adapters;


import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.msah.insight.R;
import com.msah.insight.models.MessageModel;

import java.util.HashMap;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<com.msah.insight.adapters.MessageAdapter.ViewHolder>
{
    public static final int MSG_LEFT = 0;
    public static final int MSG_RIGHT = 1;
    private List<MessageModel> listChats;
    private Context context;
    private NavController navController;
    private FirebaseUser firebaseUser;
    private int index = 0;

    public MessageAdapter(List<MessageModel> listChats, Context context, NavController navController)
    {
        this.navController = navController ;
        this.listChats = listChats;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        if(viewType == MSG_RIGHT)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.sent_message_item, parent, false);
            return new com.msah.insight.adapters.MessageAdapter.ViewHolder(view);
        }
        View view = LayoutInflater.from(context).inflate(R.layout.received_message_item, parent, false);
        return new com.msah.insight.adapters.MessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        MessageModel chat = listChats.get(position);
        holder.show_msg.setText(chat.getMessage());

        holder.relativeLayout.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete Message");
            builder.setMessage("Are you sure to delete this message?");
            builder.setPositiveButton("Delete", (dialog, which) ->
            {
                deleteMessage(position);

            });
            builder.setNegativeButton("No", (dialog, which) -> {
                dialog.dismiss();
            });

            builder.create().show();

        });

        if(position == listChats.size()-1) {
            if (chat.isSeen())
            {
                holder.txtSeen.setText("Seen");
            }
            else
                holder.txtSeen.setText("Delivered");
        }
        else
            holder.txtSeen.setVisibility(View.GONE);

    }

    private void deleteMessage(int position)
    {

        String key;
        if(listChats.get(position).getSender().compareTo(listChats.get(position).getReceiver()) >0)
            key = listChats.get(position).getReceiver() +"eranbatya" + listChats.get(position).getSender();
        else
            key = listChats.get(position).getSender() +"eranbatya" + listChats.get(position).getReceiver();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats/"+key);
        reference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1: snapshot.getChildren()) {
                    if (index == position)
                    {
                        MessageModel chat = snapshot1.getValue(MessageModel.class);
                        if (chat.getSender().equals(firebaseUser.getUid()))
                        {
                            index = 0;
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("message", "This message was deleted...");
                            snapshot1.getRef().updateChildren(hashMap);
                            Toast.makeText(context, "Message deleted...", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(context, "You can delete only your messages...", Toast.LENGTH_SHORT).show();

                        break;

                    }

                    else
                        ++index;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return listChats.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView show_msg;
        private TextView txtSeen;
        private RelativeLayout relativeLayout;



        public ViewHolder(View view)
        {
            super(view);

            show_msg = view.findViewById(R.id.show_msg);
            txtSeen = view.findViewById(R.id.txt_seen);
            relativeLayout = view.findViewById(R.id.relative_item_chat);

        }
    }


    @Override
    public int getItemViewType(int position)
    {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(listChats.get(position).getSender().equals(firebaseUser.getUid()))
            return MSG_RIGHT;
        else
            return MSG_LEFT;
    }
}
