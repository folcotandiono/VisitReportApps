package com.example.folcotandiono.visitreporthts;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Folco Tandiono on 18/02/2018.
 */

public class CircleDetailsAdapter extends RecyclerView.Adapter<CircleDetailsAdapter.ViewHolder> {
    private static String[] name;
    private Boolean admin;
    private static String[] id;
    public static String circleName;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView circleDetailsName;
        public Button circleDetailsKick;
        public ViewHolder(View v) {
            super(v);
            circleDetailsName = v.findViewById(R.id.cardCircleDetailsName);
            circleDetailsKick = v.findViewById(R.id.cardCircleDetailsKick);

            circleDetailsKick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    String code = new String();
                    for (int i = 0; i < name.length; i++) {
                        if (name[i].equals(circleDetailsName.getText().toString())) {
                            code = id[i];
                        }
                    }
                    final String finalCode = code;
                    firebaseDatabase.getReference("User").child(code).child("circle").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<String> circle = new ArrayList<>();
                            if (dataSnapshot.getValue() != null) {
                                circle = (ArrayList<String>) dataSnapshot.getValue();
                                for (int i = 0; i < circle.size(); i++) {
                                    if (circle.get(i).equals(circleName)) {
                                        circle.remove(i);
                                        final ArrayList<String> finalCircle = circle;
                                        firebaseDatabase.getReference("Circle").child(circleName).child("id").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                ArrayList<String> id = new ArrayList<>();
                                                if (dataSnapshot.getValue() != null) {
                                                    id = (ArrayList<String>) dataSnapshot.getValue();
                                                    for (int i = 0; i < id.size(); i++) {
                                                        if (id.get(i).equals(finalCode)) {
                                                            id.remove(i);
                                                            firebaseDatabase.getReference("User").child(finalCode).child("circle").setValue(finalCircle);
                                                            firebaseDatabase.getReference("Circle").child(circleName).child("id").setValue(id);
                                                            break;
                                                        }
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                        break;
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CircleDetailsAdapter(String[] id, String[] name, Boolean admin, String circleName) {
        this.id = id;
        this.name = name;
        this.admin = admin;
        this.circleName = circleName;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CircleDetailsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_circle_details, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.circleDetailsName.setText(name[position]);
        if (!admin) {
            holder.circleDetailsKick.setVisibility(View.GONE);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return name.length;
    }
}
