package edu.uark.uarkregisterapp.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.uark.uarkregisterapp.R;
import edu.uark.uarkregisterapp.models.api.ApiResponse;
import edu.uark.uarkregisterapp.models.api.Product;
import edu.uark.uarkregisterapp.models.api.services.CartService;

public class ProductListAdapter extends ArrayAdapter<Product> {
    private Context context;
    private static List<Product> productsInCart = new ArrayList<>();

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull final ViewGroup parent) {
        int zero = 0;
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(this.getContext());
            view = inflater.inflate(R.layout.list_view_item_product, parent, false);
        }

        final Product product = this.getItem(position);
        final EditText quantityEditTexts = view.findViewById(R.id.quantityEditText);
        final TextInputLayout quantityTextLayout = view.findViewById(R.id.quantityTextLayout);
        if (product != null) {
            TextView lookupCodeTextView = (TextView) view.findViewById(R.id.list_view_item_product_title);
            if (lookupCodeTextView != null) {
                lookupCodeTextView.setText(product.getLookupCode());
            }

            TextView countTextView = (TextView) view.findViewById(R.id.list_view_item_product_count);
            if (countTextView != null) {
                countTextView.setText(String.format(Locale.getDefault(), "%d", product.getCount()));
            }
        }
/*
        ImageButton upButton = view.findViewById(R.id.upArrow);
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = 0;
                if (quantityEditTexts.getText().toString().equals("")) {
                    quantityEditTexts.setText(String.valueOf(quantity + 1));
                    product.setCount(quantity + 1);
                } else {
                    quantity = Integer.parseInt(quantityEditTexts.getText().toString());
                    quantityEditTexts.setText(String.valueOf(quantity + 1));
                    product.setCount(quantity + 1);
                }
            }
        });

        ImageButton downButton = view.findViewById(R.id.downArrow);
        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = 0;
                if (!quantityEditTexts.getText().toString().equals("") && Integer.parseInt(quantityEditTexts.getText().toString()) > 0) {
                    quantity = Integer.parseInt(quantityEditTexts.getText().toString());
                    quantityEditTexts.setText(String.valueOf(quantity - 1));
                    product.setCount(quantity - 1);
            }
            }
        });

        Button addButton = view.findViewById(R.id.button_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantityEditTextString = quantityEditTexts.getText().toString();
                if (quantityEditTextString.equals("")) {
                    quantityTextLayout.setError("Quantity can't be blank");
                } else {
                    quantityTextLayout.setErrorEnabled(false);
                    product.setCount(Integer.parseInt(quantityEditTexts.getText().toString()));
                    (new RetrieveProductsTask()).execute();
                    (new SaveProductTask(product, context, productsInCart)).execute();
                }
            }
        });*/
        return view;
    }

    class SaveProductTask extends AsyncTask<Void, Void, Boolean> {
        Product product;
        Context context;
        List<Product> productsInCart;

        SaveProductTask(Product product, Context context, List<Product> productsInCart) {
            this.product = product;
            this.context = context;
            this.productsInCart = productsInCart;
        }

        @Override
        protected void onPreExecute() {
        }

        private boolean isProductNotInCart(Product product) {
            for (Product productInCart : productsInCart) {
                if (productInCart.getLookupCode().equals(product.getLookupCode())) {
                    return false;
                }
            }
            return true;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            ApiResponse<Product> apiResponse = (
                    (isProductNotInCart(product))
                            ? (new CartService()).createProduct(product)
                            : (new CartService()).updateProductByID(product)
            );
            return apiResponse.isValidResponse();
        }

        @Override
        protected void onPostExecute(Boolean successfulSave) {
            if (successfulSave) {
                Toast.makeText(context, "Added to cart!", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(context, "Failed to add to cart!", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    static class RetrieveProductsTask extends AsyncTask<Void, Void, ApiResponse<List<Product>>> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected ApiResponse<List<Product>> doInBackground(Void... params) {
            ApiResponse<List<Product>> apiResponse = (new CartService()).getProducts();

            if (apiResponse.isValidResponse()) {
                productsInCart.clear();
                productsInCart.addAll(apiResponse.getData());
            }
            return apiResponse;
        }

        @Override
        protected void onPostExecute(ApiResponse<List<Product>> apiResponse) {
        }
    }

    public ProductListAdapter(Context context, List<Product> products) {
        super(context, R.layout.list_view_item_product, products);
        this.context = context;
    }
}