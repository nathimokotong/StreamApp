package com.example.android.testrun;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.example.android.testrun.Data.Gendre;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MusicGallery extends AppCompatActivity {


    ExpandableListView expandableListView;
    List<String> headings;
    HashMap<String, List<String>> childlist;
    DatabaseReference reference;
    List<String> HipHop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_gallery);

        setTitle("Music Gallery");

        expandableListView = (ExpandableListView) findViewById(R.id.explistview);

        //preparing list data
        prepareList();

        MyexpendAdapter myexpendAdapter = new MyexpendAdapter(headings, childlist, this);

        //setting list adapter
        expandableListView.setAdapter(myexpendAdapter);

    }

    //prepare the list in listview
    private void prepareList() {
        headings = new ArrayList<String>();
        childlist = new HashMap<String, List<String>>();

        //Adding Parend headingd
        headings.add("Genre 1");
        headings.add("Genre 2");
        headings.add("Genre 3");

        //Adding Child Data
        List<String> RnB = new ArrayList<String>();
        RnB.add("Walking away");
        RnB.add("Looking through the window");

        HipHop = new ArrayList<String>();
        dataget("Hip Hop", HipHop);

        List<String> Kwaito = new ArrayList<String>();
        Kwaito.add("Woza nawe");
        Kwaito.add("Wewe Mama keh");
        Kwaito.add("Jaiva nami");
        Kwaito.add("Woza nawe");
        Kwaito.add("Wewe Mama keh");
        Kwaito.add("Jaiva nami");
        Kwaito.add("Woza nawe");
        Kwaito.add("Wewe Mama keh");
        Kwaito.add("Jaiva nami");
        Kwaito.add("Woza nawe");
        Kwaito.add("Wewe Mama keh");
        Kwaito.add("Jaiva nami");

        //adding list to respective cats
        childlist.put(headings.get(0), RnB);
        childlist.put(headings.get(1), HipHop);
        childlist.put(headings.get(2), Kwaito);
    }

    //Retrieve data
    public void dataget(String gendr, final List<String> temp) {

        reference = FirebaseDatabase.getInstance().getReference("Genre").child(gendr);

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                poplistview(dataSnapshot, temp);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                poplistview(dataSnapshot, temp);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    //populate listview
    public void poplistview(DataSnapshot ds, List<String> temp) {
        temp.clear();

        for (DataSnapshot dataSnapshot : ds.getChildren()) {
            Gendre g = new Gendre();
            g.setSongName(dataSnapshot.getValue(Gendre.class).getSongName().toString());
            temp.add(g.getSongName().toString());
        }
    }

}
