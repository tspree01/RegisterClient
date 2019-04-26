package edu.uark.uarkregisterapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.uark.uarkregisterapp.R;
import edu.uark.uarkregisterapp.models.api.Product;

public class ProductSearchAdapter extends ArrayAdapter<Product>{
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(this.getContext());
            view = inflater.inflate(R.layout.list_view_search_product, parent, false);
        }

        Product product = this.getItem(position);
        if (product != null) {
            TextView lookupCodeTextView = (TextView) view.findViewById(R.id.list_view_item_product_lookup_code);
            if (lookupCodeTextView != null) {
                lookupCodeTextView.setText(products.get(position).getLookupCode());
            }

            TextView countTextView = (TextView) view.findViewById(R.id.list_view_item_product_count);
            if (countTextView != null) {
                countTextView.setText(String.format(Locale.getDefault(), "%d", product.getCount()));
            }
        }

        return view;
    }

    public void filter(String charText){
        charText = charText.toLowerCase(Locale.getDefault());
    }

    public ProductSearchAdapter(Context context, List<Product> products) {
        super(context, R.layout.list_view_search_product, products);
        this.products = products;
        this.searchList = new ArrayList<Product>();
        this.searchList.addAll(products);
    }

    private List<Product> products = null;
    private ArrayList<Product> searchList;
}
