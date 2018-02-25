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
 * Created by Folco Tandiono on 21/02/2018.
 */

public class CheckInAdapter extends RecyclerView.Adapter<CheckInAdapter.ViewHolder> {
    private static ArrayList<String> placeName = new ArrayList<String>();
    private static ArrayList<String> address = new ArrayList<String>();
    private static ArrayList<String> checkIn = new ArrayList<String>();
    private static ArrayList<LatLng> latlng = new ArrayList<LatLng>();
    private static String circleName;
    private static String date;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView cardCheckInPosition;
        public TextView cardCheckInPlaceName;
        public TextView cardCheckInAddress;
        public TextView cardCheckInStatusCheckIn;
        public Button cardCheckInButton;

        public ViewHolder(View v) {
            super(v);
            cardCheckInPosition = v.findViewById(R.id.cardCheckInPosition);
            cardCheckInPlaceName = v.findViewById(R.id.cardCheckInPlaceName);
            cardCheckInAddress = v.findViewById(R.id.cardCheckInAddress);
            cardCheckInStatusCheckIn = v.findViewById(R.id.cardCheckInStatusCheckIn);
            cardCheckInButton = v.findViewById(R.id.cardCheckInButton);

            cardCheckInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final int pos = Integer.valueOf(cardCheckInPosition.getText().toString());
                    if (!checkIn.get(pos).isEmpty()) {
                        Toast.makeText(v.getContext(), "Already checked in", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    database.getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("checkIn").child("status").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Boolean bisaCheckIn = (Boolean) dataSnapshot.getValue();
                            if (bisaCheckIn == true) {

                                database.getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        LatLng positionNow;
                                        positionNow = new LatLng((double) dataSnapshot.child("lat").getValue(), (double) dataSnapshot.child("lng").getValue());
                                        double radius = (double) v.getContext().getResources().getInteger(R.integer.radius);
                                        
                                        LatLng positionTo = new LatLng(latlng.get(pos).latitude, latlng.get(pos).longitude);
                                        Location now = new Location("now");
                                        now.setLatitude(positionNow.latitude);
                                        now.setLongitude(positionNow.longitude);
                                        Location to = new Location("to");
                                        to.setLatitude(positionTo.latitude);
                                        to.setLongitude(positionTo.longitude);
                                        double jarak = now.distanceTo(to);

                                        if (jarak <= radius) {
                                            Calendar calendar = Calendar.getInstance();
                                            checkIn.set(pos, calendar.getTime().toString());
                                            database.getReference("VisitPlan").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(circleName).child(date).child("checkIn").setValue(checkIn);
                                            database.getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("checkIn").child("status").setValue(false);
                                            database.getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("checkIn").child("circle").setValue(circleName);
                                            database.getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("checkIn").child("date").setValue(calendar.getTime().toString());

                                            Toast.makeText(v.getContext(), "Checked in", Toast.LENGTH_SHORT).show();

                                        } else {
                                            Toast.makeText(v.getContext(), "Not in location radius", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            } else {
                                Toast.makeText(v.getContext(), "Check out first", Toast.LENGTH_SHORT).show();
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
    public CheckInAdapter(ArrayList<String> placeName, ArrayList<String> address, ArrayList<String> checkIn, String circleName, String date, ArrayList<LatLng> latlng) {
        if (placeName != null) this.placeName = placeName;
        if (address != null) this.address = address;
        if (checkIn != null) this.checkIn = checkIn;
        this.circleName = circleName;
        this.date = date;
        if (latlng != null) this.latlng = latlng;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CheckInAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_check_in, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.cardCheckInPosition.setText(String.valueOf(position));
        holder.cardCheckInPlaceName.setText(placeName.get(position));
        holder.cardCheckInAddress.setText(address.get(position));
        if (checkIn.get(position).isEmpty()) {
            holder.cardCheckInStatusCheckIn.setText("Status : Have not checked in");
        } else {
            holder.cardCheckInStatusCheckIn.setText("Status : Checked in at " + checkIn.get(position));
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return placeName.size();
    }
}
