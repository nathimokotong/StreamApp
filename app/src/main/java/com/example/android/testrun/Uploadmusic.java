package com.example.android.testrun;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.testrun.Data.MotherClass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Uploadmusic extends AppCompatActivity {

    final static String DB_URL = "https://console.firebase.google.com/project/testrun-75be8/database/data";
    private static final int PICKFILE_RESULT_CODE = 1001;
    private StorageReference mStorageRef;
    ImageButton openButton;
    ImageButton BTNplay;
    ImageButton upload;
    Button addnew, btnsongs;
    UploadTask uploadTask;
    TextView foundpath, test, uploadtxt;
    String songdisplay;
    String picture;
    String FilePath = "";
    private String[] mPath;
    private String[] mMusic;
    ProgressBar bar;
    String username;
    MotherClass motherClass;
    Uri douwnloadURI;
    public MediaPlayer mediaplayer;
    String cat;
    DatabaseReference db;
    ArrayList<String> songlist = new ArrayList<>();
    private FirebaseAuth.AuthStateListener mAuthListener;
    SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        BTNplay = (ImageButton) findViewById(R.id.button3);
        openButton = (ImageButton) findViewById(R.id.button2);
        upload = (ImageButton) findViewById(R.id.btnupload);
        bar = (ProgressBar) findViewById(R.id.progressBar2);
        foundpath = (TextView) findViewById(R.id.pathfound);
        uploadtxt = (TextView) findViewById(R.id.txtupload);
        addnew = (Button) findViewById(R.id.btnAddnew);
        btnsongs = (Button) findViewById(R.id.btnMySongz);
        test = (TextView) findViewById(R.id.txtAudioTest);

        test.setVisibility(View.GONE);
        bar.setVisibility(View.GONE);
        uploadtxt.setVisibility(View.GONE);
        BTNplay.setVisibility(View.GONE);
        upload.setVisibility(View.GONE);

        db = FirebaseDatabase.getInstance().getReference();

        mediaplayer = new MediaPlayer();

        motherClass = new MotherClass();


        preferences = getSharedPreferences("User", 0);

        username = preferences.getString("username", "not answered");
        preferences = getSharedPreferences("UserPhoto", 0);
        picture = preferences.getString("picklink", "https://cdn.pixabay.com/photo/2016/12/01/07/30/music-1874621_960_720.jpg");


//_____________________Button click
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/mpeg");
                startActivityForResult(intent, PICKFILE_RESULT_CODE);

                openButton.startAnimation(sideanim());
            }
        });


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                upload.startAnimation(sideanim());

                mStorageRef = FirebaseStorage.getInstance().getReference();

                Uri file = Uri.fromFile(new File(FilePath));
                StorageReference riversRef = mStorageRef.child(FilePath);

                uploadTask = riversRef.putFile(file);
                bar.setVisibility(View.VISIBLE);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Uploadmusic.this, "can not upload song", Toast.LENGTH_SHORT).show();
                        bar.setVisibility(View.GONE);
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        BTNplay.setVisibility(View.VISIBLE);
                        douwnloadURI = taskSnapshot.getDownloadUrl();
                        Toast.makeText(Uploadmusic.this, "Upload successful ", Toast.LENGTH_SHORT).show();
                        //     Toast.makeText(Uploadmusic.this, douwnloadURI.toString() , Toast.LENGTH_SHORT).show();
                        bar.setVisibility(View.GONE);
                        test.setVisibility(View.VISIBLE);
                        upload.setClickable(false);

                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        long time = System.currentTimeMillis();
                        String timsp = String.valueOf(time);
                        motherClass.writeCat(cat, songdisplay, douwnloadURI, timsp, username);
                        motherClass.writeArtist(user.getDisplayName(), user.getEmail(), songdisplay, douwnloadURI, timsp);
//                        motherClass.writeComments(songdisplay);

                        DatabaseReference database;
                        database = FirebaseDatabase.getInstance().getReference("Contacts").child("Artists");
                        final Query query = database.orderByChild("artist").equalTo(username);

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.getValue() == null) {
                                    motherClass.contactInfo(username, user.getEmail(), picture);

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                });

            }
        });

        //Streaming the song
        BTNplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                BTNplay.setSelected(!BTNplay.isSelected());


                try {


                    if (mediaplayer.isPlaying()) {

                        mediaplayer.pause();
                        BTNplay.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    } else {
                        mediaplayer.stop();
                        mediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaplayer.reset();
                        mediaplayer.setDataSource(String.valueOf(douwnloadURI));
                        mediaplayer.prepare();
                        mediaplayer.start();
                        BTNplay.setImageResource(R.drawable.ic_pause_black_24dp);
                    }

                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (SecurityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }
        });


        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addnew.startAnimation(didTapButton());
                test.setVisibility(View.GONE);
                bar.setVisibility(View.GONE);
                BTNplay.setVisibility(View.GONE);
                upload.setVisibility(View.GONE);
                uploadtxt.setVisibility(View.GONE);
                openButton.setClickable(true);
                upload.setClickable(false);
                songdisplay = "";

            }
        });

        //displaying songs
        btnsongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btnsongs.startAnimation(didTapButton());
                // mySongs();

                Intent intent = new Intent(Uploadmusic.this, Artistmenu.class);
                startActivity(intent);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
// TODO Auto-generated method stub

        try {
            Uri selectedImageUri = data.getData();
            //open a select gallery to choose file to be uploaded
            String[] projection = {MediaStore.Audio.Media.DISPLAY_NAME};
            switch (requestCode) {
                case PICKFILE_RESULT_CODE:

                    if (resultCode == RESULT_OK) {

                        setCat();
                        Cursor cursor = getContentResolver().query(selectedImageUri, projection, null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(projection[0]);
                        String picturePath = cursor.getString(columnIndex);
                        cursor.close();
                        FilePath = getFirebaseURIparth(picturePath);
                        openButton.setClickable(false);
                        upload.setVisibility(View.VISIBLE);
                        uploadtxt.setVisibility(View.VISIBLE);
                        upload.setClickable(true);
                    }
                    if (requestCode == RESULT_CANCELED) {

                        upload.setVisibility(View.GONE);
                        //refresh activity if backpress

                    }
                    break;

            }


        } catch (Exception e) {

        }


    }


    //get all the songs Display names
    private String[] getAudioList() {
        final Cursor mCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA}, null, null
                , "LOWER(" + MediaStore.Audio.Media.TITLE + ")ASC");

        int count = mCursor.getCount();
        String[] songs = new String[count];
        String[] mAudioparth = new String[count];

        int i = 0;
        if (mCursor.moveToFirst()) {
            do {
                songs[i] = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                mAudioparth[i] = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                i++;
            } while (mCursor.moveToNext());

        }
        mCursor.close();
        return songs;

    }

    //Get all paths of songs from phone
    private String[] getmAudioPath() {
        final Cursor mCursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA}, null, null,
                "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC");

        int count = mCursor.getCount();

        String[] songs = new String[count];
        String[] path = new String[count];
        int i = 0;
        if (mCursor.moveToFirst()) {
            do {
                songs[i] = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                path[i] = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                i++;
            } while (mCursor.moveToNext());
        }

        mCursor.close();

        return path;
    }


    //how to get path to store file into firebase
    public String getFirebaseURIparth(String songname) {
        String uriPath = "null";

        mPath = getmAudioPath();
        mMusic = getAudioList();
        int i = mMusic.length;

        for (int k = 0; k < i; k++) {
            if (mMusic[k].equals(songname)) {
                uriPath = mPath[k].toString();
                Toast.makeText(this, "Song selected", Toast.LENGTH_SHORT).show();
                songdisplay = songname;
                blink();
                break;
            }
        }

        return uriPath;
    }


    //display category for user to select
    private void setCat() {
        final String[] names = {"Kwaito", "Hip Hop", "RnB", "House", "Jazz and Soul", "Mgqashiyo and Isicathamiya", "Rock", "Reggae", "Afrikaans music", "Gospel", "traditional"};


        final AlertDialog.Builder builder1 = new AlertDialog.Builder(Uploadmusic.this);
        builder1.setTitle("Genre");
        builder1.setItems(names, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                cat = names[i];

            }
        });

        builder1.show();
    }


    //----------------------------------------------ANIMATION------------------------------------------
    //button animation
    private Animation sideanim() {
        TranslateAnimation animation = new TranslateAnimation(0.0f, 400.0f,
                0.0f, 0.0f);          //  new TranslateAnimation(xFrom,xTo, yFrom,yTo)
        animation.setDuration(5000);  // animation duration
        animation.setRepeatCount(5);  // animation repeat count
        animation.setRepeatMode(2);   // repeat animation (left to right, right to left )
        //animation.setFillAfter(true);

        Animation alpha = AnimationUtils.loadAnimation(this, R.anim.left_to_right_slide);
        alpha.setDuration(600);
        alpha.setFillAfter(true);

        return alpha;
    }

    private void blink() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 1000;    //in milissegunds
                try {
                    Thread.sleep(timeToBlink);
                } catch (Exception e) {
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        TextView txt = (TextView) findViewById(R.id.songdisplay);
                        txt.setText(songdisplay);
                        if (txt.getVisibility() == View.VISIBLE) {
                            txt.setVisibility(View.INVISIBLE);
                        } else {
                            txt.setVisibility(View.VISIBLE);
                        }
                        blink();
                    }
                });
            }
        }).start();
    }


//_______________________Retrieve users songs


    //_____________________animation______________________
    public Animation didTapButton() {


        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);

        return myAnim;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed(); // optional //you may put your intent here, putExtra, startActivity
        Intent intent = new Intent(Uploadmusic.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}


