package com.projects.bram.calendar;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//This is a standard Recycler View
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mTaskList = new ArrayList<>();
    private Context mContext;

    ToDoActivity todo = new ToDoActivity();
    String mDate;
    int mTaskNumber;
    private ArrayList<String> colorList = new ArrayList<>();
    String currentColor = "";

    DatabaseReference myRef;

    public RecyclerViewAdapter(ArrayList<String> taskList, Context context, String date, int taskNumber) {
        mTaskList = taskList;
        mContext = context;
        mDate = date;
        mTaskNumber = taskNumber;

        //Color list to indicate how series a task is: Gray98 , Blue, Green, then Red
        colorList.add("#FAFAFA"); //Gray98
        colorList.add("#0000FF"); //Blue
        colorList.add("#008000"); //Green
        colorList.add("#FF0000"); //Red

    }

    @NonNull
    @Override
    //Responsible for inflating the view
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
         Log.d(TAG, "onBindViewHolder: called. || " + mTaskList.get(position));

         // The current color is the last 7 digits, # included
         currentColor = mTaskList.get(position).substring(mTaskList.get(position).length() - 7, mTaskList.get(position).length());
         Log.d(TAG, "current color " + currentColor + " || " + Color.parseColor("#FAFAFA"));
         holder.task.setText(mTaskList.get(position).substring(0, mTaskList.get(position).length() - 7));
         holder.parentLayout.setBackgroundColor(Color.parseColor(currentColor));
         holder.parentLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                // The current color is the last 7 digits, # included
                currentColor = mTaskList.get(position).substring(mTaskList.get(position).length() - 7, mTaskList.get(position).length());

                //holder.parentLayout.setBackgroundColor(nextColor(Color.parseColor(currentColor)));
                //holder.parentLayout.setBackgroundColor(Color.parseColor("#0000FF"));
                int newColor = nextColor(Color.parseColor(currentColor));

                //grab the database reference
                myRef = todo.database.getInstance().getReference();
                Map<String, Object> newChild = new HashMap<>();
                newChild.put(position + "", mTaskList.get(position).substring(0, mTaskList.get(position).length() - 7) + String.format("#%06X", (0xFFFFFF & newColor)));
                DatabaseReference childUpdate = myRef.child(mDate);
                childUpdate.updateChildren(newChild);
                holder.parentLayout.setBackgroundColor(newColor);
                mTaskList.set(position, mTaskList.get(position).substring(0, mTaskList.get(position).length() - 7) + String.format("#%06X", (0xFFFFFF & newColor)));
                //Toast.makeText(mContext, mTaskList.get(position), Toast.LENGTH_SHORT).show();
                //In here we can set up another alert dialog to allow for a color to be picked.

            }
        });
    }

    @Override
    public int getItemCount() {
        return mTaskList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView task;
        Button remove;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            //set up various items
            task = itemView.findViewById(R.id.itemList);
            remove = itemView.findViewById(R.id.removeItem);
            parentLayout = itemView.findViewById(R.id.parent_layout);

            //To remove an item from our recyclerView
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Toast.makeText(mContext, "Removing Item: " + getAdapterPosition(), Toast.LENGTH_SHORT).show();

                    //Grabs the current position of which ever remove button was clicked
                    int save = getAdapterPosition();

                    mTaskList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    notifyItemRangeChanged(getAdapterPosition(), mTaskList.size());

                    todo.removeItem(save, mContext, mDate, mTaskNumber);
                }
            });
        }
    }

    //this gets the next color to show how urgent the task is
    private int nextColor(int currentColor) {

        String hexColor = String.format("#%06X", (0xFFFFFF & currentColor));
        int current = colorList.indexOf(hexColor);

        //Move on to the next color
        if((current + 1) < colorList.size())
            return Color.parseColor(colorList.get(current + 1));
        //at the end of the list, set to Gray98
        else
            Log.d(TAG, "WE ARE NOW WRAPPING");
            return Color.parseColor("#FAFAFA");
    }
}

