
package edu.uark.uarkregisterapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.uark.uarkregisterapp.R;
import edu.uark.uarkregisterapp.models.api.Product;

public class ProductSearchAdapter extends BaseAdapter {
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(searchContext);
            view = inflater.inflate(R.layout.list_view_search_product, parent, false);
        }

        if (searchList.get(position) != null) {
            TextView lookupCodeTextView = (TextView) view.findViewById(R.id.list_view_item_product_lookup_code);
            if (lookupCodeTextView != null) {
                lookupCodeTextView.setText(searchList.get(position).getLookupCode());
            }

            TextView countTextView = (TextView) view.findViewById(R.id.list_view_item_product_count);
            if (countTextView != null) {
                countTextView.setText(String.format(Locale.getDefault(), "%d", searchList.get(position).getCount()));
            }

            TextView priceTextView = (TextView) view.findViewById (R.id.list_view_item_product_price);
            if (countTextView != null) {
                priceTextView.setText(String.format(Locale.getDefault(), "$ %.2f", searchList.get(position).getPrice()));
            }
        }

        return view;
    }

    @Override
    public int getCount() {
        return searchList.size();
    }

    @Override
    public Product getItem(int position) {
        return searchList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void filter(String searchText){
        searchText = searchText.toLowerCase(Locale.getDefault());
        searchList.clear();
        if (searchText.length() == 0) {
            searchList.addAll(product_list);
        }
        else {
            for (Product product : product_list) {
                if (product.getLookupCode().toLowerCase(Locale.getDefault()).contains(searchText)) {
                    searchList.add(product);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setProductList(List<Product> products_update){
        this.product_list.clear();
        this.product_list.addAll(products_update);
    }

    public ProductSearchAdapter(Context context, List<Product> products) {
        this.searchContext = context;
        this.searchList = products;
        this.product_list = new ArrayList<Product>();
    }

    Context searchContext;
    private List<Product> searchList = null;
    private ArrayList<Product> product_list;
}