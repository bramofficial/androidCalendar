package com.projects.bram.calendar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ToDoActivity extends AppCompatActivity {

    private static final String TAG = "ToDoActivity";
    //handling for our Floating Action Button
    FloatingActionButton fab;
    //for getting the extras sent in our buttonClick activity
    Intent intent;
    //String that holds whatever task that needs to be done by the user
    private String dialogText = "";
    //for use with our recyclerView
    private ArrayList<String> mTaskNames = new ArrayList<>();
    int numberTasks = 0;
    //Firebase Auth
    private FirebaseAuth mAuth;
    //Firebase Database
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    //This grabs whatever the "key" is
    DatabaseReference myRef; // = database.getReference("MoreTesting/");

    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);
        Log.d(TAG, "onCreate: started");
        //grabbing variables
        fab = findViewById(R.id.fab);
        intent = getIntent();
        date = intent.getStringExtra("Date");

        //start the auth
        mAuth = FirebaseAuth.getInstance();

        //Auth anonymously for starters
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Auth WORKED", Toast.LENGTH_SHORT).show();
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                } else {
                    Toast.makeText(getApplicationContext(), "Auth failed", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //This section is to auto populate onCreate and whenever a child is changed. This should auto handle when we remove it as well
        myRef = FirebaseDatabase.getInstance().getReference();

        //Toast.makeText(getApplicationContext(), "MyREF: " + myRef.toString(), Toast.LENGTH_LONG).show();

        myRef.child(intent.getStringExtra("Date")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int size = (int) dataSnapshot.getChildrenCount();
                numberTasks = size;
                //Toast.makeText(getApplicationContext(), "Date1: " + intent.getStringExtra("Date") + " |||  Size: " + numberTasks, Toast.LENGTH_LONG).show();

                //To populate the recyclerView with whats in our database
                for(int i = 0; i < numberTasks; i++){
                    initTasks(dataSnapshot.child(i + "").getValue().toString(), 0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Floating Action Button at the bottom right hand corner
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //call open to evoke the alertDialogBuilder and get it all set up
                myRef = database.getReference(intent.getStringExtra("Date") + "/" + mTaskNames.size() + "/");
                open(v);
                //Toast.makeText(getApplicationContext(), "FAB CLICKED" + intent.getStringExtra("Date"), Toast.LENGTH_SHORT).show();


            }
        });

    }

    //an example of how the AlertDialog system works, will need to modify this section
    //we need a text input and a time input. these may have to be separate
    public void open(View view) {
        //For opening up the AlertDialog to insert new text into our recyclerview
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add a task");
        builder.setMessage("Please enter a task you want to add.");

        //View viewInflated = LayoutInflater.from(getApplicationContext()).inflate(R.layout.textinput, (ViewGroup) findViewById(android.R.id.content), false);
        final EditText input = new EditText(this);
        builder.setView(input);

        //The OK button
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                dialogText = input.getText().toString() + " #FAFAFA"; // this adds the white-ish color to our database with our input.getText()
                myRef.setValue(dialogText);
                initTasks(dialogText, 0);
                //Toast.makeText(getApplicationContext(), "Data sent: " + dialogText, Toast.LENGTH_LONG).show();

                //Adding the color to the end of the text saved

            }
        });

        //When you select the cancel option
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Both these options work, this one is probably better
                dialogInterface.cancel();
                //closeContextMenu();
            }
        });

        //create and show the alert Dialog
        AlertDialog dialog = builder.create();
        dialog.show();


    }

    //Add the task to our list, then call our recycler view
    private void initTasks(final String textToBeAdded, int size) {

        mTaskNames.add(textToBeAdded);
        initRecyclerView(mTaskNames.size());

        if(textToBeAdded.equals("")){
            initRecyclerView(size);
        }
    }

    //sets up the recycler view with the proper information
    public void initRecyclerView(int taskNumber) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mTaskNames, this, date, taskNumber);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    //This will need to remove the specific position then update the database with the right indexes
    public void removeItem(final int position, final Context mContext, final String mDate, final int taskNumber){
        //myRef.child(intent.getStringExtra("Date"));
        final Map<String, Object> childUpdates = new HashMap<>();

        myRef = database.getInstance().getReference();

        myRef.child(mDate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().child(position + "").setValue(null);

                // from our position we are removing, get the rest and move them up a spot
                for(int j = position; j < dataSnapshot.getChildrenCount(); j++) {
                    if (j != position) {
                        childUpdates.put(( j - 1) + "", dataSnapshot.child(j + "").getValue().toString());
                    }
                }

                //our child we need to update with a built in function
                DatabaseReference childUpdate = myRef.child(mDate);
                childUpdate.updateChildren(childUpdates);

                //this removes an item from the database
                dataSnapshot.getRef().child((dataSnapshot.getChildrenCount() - 1) + "").setValue(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

