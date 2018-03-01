package com.example.folcotandiono.visitreportapps;

import android.content.Intent;
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

/**
 * Created by User on 3/1/2018.
 */

public class AddVisitPlanAdapter extends RecyclerView.Adapter<AddVisitPlanAdapter.ViewHolder> {
    private static ArrayList<String> placeName = new ArrayList<>();
    private static ArrayList<String> address = new ArrayList<>();
    private static ArrayList<LatLng> latlng = new ArrayList<>();
    private static ArrayList<String> checkIn = new ArrayList<>();
    private static ArrayList<String> checkOut = new ArrayList<>();
    private static String circleName;
    private static String date;
    private ArrayList<String> placeNameAdapter = new ArrayList<>();
    private ArrayList<String> addressAdapter = new ArrayList<>();

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView cardAddVisitPlanPosition;
        public TextView cardAddVisitPlanPlaceName;
        public TextView cardAddVisitPlanAddress;
        public Button cardAddVisitPlanButton;
        public ViewHolder(final View v) {
            super(v);
            cardAddVisitPlanPosition = v.findViewById(R.id.cardAddVisitPlanPosition);
            cardAddVisitPlanPlaceName = v.findViewById(R.id.cardAddVisitPlanPlaceName);
            cardAddVisitPlanAddress = v.findViewById(R.id.cardAddVisitPlanAddress);
            cardAddVisitPlanButton = v.findViewById(R.id.cardAddVisitPlanButton);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(v.getContext(), CustomerDetailActivity.class);
                    intent.putExtra("placeName", cardAddVisitPlanPlaceName.getText().toString());
                    v.getContext().startActivity(intent);
                }
            });

            cardAddVisitPlanButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

                    firebaseDatabase.getReference("Customer").child(cardAddVisitPlanPlaceName.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            LatLng temp = new LatLng((double) dataSnapshot.child("lat").getValue(), (double) dataSnapshot.child("lng").getValue());

                            placeName.add(cardAddVisitPlanPlaceName.getText().toString());
                            address.add(cardAddVisitPlanAddress.getText().toString());
                            latlng.add(temp);
                            checkIn.add("");
                            checkOut.add("");

                            firebaseDatabase.getReference("VisitPlan").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(circleName).child(date).child("placeName").setValue(placeName);
                            firebaseDatabase.getReference("VisitPlan").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(circleName).child(date).child("address").setValue(address);
                            firebaseDatabase.getReference("VisitPlan").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(circleName).child(date).child("latlng").setValue(latlng);
                            firebaseDatabase.getReference("VisitPlan").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(circleName).child(date).child("checkIn").setValue(checkIn);
                            firebaseDatabase.getReference("VisitPlan").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(circleName).child(date).child("checkOut").setValue(checkOut);

                            Toast.makeText(v.getContext(), "Added to visit plan", Toast.LENGTH_SHORT).show();

                            ((AddVisitPlanActivity) v.getContext()).onBackPressed();
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
    public AddVisitPlanAdapter(ArrayList<String> placeName, ArrayList<String> address, ArrayList<LatLng> latlng, ArrayList<String> checkIn, ArrayList<String> checkOut, String circleName, String date, ArrayList<String> placeNameAdapter, ArrayList<String> addressAdapter) {
        if (placeName != null) this.placeName = placeName;
        if (address != null) this.address = address;
        if (latlng != null) this.latlng = latlng;
        if (checkIn != null) this.checkIn = checkIn;
        if (checkOut != null) this.checkOut = checkOut;
        this.circleName = circleName;
        this.date = date;
        if (placeNameAdapter != null) this.placeNameAdapter = placeNameAdapter;
        if (addressAdapter != null) this.addressAdapter = addressAdapter;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AddVisitPlanAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_add_visit_plan, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.cardAddVisitPlanPosition.setText(String.valueOf(position));
        holder.cardAddVisitPlanPlaceName.setText(placeNameAdapter.get(position));
        holder.cardAddVisitPlanAddress.setText(addressAdapter.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return placeNameAdapter.size();
    }
}
