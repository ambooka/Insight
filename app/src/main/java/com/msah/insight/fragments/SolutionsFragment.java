package com.msah.insight.fragments;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.msah.insight.R;
import com.msah.insight.activities.IllustratorActivity;
import com.msah.insight.adapters.FirebaseListAdapter;
import com.msah.insight.utils.SyncedBoardManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SolutionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SolutionsFragment extends Fragment {

    public static final String TAG = "AndroidDrawing";
    private DatabaseReference mRef;
    private DatabaseReference mBoardsRef;
    private DatabaseReference mSegmentsRef;
    private FirebaseListAdapter<HashMap> mBoardListAdapter;
    private ValueEventListener mConnectedListener;
    private NavController navController;
    View view;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SolutionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SolutionsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SolutionsFragment newInstance(String param1, String param2) {
        SolutionsFragment fragment = new SolutionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*TODO:note it */
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRef = FirebaseDatabase.getInstance().getReference("Boards");
        mBoardsRef = mRef.child("boardmetas");
        mBoardsRef.keepSynced(true); // keep the board list in sync
        mSegmentsRef = mRef.child("boardsegments");
        SyncedBoardManager.restoreSyncedBoards(mSegmentsRef);
        view = inflater.inflate(R.layout.fragment_solutions, container, false);
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();

        // Set up a notification to let us know when we're connected or disconnected from the Firebase servers
        mConnectedListener = mRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    Toast.makeText(getContext(), "Connected to Firebase", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Disconnected from Firebase", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                // No-op
            }
        });
        final ListView boardList = (ListView) view.findViewById(R.id.BoardList);
        mBoardListAdapter = new FirebaseListAdapter<HashMap>(mBoardsRef, HashMap.class, R.layout.board_item, getActivity()) {
            @Override
            protected void populateView(View v, HashMap model) {
                final String key = mBoardListAdapter.getModelKey(model);
                ((TextView)v.findViewById(R.id.board_title)).setText(key);

                // show if the board is synced and listen for clicks to toggle that state
                CheckBox checkbox = (CheckBox) v.findViewById(R.id.keepSynced);
                checkbox.setChecked(SyncedBoardManager.isSynced(key));
                checkbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SyncedBoardManager.toggle(mSegmentsRef, key);
                    }
                });

                // display the board's thumbnail if it is available
                ImageView thumbnailView = (ImageView) v.findViewById(R.id.board_thumbnail);
                if (model.get("thumbnail") != null){
                    try {
                        thumbnailView.setImageBitmap(IllustratorActivity.decodeFromBase64(model.get("thumbnail").toString()));
                        thumbnailView.setVisibility(View.VISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    thumbnailView.setVisibility(View.INVISIBLE);
                }
            }
        };
        boardList.setAdapter(mBoardListAdapter);
        boardList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openBoard(mBoardListAdapter.getModelKey(position));
            }
        });
        mBoardListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                boardList.setSelection(mBoardListAdapter.getCount() - 1);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        // Clean up our listener so we don't have it attached twice.
        mRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        mBoardListAdapter.cleanup();

    }

    private void createBoard() {
        // create a new board
        final DatabaseReference newBoardRef = mBoardsRef.push();
        Map<String, Object> newBoardValues = new HashMap<>();
        newBoardValues.put("createdAt", ServerValue.TIMESTAMP);
        android.graphics.Point size = new android.graphics.Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        newBoardValues.put("width", size.x);
        newBoardValues.put("height", size.y);
        newBoardRef.setValue(newBoardValues, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError firebaseError, DatabaseReference ref) {
                if (firebaseError != null) {
                    Log.e(TAG, firebaseError.toString());
                    throw firebaseError.toException();
                } else {
                    // once the board is created, start a DrawingActivity on it
                    openBoard(newBoardRef.getKey());
                }
            }
        });
    }

    private void openBoard(String key) {
        Toast.makeText(getContext(), "Opening board: "+key, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getContext(), IllustratorActivity.class);
        intent.putExtra("BOARD_ID", key);
        startActivity(intent);

    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_board_list, menu);

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_new_board) {
            createBoard();
        }


        return super.onOptionsItemSelected(item);
    }

}
