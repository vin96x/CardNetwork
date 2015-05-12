package vincently.cardnetwork;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CardArrayAdapter extends ArrayAdapter<String>{

    Typeface myFont;

    public CardArrayAdapter(Context context, int textViewResourceId, ArrayList<String> objects,Typeface t) {
        super(context, textViewResourceId,objects);
        myFont = t;
    }

    public TextView getView(int position, View convertView, ViewGroup parent) {
        TextView v = (TextView) super.getView(position, convertView, parent);
        v.setTypeface(myFont);
        return v;
    }

    public TextView getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView v = (TextView) super.getView(position, convertView, parent);
        v.setTypeface(myFont);
        return v;
    }
}

