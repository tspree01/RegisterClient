package edu.uark.uarkregisterapp.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

import edu.uark.uarkregisterapp.CartActivity;
import edu.uark.uarkregisterapp.ProductCardViewHolder;
import edu.uark.uarkregisterapp.R;
import edu.uark.uarkregisterapp.models.api.ApiResponse;
import edu.uark.uarkregisterapp.models.api.Product;
import edu.uark.uarkregisterapp.models.api.services.CartService;

public class CartRecyclerViewAdapter extends RecyclerView.Adapter<ProductCardViewHolder> {
    public Context context;
    private List<Product> productList;
    private View cartView;

    public CartRecyclerViewAdapter(Context context, List<Product> productList, View cartView){
        this.context = context;
        this.productList = productList;
        this.cartView = cartView;
    }

    @NonNull
    @Override
    public ProductCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card, parent, false);
        return new ProductCardViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductCardViewHolder productCardViewHolder, int position) {
        if(productList != null && position < productList.size()){
            final Product product = productList.get(position);
            productCardViewHolder.productTitle.setText(product.getLookupCode());
            productCardViewHolder.productPrice.setText(String.format(Locale.getDefault(), "$ %.2f", product.getPrice()));
            productCardViewHolder.productQuantity.setText(String.format(Locale.getDefault(),"%d",product.getCount()));
            productCardViewHolder.productQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    productCardViewHolder.productQuantity.addTextChangedListener(new TextWatcher() {

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            product.setCount(Integer.parseInt(s.toString()));
                        }
                        @Override
                        public void afterTextChanged(Editable s) {
                            (new UpdateProductTask(product,context)).execute();
                        }
                    });
                }
            });
        }
    }

    private class UpdateProductTask extends AsyncTask<Void, Void, Boolean> {
        Product product;
        Context context;

        UpdateProductTask(Product product, Context context) {
            this.product = product;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            ApiResponse<Product> apiResponse = ((new CartService()).updateProduct(product)
            );
            return apiResponse.isValidResponse();
        }

        @Override
        protected void onPostExecute(Boolean successfulSave) {
            if(successfulSave) {
                Toast.makeText(context, "Updated!", Toast.LENGTH_SHORT)
                        .show();
                ((TextView) cartView.findViewById(R.id.bottom_sheet_subtotal_price)).setText(String.format(Locale.getDefault(), "$ %.2f", CartActivity.calculateSubtotal(productList)));
                ((TextView) cartView.findViewById(R.id.bottom_sheet_taxes_price)).setText(String.format(Locale.getDefault(), "$ %.2f", CartActivity.calculateTaxes(productList)));
                ((TextView) cartView.findViewById(R.id.bottom_sheet_total_price)).setText(String.format(Locale.getDefault(), "$ %.2f", CartActivity.calculateTotal(productList)));
            }
            else {
                Toast.makeText(context, "Failed to updated!", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void deleteItem(int position){
        productList.remove(position);
        notifyDataSetChanged();
        ((TextView) cartView.findViewById(R.id.bottom_sheet_subtotal_price)).setText(String.format(Locale.getDefault(), "$ %.2f", CartActivity.calculateSubtotal(productList)));
        ((TextView) cartView.findViewById(R.id.bottom_sheet_taxes_price)).setText(String.format(Locale.getDefault(), "$ %.2f", CartActivity.calculateTaxes(productList)));
        ((TextView) cartView.findViewById(R.id.bottom_sheet_total_price)).setText(String.format(Locale.getDefault(), "$ %.2f", CartActivity.calculateTotal(productList)));
    }
}
