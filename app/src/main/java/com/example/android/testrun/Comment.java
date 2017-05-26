package com.example.android.testrun;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.test.espresso.core.deps.guava.io.Files;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.android.testrun.Data.Artist;
import com.example.android.testrun.Data.CommentClass;
import com.example.android.testrun.Data.MotherClass;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Comment extends AppCompatActivity {

    public MediaPlayer mediaplayer;
    FirebaseRecyclerAdapter<CommentClass, Comment.FireViewHolder> fireAdapter;
    public String link, Timess, username;
    DatabaseReference reference;
    ImageButton downloadbutton;
    ImageButton play;
    ImageButton coomment;
    FirebaseDatabase database;
    SharedPreferences preferences;
    final Context context = this;
    private RecyclerView recyclerView;
    MotherClass motherClass;
    ToggleButton toggleButton;
    FrameLayout frameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

//
//        frameLayout = (FrameLayout)findViewById(R.id.frameID);
//        frameLayout.setVisibility(View.INVISIBLE);
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);

        mediaplayer = new MediaPlayer();


//        final TextView backgroundOne = (TextView) findViewById(R.id.background_one);
//        final TextView backgroundTwo = (TextView) findViewById(R.id.background_two);
//
//        final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
//        animator.setRepeatCount(ValueAnimator.INFINITE);
//        animator.setInterpolator(new LinearInterpolator());
//        animator.setDuration(10000L);
//        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                final float progress = (float) animation.getAnimatedValue();
//                final float width = backgroundOne.getWidth();
//                final float translationX = width * progress;
//                backgroundOne.setTranslationX(translationX);
//                backgroundTwo.setTranslationX(translationX - width);
//            }
//        });
//        animator.start();


        play = (ImageButton) findViewById(R.id.plycom);

        database = FirebaseDatabase.getInstance();

        preferences = getSharedPreferences("User", 0);
        username = preferences.getString("username", "not answered");


        downloadbutton = (ImageButton) findViewById(R.id.downloadbtn);

        coomment = (ImageButton) findViewById(R.id.commentbtn);

        // /
        //Recycler view...............
        recyclerView = (RecyclerView) findViewById(R.id.commentrecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        motherClass = new MotherClass();

        //get intent
        Intent intent = this.getIntent();
        link = intent.getExtras().getString("Path"); //for downloading
        Timess = intent.getExtras().getString("Times");

        //send a Query to the database
        reference = FirebaseDatabase.getInstance().getReference("Comments").child(Timess);


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    Toast.makeText(Comment.this, "No comments", Toast.LENGTH_LONG).show();
                } else {
                    //     fireAdapter =  new FirebaseRecyclerAdapter<Gendre, GendreActivity.FireViewHolder>(Gendre.class,R.layout.gendre_cardview,GendreActivity.FireViewHolder.class,reference)
                    fireAdapter = new FirebaseRecyclerAdapter<CommentClass, Comment.FireViewHolder>(CommentClass.class, R.layout.comment_card_design, Comment.FireViewHolder.class, reference) {


                        @Override
                        protected void populateViewHolder(FireViewHolder viewHolder, CommentClass model, int position) {
                            String name = model.getUser();

                            if (name == username) {
                                name = "You";

                            }

                            viewHolder.setImage(model.getMessage());
                            viewHolder.setTextSongName(name);
                            viewHolder.setTime(model.getDate());

                        }

                    };

                    recyclerView.setAdapter(fireAdapter);
                    fireAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(Comment.this, "Please load data", Toast.LENGTH_LONG).show();

            }
        });

        //  fireAdapter.setHasStableIds(true);


        //BUTTONS____________________________________________________________________________________
        //download song
        downloadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                downloadbutton.startAnimation(myAnim);
                //open browswer options to download
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(link));
                startActivity(i);


            }
        });


        //play song
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                play.setSelected(!play.isSelected());


                try {


                    if (mediaplayer.isPlaying()) {

                        mediaplayer.pause();
                        play.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    } else {
                        mediaplayer.stop();
                        mediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaplayer.reset();
                        mediaplayer.setDataSource(link);
                        mediaplayer.prepare();
                        mediaplayer.start();
                        play.setImageResource(R.drawable.ic_pause_black_24dp);
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


        //comment
        coomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                coomment.startAnimation(myAnim);

                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.dialog_cooment, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        // edit text
//                                        result.setText(userInput.getText());
                                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                                        Date date = new Date();
                                        String time = String.valueOf(dateFormat.format(date));

                                        DatabaseReference ref = database.getReference();
                                        DatabaseReference usersRef = ref.child("Comments").child(Timess);
                                        usersRef.push().setValue(new CommentClass(username, userInput.getText().toString(), Timess, time));
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });


    }


/////____________________BUTTON END_______________________________________________________________

    //Castom view holder to make interactions for each card
    public static class FireViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        CardView cardView;
        TextView txtViewlike;
        TextView txtViewDislike;

        public FireViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            cardView = (CardView) itemView.findViewById(R.id.commentrecycler);


//            txtViewDislike = (TextView)itemView.findViewById(R.id.txtNamehere);
//            txtViewlike = (TextView)itemView.findViewById(R.id.txtNamehere);
        }


        public void setTextSongName(String name) {


            TextView usename = (TextView) mView.findViewById(R.id.txtNamehere);
            usename.setText(name);
        }

        public void setImage(String uri) {
            TextView usercom = (TextView) itemView.findViewById(R.id.commentsID);
            usercom.setText(uri);


        }

        public void setTime(String time) {
            TextView timedis = (TextView) itemView.findViewById(R.id.txtTime);
            timedis.setText(time);
        }


    }

}





