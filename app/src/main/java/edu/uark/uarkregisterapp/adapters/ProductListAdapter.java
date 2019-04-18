package edu.uark.uarkregisterapp.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import edu.uark.uarkregisterapp.ProductsListingActivity;
import edu.uark.uarkregisterapp.R;
import edu.uark.uarkregisterapp.models.api.ApiResponse;
import edu.uark.uarkregisterapp.models.api.Product;
import edu.uark.uarkregisterapp.models.api.services.CartService;
import edu.uark.uarkregisterapp.models.api.services.ProductService;

public class ProductListAdapter extends ArrayAdapter<Product> {
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(this.getContext());
            view = inflater.inflate(R.layout.list_view_item_product, parent, false);
        }

        final Product product = this.getItem(position);
        if (product != null) {
            TextView lookupCodeTextView = (TextView) view.findViewById(R.id.list_view_item_product_lookup_code);
            if (lookupCodeTextView != null) {
                lookupCodeTextView.setText(product.getLookupCode());
            }

            TextView countTextView = (TextView) view.findViewById(R.id.list_view_item_product_count);
            if (countTextView != null) {
                countTextView.setText(String.format(Locale.getDefault(), "%d", product.getCount()));
            }
        }

        Button addButton = (Button) view.findViewById(R.id.button_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new SaveProductTask(product)).execute();
            }
        });

        return view;
    }

    private class SaveProductTask extends AsyncTask<Void, Void, Boolean> {
        Product product;
        public SaveProductTask(Product product) {
            this.product = product;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            ApiResponse<Product> apiResponse = ((new CartService()).createProduct(product)
            );

            if (apiResponse.isValidResponse()) {
            }

            return apiResponse.isValidResponse();
        }

        @Override
        protected void onPostExecute(Boolean successfulSave) {
        }
    }

    public ProductListAdapter(Context context, List<Product> products) {
        super(context, R.layout.list_view_item_product, products);
    }
}