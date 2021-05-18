package com.msah.insight.adapters;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.msah.insight.MainActivity;
import com.msah.insight.R;
import com.msah.insight.activities.NotesActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class NotesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<String> notesTitles = new ArrayList<>();
    Context context;
    private static final int PERMISSION_REQUEST_CODE = 1000;
    File directory,tutorial;
    File[] noteList;




    public NotesAdapter(Context context) {
        this.notesTitles = notesTitles;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item,
                parent,false);



        return new NotesAdapter.NoteHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        String fileName = noteList[position].getName().replace(".html", "");
        ((NotesAdapter.NoteHolder) holder).noteName.setText(fileName);

        ((NotesAdapter.NoteHolder) holder).description.setText(directory.getAbsolutePath());
        // Glide.with(context).load(R.drawable.ic_baseline_account_circle_24).into(((NotesAdapter.NoteHolder) holder).noteImage);
    }

    @Override
    public int getItemCount() {
        notesTitles.clear();

        tutorial = new File(context.getExternalFilesDir(null) + File.separator + "Notes" + File.separator + "Tutorial.html");
        try {
            boolean isFileCreated =  tutorial.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        directory = new File(context.getExternalFilesDir(null) + File.separator + "Notes");

        noteList = directory.listFiles();

        for (int i = 0; i < noteList.length; i++){
            String fileName = noteList[i].getName().replace(".html", "");
            notesTitles.add(fileName);
        }

        return notesTitles.size();
    }

    private class NoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView noteImage;
        TextView noteName,description;

        public NoteHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            noteImage = itemView.findViewById(R.id.note_thumbnail);
            noteName = itemView.findViewById(R.id.note_name);
            description = itemView.findViewById(R.id.note_description);

        }


        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onClick(View v) {
            final Intent notesIntent = new Intent(context, NotesActivity.class);
            //Add data in the form of key/value pairs to the intent object by using putExtra()

            notesIntent.putExtra(NotesActivity.NOTE_TITLE, noteName.getText());

            // Start the target Activity with the intent object
            context.startActivity(notesIntent);

        }
    }

}





