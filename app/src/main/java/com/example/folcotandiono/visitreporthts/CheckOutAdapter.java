package com.example.folcotandiono.visitreporthts;

import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Folco Tandiono on 25/02/2018.
 */

public class CheckOutAdapter extends RecyclerView.Adapter<CheckOutAdapter.ViewHolder> {
    private static ArrayList<String> placeName = new ArrayList<String>();
    private static ArrayList<String> address = new ArrayList<String>();
    private static ArrayList<String> checkOut = new ArrayList<String>();
    private static ArrayList<LatLng> latlng = new ArrayList<LatLng>();
    private static String circleName;
    private static String date;
    private static int pos;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView cardCheckOutPosition;
        public TextView cardCheckOutPlaceName;
        public TextView cardCheckOutAddress;
        public TextView cardCheckOutStatusCheckOut;
        public Button cardCheckOutButton;
        public View cardCheckOutView;

        public ViewHolder(View v) {
            super(v);
            cardCheckOutView = v;
            cardCheckOutPosition = v.findViewById(R.id.cardCheckOutPosition);
            cardCheckOutPlaceName = v.findViewById(R.id.cardCheckOutPlaceName);
            cardCheckOutAddress = v.findViewById(R.id.cardCheckOutAddress);
            cardCheckOutStatusCheckOut = v.findViewById(R.id.cardCheckOutStatusCheckOut);
            cardCheckOutButton = v.findViewById(R.id.cardCheckOutButton);

            cardCheckOutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final int ind = Integer.valueOf(cardCheckOutPosition.getText().toString());
                    if (!checkOut.get(ind).isEmpty()) {
                        Toast.makeText(v.getContext(), "Already checked out", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    database.getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("checkIn").child("status").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Boolean bisaCheckIn = (Boolean) dataSnapshot.getValue();
                            if (bisaCheckIn == false) {
                                if (pos != ind) {
                                    Toast.makeText(v.getContext(), "Please check out at the last place you check in", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                database.getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        LatLng positionNow;
                                        positionNow = new LatLng((double) dataSnapshot.child("lat").getValue(), (double) dataSnapshot.child("lng").getValue());
                                        double radius = (double) v.getContext().getResources().getInteger(R.integer.radius);

                                        LatLng positionTo = new LatLng(latlng.get(ind).latitude, latlng.get(ind).longitude);
                                        Location now = new Location("now");
                                        now.setLatitude(positionNow.latitude);
                                        now.setLongitude(positionNow.longitude);
                                        Location to = new Location("to");
                                        to.setLatitude(positionTo.latitude);
                                        to.setLongitude(positionTo.longitude);
                                        double jarak = now.distanceTo(to);

                                        if (jarak <= radius) {
                                            Calendar calendar = Calendar.getInstance();
                                            checkOut.set(ind, calendar.getTime().toString());
                                            database.getReference("VisitPlan").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(circleName).child(date).child("checkOut").setValue(checkOut);
                                            database.getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("checkIn").child("status").setValue(true);

                                            Toast.makeText(v.getContext(), "Checked out", Toast.LENGTH_SHORT).show();

                                        } else {
                                            Toast.makeText(v.getContext(), "Not in location radius", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            } else {
                                Toast.makeText(v.getContext(), "Check in first", Toast.LENGTH_SHORT).show();
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
    public CheckOutAdapter(ArrayList<String> placeName, ArrayList<String> address, ArrayList<String> checkOut, String circleName, String date, ArrayList<LatLng> latlng, int pos) {
        if (placeName != null) this.placeName = placeName;
        if (address != null) this.address = address;
        if (checkOut != null) this.checkOut = checkOut;
        this.circleName = circleName;
        this.date = date;
        if (latlng != null) this.latlng = latlng;
        this.pos = pos;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CheckOutAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_check_out, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.cardCheckOutPosition.setText(String.valueOf(position));
        holder.cardCheckOutPlaceName.setText(placeName.get(position));
        holder.cardCheckOutAddress.setText(address.get(position));
        if (checkOut.get(position).isEmpty()) {
            holder.cardCheckOutStatusCheckOut.setText("Status : Have not checked out");
        } else {
            holder.cardCheckOutStatusCheckOut.setText("Status : Checked out at " + checkOut.get(position));
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return placeName.size();
    }
}
