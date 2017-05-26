package com.example.android.testrun;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.testrun.Data.Artist;
import com.example.android.testrun.Data.ItemClickListener;
import com.example.android.testrun.FirebaseRecycle.FirebaseHelper;
import com.example.android.testrun.FirebaseRecycle.MyAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;


public class Artistmenu extends AppCompatActivity {

    private RecyclerView recyclerView;
    RecyclerView rv;
    DatabaseReference reference;
    DatabaseReference ref, refHits;
    String reftest;
    ItemClickListener itemClickListener;
    String link = "https://cdn.pixabay.com/photo/2016/12/01/07/30/music-1874621_960_720.jpg"; //"http://pitchfork-cdn.s3.amazonaws.com/longform/381/streaming-2.png"
    FirebaseRecyclerAdapter<Artist, FireViewHolder> fireAdapter; //https://cdn.pixabay.com/photo/2016/11/29/05/23/chrome-1867512_960_720.jpg
    String streamlink; //https://cdn.pixabay.com/photo/2016/11/23/00/58/brand-1851576_960_720.jpg
    ImageButton streambtn;  //https://cdn.pixabay.com/photo/2017/01/19/14/11/silhouette-1992390_960_720.jpg
    String username;
    SharedPreferences preferences;
    String[] links;
    CardView cardView;
    boolean isplaying = false;
    private MediaPlayer mediaPlayer;
    private TelephonyManager telephonyManager;
    private PhoneStateListener phoneStateListener;
    private boolean ongoingCall = false;
    ProgressBar bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artistmenu);
        mediaPlayer = new MediaPlayer();
        callStateListener();

        bar = (ProgressBar) findViewById(R.id.progressBarArt);
        bar.setVisibility(View.VISIBLE);

        //get users name from shared preference
        preferences = getSharedPreferences("User", 0);

        username = preferences.getString("username", "not answered");

        final String[] imagelink = new String[]{"https://cdn.pixabay.com/photo/2016/12/01/07/30/music-1874621_960_720.jpg", "https://cdn.pixabay.com/photo/2016/11/29/05/23/chrome-1867512_960_720.jpg"
                , "https://cdn.pixabay.com/photo/2016/11/23/00/58/brand-1851576_960_720.jpg", "https://cdn.pixabay.com/photo/2017/01/19/14/11/silhouette-1992390_960_720.jpg"};

        Collections.shuffle(Arrays.asList(imagelink));

        cardView = (CardView) findViewById(R.id.cardview);
        //Recycler view...............
        recyclerView = (RecyclerView) findViewById(R.id.recycview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //send a Query to the database
        reference = FirebaseDatabase.getInstance().getReference("Artist").child(username);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    bar.setVisibility(View.GONE);
                    Toast.makeText(Artistmenu.this, "No Data to display, add song(s)", Toast.LENGTH_LONG).show();
                } else {
                    bar.setVisibility(View.GONE);
                    fireAdapter = new FirebaseRecyclerAdapter<Artist, FireViewHolder>(Artist.class, R.layout.design_row_cardview, FireViewHolder.class, reference) {

                        @Override
                        protected void populateViewHolder(final FireViewHolder viewHolder, final Artist model, final int position) {


                            //Get number of comments
                            ref = FirebaseDatabase.getInstance().getReference("Comments").child(model.getTimestamp());
                            ref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    viewHolder.setComments("" + dataSnapshot.getChildrenCount());

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            //Get number of hits
                            refHits = FirebaseDatabase.getInstance().getReference("Likes").child(model.getTimestamp());
                            refHits.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    viewHolder.setHits("" + dataSnapshot.getChildrenCount());

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            ;
                            Random random = new Random();
                            links = new String[]{Integer.toString(position)};
                            viewHolder.setTextSongName(model.getSongName());
                            int num = random.nextInt(3) + 1;
                            viewHolder.setImage(imagelink[num]);
                            // viewHolder.txtViewlike.setText(model.getLikes());
                            // viewHolder.txtViewDislike.setText(model.getLikes());


                            viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //
                                    Intent intent = new Intent(Artistmenu.this, Comment.class);
                                    intent.putExtra("Songtitle", model.getSongName());
                                    intent.putExtra("Path", model.getDownloadu());
                                    intent.putExtra("Times", model.getTimestamp());
                                    startActivity(intent);


                                }
                            });

                            //delete from database , prompt with dialog
                            viewHolder.imageButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {


                                    final AlertDialog.Builder builder1 = new AlertDialog.Builder(Artistmenu.this);
                                    builder1.setMessage("Are you sure you want to delete this song?");
                                    builder1.setCancelable(true);
                                    builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            deleteArt(reference, model.getTimestamp());
                                            deleteGen(model.getTimestamp());
                                            deleteCom(model.getTimestamp());

                                        }
                                    });

                                    builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });

                                    builder1.show();


                                }
                            });

                        }
                    };

                    recyclerView.setAdapter(fireAdapter);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(Artistmenu.this, "Please load data", Toast.LENGTH_LONG).show();

            }
        });


    }


    //Castom view holder to make interactions for each card
    public static class FireViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        CardView cardView;
        Uri streamline;
        ImageButton imageButton;
        TextView songshit;

        public FireViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            cardView = (CardView) itemView.findViewById(R.id.cardview);
            streamline = Uri.parse("");
            imageButton = (ImageButton) itemView.findViewById(R.id.songDelete);

        }


        public void setTextSongName(String name) {


            TextView tvSongName = (TextView) mView.findViewById(R.id.songnameID);
            tvSongName.setText(name);
        }

        public void setImage(String uri) {
            ImageView albumpic = (ImageView) itemView.findViewById(R.id.alubcover);
            Picasso.with(mView.getContext()).load(uri).into(albumpic);


        }

        public void setComments(String num) {
            TextView textCom = (TextView) itemView.findViewById(R.id.commntsdisplay);
            textCom.setText("Comments : " + num);
        }

        public void setHits(String num) {
            TextView texthit = (TextView) itemView.findViewById(R.id.songHits);
            texthit.setText("Hits : " + num);
        }


    }

    //when a call is received
//when someone calls
    private void callStateListener() {
        // Get the telephony manager
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //Starting listening for PhoneState changes
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    //if at least one call exists or the phone is ringing
                    //pause the MediaPlayer
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mediaPlayer != null) {
                            mediaPlayer.pause();
                            ongoingCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        // Phone idle. Start playing.
                        if (mediaPlayer != null) {
                            if (ongoingCall) {
                                ongoingCall = false;
                                mediaPlayer.start();
                            }
                        }
                        break;
                }
            }
        };
        // Register the listener with the telephony manager
        // Listen for changes to the device call state.
        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
    }

    //___________________________Delete song
    public void useroption() {


    }

    //______________________________delete items

    //under user
    public void deleteArt(DatabaseReference database, String fild) {
        final Query query = database.orderByChild("timestamp").equalTo(fild);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot qryshot : dataSnapshot.getChildren()) {
                    qryshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //under comments
    public void deleteCom(String fild) {
        DatabaseReference database;
        database = FirebaseDatabase.getInstance().getReference("Comments").child(fild);
        final Query query = database.orderByChild("timestamp").equalTo(fild);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot qryshot : dataSnapshot.getChildren()) {
                    qryshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //under genre
    public void deleteGen(String fild) {
        DatabaseReference database;

        String[] names = new String[]{"Kwaito", "Hip Hop", "RnB", "House", "Jazz and Soul", "Mgqashiyo and Isicathamiya", "Rock", "Reggae", "Afrikaans music", "Gospel", "traditional"};


        // database =  database.child(names[i]);

        for (int i = 0; i < names.length; i++) {
            database = FirebaseDatabase.getInstance().getReference("Genre").child(names[i]);
            final Query query = database.orderByChild("timestamp").equalTo(fild);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot qryshot : dataSnapshot.getChildren()) {
                        qryshot.getRef().removeValue();

                        break;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


    }


}


//_____________________________________________________________________________

