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

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Folco Tandiono on 28/02/2018.
 */

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> {
    private static ArrayList<String> placeName = new ArrayList<String>();
    private static ArrayList<String> address = new ArrayList<String>();

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView cardCustomerPosition;
        public TextView cardCustomerPlaceName;
        public TextView cardCustomerAddress;
        public Button cardCustomerDelete;
        public ViewHolder(View v) {
            super(v);
            cardCustomerPosition = v.findViewById(R.id.cardCustomerPosition);
            cardCustomerPlaceName = v.findViewById(R.id.cardCustomerPlaceName);
            cardCustomerAddress = v.findViewById(R.id.cardCustomerAddress);
            cardCustomerDelete = v.findViewById(R.id.cardCustomerDelete);

            cardCustomerDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    firebaseDatabase.getReference("Customer").child(cardCustomerPlaceName.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            dataSnapshot.getRef().removeValue();
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
    public CustomerAdapter(ArrayList<String> placeName, ArrayList<String> address) {
        if (placeName != null) this.placeName = placeName;
        if (address != null) this.address = address;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CustomerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_customer, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.cardCustomerPosition.setText(String.valueOf(position));
        holder.cardCustomerPlaceName.setText(placeName.get(position));
        holder.cardCustomerAddress.setText(address.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return placeName.size();
    }
}

