package com.example.android.testrun;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.testrun.Data.CommentClass;
import com.example.android.testrun.Data.Contacts;
import com.example.android.testrun.Data.Gendre;
import com.example.android.testrun.Data.MotherClass;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ContactArtist extends AppCompatActivity {


    FirebaseRecyclerAdapter<Contacts, FireViewHolder> fireAdapter;
    private RecyclerView recyclerView;
    DatabaseReference reference;
    DatabaseReference ref;
    ProgressBar bar;
    Dialog builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_artist);

        builder = new Dialog(ContactArtist.this);

        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);

        bar = (ProgressBar) findViewById(R.id.progressBarCon);
        bar.setVisibility(View.VISIBLE);
        //Recycler view...............
        recyclerView = (RecyclerView) findViewById(R.id.contctRecyc);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //send a Query to the database
        reference = FirebaseDatabase.getInstance().getReference("Contacts").child("Artists");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    bar.setVisibility(View.GONE);
                    Toast.makeText(ContactArtist.this, "No Artists", Toast.LENGTH_LONG).show();
                } else {

                    bar.setVisibility(View.GONE);

                    //        fireAdapter =  new FirebaseRecyclerAdapter<Gendre, FireViewHolder>(Gendre.class,R.layout.gendre_cardview,FireViewHolder.class,reference)
                    fireAdapter = new FirebaseRecyclerAdapter<Contacts, FireViewHolder>(Contacts.class, R.layout.artists_cardview, FireViewHolder.class, reference) {


                        @Override
                        protected void populateViewHolder(final FireViewHolder viewHolder, final Contacts model, int position) {

                            viewHolder.setImage(model.getPictur());
                            viewHolder.setTextName(model.getArtist());

                            ref = FirebaseDatabase.getInstance().getReference("Artist").child(model.getArtist());
                            ref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    viewHolder.setSongNum("Songs : " + dataSnapshot.getChildrenCount());
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    final AlertDialog.Builder builder1 = new AlertDialog.Builder(ContactArtist.this);
                                    builder1.setMessage("Would you like to contact " + model.artist + " ?");
                                    builder1.setCancelable(true);
                                    builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            String mailto = "mailto:" + model.getEmail() +
                                                    "subject=" + Uri.encode("Music Alive user") +
                                                    "body=" + Uri.encode("Dear " + model.getArtist());

                                            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                                            emailIntent.setData(Uri.parse(mailto));

                                            try {
                                                startActivity(emailIntent);
                                            } catch (ActivityNotFoundException e) {
                                                //TODO: Handle case where no email app is available
                                            }


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


                            viewHolder.txtViewDislike.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(ContactArtist.this);

                                    builderSingle.setTitle("Song Lists");

                                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ContactArtist.this, android.R.layout.simple_list_item_1);
                                    ref = FirebaseDatabase.getInstance().getReference("Artist").child(model.getArtist());
                                    ref.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot qryshot : dataSnapshot.getChildren()) {
                                                arrayAdapter.add(qryshot.child("songName").getValue().toString()); //qryshot.getRef().removeValue();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                                    builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String strName = arrayAdapter.getItem(which);
                                            AlertDialog.Builder builderInner = new AlertDialog.Builder(ContactArtist.this);
                                            builderInner.setMessage(strName);
                                            builderInner.setTitle("Your Selected Item is");
                                            builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            //  builderInner.show();
                                        }
                                    });
                                    builderSingle.show();
                                }
                            });
                        }


                    };


                    recyclerView.setAdapter(fireAdapter);
                    fireAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(ContactArtist.this, "Please load data", Toast.LENGTH_LONG).show();

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
            cardView = (CardView) itemView.findViewById(R.id.cardArtCon);

            txtViewDislike = (TextView) itemView.findViewById(R.id.textView4);
//            txtViewlike = (TextView)itemView.findViewById(R.id.txtNamehere);
        }


        public void setTextName(String name) {


            TextView usename = (TextView) mView.findViewById(R.id.textView2);
            usename.setText(name);
        }

        public void setSongNum(String name) {


            TextView usename = (TextView) mView.findViewById(R.id.textView4);
            usename.setText(name);
        }

        public void setImage(String uri) {
            ImageView usercom = (ImageView) itemView.findViewById(R.id.ppId);
            Picasso.with(mView.getContext()).load(uri).into(usercom);


        }


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed(); // optional //you may put your intent here, putExtra, startActivity
        Intent intent = new Intent(ContactArtist.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
