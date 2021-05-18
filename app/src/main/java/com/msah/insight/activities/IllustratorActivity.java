package com.msah.insight.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.msah.insight.R;
import com.msah.insight.adapters.UserAdapter;
import com.msah.insight.adapters.boardUsersAdapter;
import com.msah.insight.colorpicker.ColorPickerListener;
import com.msah.insight.colorpicker.ColorPickerView;
import com.msah.insight.models.UserModel;
import com.msah.insight.styles.toolbar.IToolbar;
import com.msah.insight.styles.toolitems.IToolItem;
import com.msah.insight.styles.toolitems.ToolItem_FontColor;
import com.msah.insight.styles.toolitems.ToolItem_Hr;
import com.msah.insight.utils.SyncedBoardManager;
import com.msah.insight.views.IllustratorView;
import com.msah.insight.views.Segment;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IllustratorActivity extends AppCompatActivity  {
    public static final int THUMBNAIL_SIZE = 256;

    private static final int COLOR_MENU_ID = Menu.FIRST;
    private static final int CLEAR_MENU_ID = COLOR_MENU_ID + 1;
    private static final int PIN_MENU_ID = CLEAR_MENU_ID + 1;
    public static final String TAG = "AndroidDrawing";

    private IllustratorView mDrawingView;
    private DatabaseReference mFirebaseRef; // Firebase base URL
    private DatabaseReference mMetadataRef;
    private DatabaseReference mSegmentsRef;
    private ValueEventListener mConnectedListener;
    private String mBoardId;
    private int mBoardWidth;
    private int mBoardHeight;
    LinearLayout containerMain;
    private ColorPickerView colorPickerView;

    private BottomSheetBehavior mBehavior;
    private View bottom_sheet;
    private ImageView showBottomSheet;
    ListView listView;
    ArrayList<String> listUsers;
    UserAdapter userAdapter;
    ArrayAdapter arrayAdapter;
    IToolbar illustratorToolbar;

    private Animation moveLeft;
    private Animation moveRight;

    FloatingActionButton clearFab;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_illustrator);
        containerMain = findViewById(R.id.container_main);
        CoordinatorLayout coordinatorLayout = findViewById(R.id.coordinator_main);
        moveLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_left);
        moveRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_right);


        clearFab = (FloatingActionButton)findViewById(R.id.clear_fab);


        clearFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawingView.removeListener();
                mDrawingView.clear();
                /* Only admins */
                mSegmentsRef.removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError != null) {
                            throw firebaseError.toException();
                        }
                    }
                });
            }
        });
        colorPickerView = (ColorPickerView)findViewById(R.id.rteColorPalette);
        colorPickerView.setColorPickerListener(new ColorPickerListener() {
            @Override
            public void onPickColor(int color) {
                Toast.makeText(getApplication(), "Color "+ color, Toast.LENGTH_SHORT).show();
                mDrawingView.drawRect();
                if(color == -1){
                    mDrawingView.setStrokeSize(30);
                }
                mDrawingView.setStrokeSize(10);
                mDrawingView.setColor(color);
            }
        });
        bottom_sheet = findViewById(R.id.simple_bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);
        listView = findViewById(R.id.bottom_sheet_list);
        showBottomSheet = findViewById(R.id.show_bottom_sheet);




        showBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    clearFab.startAnimation(moveLeft);
                    clearFab.setVisibility(View.VISIBLE);
                    mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    showBottomSheet.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24);
                    mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    clearFab.startAnimation(moveRight);
                    clearFab.setVisibility(View.GONE);


                }
            }

        });

        listUsers = new ArrayList<>();

        readUsers();

        Intent intent = getIntent();
       // final String url = intent.getStringExtra("FIREBASE_URL");
        final String boardId = intent.getStringExtra("BOARD_ID");
        mFirebaseRef = FirebaseDatabase.getInstance().getReference("Boards");
        mBoardId = boardId;
        mMetadataRef = mFirebaseRef.child("boardmetas").child(boardId);
        mSegmentsRef = mFirebaseRef.child("boardsegments").child(mBoardId);
        mMetadataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                if (mDrawingView != null) {
                    ((ViewGroup) mDrawingView.getParent()).removeView(mDrawingView);
                    mDrawingView.removeListener();
                    mDrawingView = null;
                }

                Map<String, Object> boardValues = (Map<String, Object>) dataSnapshot.getValue();

                if (boardValues != null && boardValues.get("width") != null && boardValues.get("height") != null) {
                  /// mBoardWidth = ((Long) boardValues.get("width")).intValue();
                  // mBoardHeight = ((Long) boardValues.get("height")).intValue();

                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    mBoardHeight = displayMetrics.heightPixels;
                    mBoardWidth = displayMetrics.widthPixels;

                    mDrawingView = new IllustratorView(IllustratorActivity.this, mFirebaseRef.child("boardsegments").child(boardId), mBoardWidth, mBoardHeight);
                    mDrawingView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT));
                    mDrawingView.setStrokeSize(10);
                    containerMain.addView(mDrawingView);
                   // containerMain.scrollTo(1000,1000);
                    IToolItem fontColor = new ToolItem_FontColor();
                    IToolItem hr = new ToolItem_Hr();


                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError firebaseError) {
                // No-op
            }
        });

        MaterialButtonToggleGroup materialButtonToggleGroup =
                findViewById(R.id.illustrator_toggle_group);

        materialButtonToggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    if (checkedId == R.id.btn_illustrator_toolbar) {
                        colorPickerView.setVisibility(View.VISIBLE);
                    }
                }if (!isChecked){
                    if(checkedId == R.id.btn_illustrator_toolbar) {
                        colorPickerView.setVisibility(View.GONE);
                    }
                    }
                }
        });
    }

    private  void readUsers()
    {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener()
        {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserModel user = snapshot.getValue(UserModel.class);
                    assert firebaseUser != null;
                    assert user != null;
                    if (!user.getId().equals(firebaseUser.getUid()))
                        listUsers.add(user.getUserName());

                }

                listView.setAdapter(new boardUsersAdapter(IllustratorActivity.this, listUsers) );


            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    @Override
    public void onStart() {
        super.onStart();
        // Set up a notification to let us know when we're connected or disconnected from the Firebase servers
        mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    Toast.makeText(IllustratorActivity.this, "Connected to Firebase", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(IllustratorActivity.this, "Disconnected from Firebase", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                // No-op
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        // Clean up our listener so we don't have it attached twice.
        mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        if (mDrawingView != null) {
            mDrawingView.removeListener();
        }
        this.updateThumbnail(mBoardWidth, mBoardHeight, mSegmentsRef, mMetadataRef);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // getMenuInflater().inflate(R.menu.menu_drawing, menu);

        menu.add(0, COLOR_MENU_ID, 0, "Color").setShortcut('3', 'c').setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, CLEAR_MENU_ID, 2, "Clear").setShortcut('5', 'x');
        menu.add(0, PIN_MENU_ID, 3, "Keep in sync").setShortcut('6', 's').setIcon(android.R.drawable.ic_lock_lock)
                .setCheckable(true).setChecked(SyncedBoardManager.isSynced(mBoardId));

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == COLOR_MENU_ID) {
            return true;
        } else if (item.getItemId() == CLEAR_MENU_ID) {
            mDrawingView.removeListener();
            mSegmentsRef.removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                    if (firebaseError != null) {
                        throw firebaseError.toException();
                    }
                    mDrawingView = new IllustratorView(IllustratorActivity.this, mFirebaseRef.child("boardsegments").child(mBoardId), mBoardWidth, mBoardHeight);
                    containerMain.addView(mDrawingView);
                    //mDrawingView.clear();
                }
            });

            return true;
        } else if (item.getItemId() == PIN_MENU_ID) {
            SyncedBoardManager.toggle(mFirebaseRef.child("boardsegments"), mBoardId);
            item.setChecked(SyncedBoardManager.isSynced(mBoardId));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public static void updateThumbnail(int boardWidth, int boardHeight, DatabaseReference segmentsRef, final DatabaseReference metadataRef) {
        final float scale = Math.min(1.0f * THUMBNAIL_SIZE / boardWidth, 1.0f * THUMBNAIL_SIZE / boardHeight);
        final Bitmap b = Bitmap.createBitmap(Math.round(boardWidth * scale), Math.round(boardHeight * scale), Bitmap.Config.ARGB_8888);
        final Canvas buffer = new Canvas(b);

        buffer.drawRect(0, 0, b.getWidth(), b.getHeight(), IllustratorView.paintFromColor(Color.WHITE, Paint.Style.FILL_AND_STROKE));
        Log.i(TAG, "Generating thumbnail of " + b.getWidth() + "x" + b.getHeight());

        segmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                for (DataSnapshot segmentSnapshot : dataSnapshot.getChildren()) {
                    Segment segment = segmentSnapshot.getValue(Segment.class);
                    buffer.drawPath(
                            IllustratorView.getPathForPoints(segment.getPoints(), scale),
                            IllustratorView.paintFromColor(segment.getColor())
                    );
                }
                String encoded = encodeToBase64(b);
                metadataRef.child("thumbnail").setValue(encoded, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError != null) {
                            Log.e(TAG, "Error updating thumbnail", firebaseError.toException());
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NotNull DatabaseError firebaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public static String encodeToBase64(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();

        return Base64.encodeToString(b, Base64.DEFAULT);

    }
    public static Bitmap decodeFromBase64(String input) throws IOException {
        byte[] decodedByte = Base64.decode(input, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
