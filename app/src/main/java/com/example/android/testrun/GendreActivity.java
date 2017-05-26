package com.example.android.testrun;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.testrun.Data.Artist;
import com.example.android.testrun.Data.Gendre;
import com.example.android.testrun.Data.ItemClickListener;
import com.example.android.testrun.Data.Likes;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GendreActivity extends AppCompatActivity {

    String genre;
    private RecyclerView recyclerView;
    RecyclerView rv;
    Random random;
    DatabaseReference reference, ref, reflik, refHits, refStar;
    ItemClickListener itemClickListener;
    String link = "http://pitchfork-cdn.s3.amazonaws.com/longform/381/streaming-2.png";
    FirebaseRecyclerAdapter<Gendre, FireViewHolder> fireAdapter;
    EditText search;
    SharedPreferences preferences;
    String userEmail;
    String[] links;
    CardView cardView;
    ProgressBar bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gendre);

        Intent intent = this.getIntent();
        genre = intent.getExtras().getString("Selected_Genre");

        bar = (ProgressBar) findViewById(R.id.progressBarGen);
        bar.setVisibility(View.VISIBLE);

        setTitle(genre);
        random = new Random();

        preferences = getSharedPreferences("UserEmail", 0);

        userEmail = preferences.getString("yourEmail", "like@name.com");

        //  search = (EditText)findViewById(R.id.editsearch);

        final String[] imagelink = new String[]{"https://cdn.pixabay.com/photo/2016/12/01/07/30/music-1874621_960_720.jpg", "https://cdn.pixabay.com/photo/2016/11/29/05/23/chrome-1867512_960_720.jpg"
                , "https://cdn.pixabay.com/photo/2016/11/23/00/58/brand-1851576_960_720.jpg", "https://cdn.pixabay.com/photo/2017/01/19/14/11/silhouette-1992390_960_720.jpg"};

        Collections.shuffle(Arrays.asList(imagelink));

        cardView = (CardView) findViewById(R.id.cardviewgenre);
        //Recycler view...............
        recyclerView = (RecyclerView) findViewById(R.id.gendresongs);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        //send a Query to the database
        reference = FirebaseDatabase.getInstance().getReference("Genre").child(genre);
//       reflik.push().setValue(new Likes("like@name.com"));

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    Toast.makeText(GendreActivity.this, "No songs for this genre", Toast.LENGTH_LONG).show();
                    bar.setVisibility(View.GONE);
                } else {
                    bar.setVisibility(View.GONE);
                    fireAdapter = new FirebaseRecyclerAdapter<Gendre, FireViewHolder>(Gendre.class, R.layout.gendre_cardview, FireViewHolder.class, reference) {


                        @Override
                        protected void populateViewHolder(final FireViewHolder viewHolder, final Gendre model, final int position) {
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


                            //set ratebar if already rated
                            refStar = FirebaseDatabase.getInstance().getReference("Likes").child(model.getTimestamp());
                            final Query query = refStar.orderByChild("email").equalTo(userEmail);

                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.getValue() != null) {
                                        viewHolder.imageButton.setRating(Float.parseFloat("1.0"));
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            ////////////////


                            links = new String[]{Integer.toString(position)}; //put picture likes in array
                            viewHolder.setTextSongName(model.getSongName());
                            int num = random.nextInt(3) + 1;
                            viewHolder.setImage(imagelink[num]);

                            viewHolder.setArtist(model.getArtist());


                            viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Intent intent = new Intent(GendreActivity.this, Comment.class);
                                    intent.putExtra("Songtitle", model.getSongName());
                                    intent.putExtra("Path", model.getDownloadu());
                                    intent.putExtra("Times", model.getTimestamp());
                                    startActivity(intent);
                                }
                            });


                            viewHolder.imageButton.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

                                @Override
                                public void onRatingChanged(RatingBar ratingBar, float rating,
                                                            boolean fromUser) {
                                    // TODO Auto-generated method stub

                                    if (rating > 0) {
                                        reflik = FirebaseDatabase.getInstance().getReference("Likes").child(model.getTimestamp());

                                        // reflik.child(model.getTimestamp());

                                        final Query query = reflik.orderByChild("email").equalTo(userEmail);

                                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                if (dataSnapshot.getValue() == null) {
                                                    reflik.push().setValue(new Likes(userEmail));
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });


                                    }
                                    if (rating < 1) {
                                        reflik = FirebaseDatabase.getInstance().getReference("Likes").child(model.getTimestamp());
                                        final Query query = reflik.orderByChild("email").equalTo(userEmail);

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

                                }

                            });


                        }


                    };


                    recyclerView.setAdapter(fireAdapter);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                //   Toast.makeText(GendreActivity.this,"Please load data",Toast.LENGTH_LONG).show();

            }
        });


    }


    //Castom view holder to make interactions for each card
    public static class FireViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        CardView cardView;
        RatingBar imageButton;
        Uri streamline;


        public FireViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            cardView = (CardView) itemView.findViewById(R.id.cardviewgenre);
            streamline = Uri.parse("");
            imageButton = (RatingBar) itemView.findViewById(R.id.likeid);


        }


        public void setTextSongName(String name) {


            TextView tvSongName = (TextView) mView.findViewById(R.id.songhereid);
            tvSongName.setText("SONG: " + name);
        }

        public void setImage(String uri) {
            ImageView albumpic = (ImageView) itemView.findViewById(R.id.imagecover);
            Picasso.with(mView.getContext()).load(uri).into(albumpic);


        }

        public void setComments(String num) {
            if (num == null) {
                num = "0";
            }
            TextView textCom = (TextView) itemView.findViewById(R.id.numcomid);
            textCom.setText("COMMENTS : " + num);
        }

        public void setArtist(String num) {
            TextView textCom = (TextView) itemView.findViewById(R.id.artstnameId);
            textCom.setText("ARTIST :" + num);
        }

        public void setHits(String num) {
            TextView texthit = (TextView) itemView.findViewById(R.id.hitsid);
            texthit.setText("HITS : " + num);
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed(); // optional //you may put your intent here, putExtra, startActivity
        Intent intent = new Intent(GendreActivity.this, GalleryMusic.class);
        startActivity(intent);
        // finish();
    }


}
