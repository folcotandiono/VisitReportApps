package com.example.folcotandiono.visitreportapps;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Folco Tandiono on 16/02/2018.
 */

public class CircleOptionsAdapter extends RecyclerView.Adapter<CircleOptionsAdapter.ViewHolder> {
    private String[] listCircleName;
    private String[] listCircleCode;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView circleName;
        private TextView circleCode;
        public ViewHolder(View v) {
            super(v);
            circleName = (TextView) v.findViewById(R.id.cardCircleOptionsCircleName);
            circleCode = (TextView) v.findViewById(R.id.cardCircleOptionsCircleCode);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), CircleDetailsActivity.class);
                    intent.putExtra("circleName", circleName.getText().toString());
                    intent.putExtra("circleCode", circleCode.getText().toString());
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CircleOptionsAdapter(String[] listCircleName, String[] listCircleCode) {
        this.listCircleName = listCircleName;
        this.listCircleCode = listCircleCode;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CircleOptionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_circle_options, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.circleName.setText(listCircleName[position]);
        holder.circleCode.setText(listCircleCode[position]);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return listCircleName.length;
    }
}
