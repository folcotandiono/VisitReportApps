package com.example.folcotandiono.visitreporthts;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Folco Tandiono on 20/02/2018.
 */

public class VisitPlanAdapter extends RecyclerView.Adapter<VisitPlanAdapter.ViewHolder> {
    private static ArrayList<String> placeName = new ArrayList<String>();
    private static ArrayList<String> address = new ArrayList<String>();
    private static ArrayList<LatLng> latlng = new ArrayList<LatLng>();
    private static String date;
    private static String circleName;
    private static ArrayList<Boolean> statusCheckIn = new ArrayList<Boolean>();

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView visitPlanPosition;
        public TextView visitPlanPlaceName;
        public TextView visitPlanAddress;
        public TextView visitPlanStatusCheckIn;
        public Button visitPlanDelete;
        public ViewHolder(View v) {
            super(v);
            visitPlanPosition = v.findViewById(R.id.cardVisitPlanPosition);
            visitPlanPlaceName = v.findViewById(R.id.cardVisitPlanPlaceName);
            visitPlanAddress = v.findViewById(R.id.cardVisitPlanAddress);
            visitPlanStatusCheckIn = v.findViewById(R.id.cardVisitPlanStatusCheckIn);
            visitPlanDelete = v.findViewById(R.id.cardVisitPlanDelete);

            visitPlanDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    int ind = Integer.valueOf(visitPlanPosition.getText().toString());

                    placeName.remove(ind);
                    address.remove(ind);
                    latlng.remove(ind);
                    statusCheckIn.remove(ind);

                    database.getReference("VisitPlan").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(circleName).child(date).child("placeName").setValue(placeName);
                    database.getReference("VisitPlan").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(circleName).child(date).child("address").setValue(address);
                    database.getReference("VisitPlan").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(circleName).child(date).child("latlng").setValue(latlng);
                    database.getReference("VisitPlan").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(circleName).child(date).child("statusCheckIn").setValue(statusCheckIn);
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public VisitPlanAdapter(ArrayList<String> placeName, ArrayList<String> address, ArrayList<LatLng> latlng, String date, String circleName, ArrayList<Boolean> statusCheckIn) {
        if (placeName != null) this.placeName = placeName;
        if (address != null) this.address = address;
        if (latlng != null) this.latlng = latlng;
        this.date = date;
        this.circleName = circleName;
        if (statusCheckIn != null) this.statusCheckIn = statusCheckIn;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public VisitPlanAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_visit_plan, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.visitPlanPlaceName.setText(placeName.get(position));
        holder.visitPlanAddress.setText(address.get(position));
        if (statusCheckIn.get(position).equals(false)) {
            holder.visitPlanStatusCheckIn.setText("Status : Have not checked in");
        }
        else {
            holder.visitPlanStatusCheckIn.setText("Status : Already checked in");
        }
        holder.visitPlanPosition.setText(String.valueOf(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return placeName.size();
    }
}
