package edu.uark.uarkregisterapp.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
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
    //public ProductCardViewHolder productCardViewHolder;

    public CartRecyclerViewAdapter(Context context, List<Product> productList, View cartView){
        this.context = context;
        this.productList = productList;
        this.cartView = cartView;
    }

    @NonNull
    @Override
    public ProductCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card, parent, false);
            //productCardViewHolder = new ProductCardViewHolder(layoutView)
        return new ProductCardViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductCardViewHolder productCardViewHolder, int position) {
        if(productList != null && position < productList.size()){
            final Product product = productList.get(position);
            productCardViewHolder.productTitle.setText(product.getLookupCode());
            productCardViewHolder.productPrice.setText(String.format(Locale.getDefault(), "$ %.2f", product.getPrice()));
            productCardViewHolder.productQuantity.setText(String.format(Locale.getDefault(),"%d",product.getQuantity_sold()));

            MaterialButton plusButton = productCardViewHolder.plusButton;
            plusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int quantity = 0;
                    if (productCardViewHolder.productQuantity.getText().toString().equals("")) {
                        productCardViewHolder.productQuantity.setText(String.valueOf(quantity + 1));
                        product.setQuantity_sold(quantity + 1);
                        (new UpdateProductTask(product)).execute();
                    } else {
                        quantity = Integer.parseInt(productCardViewHolder.productQuantity.getText().toString());
                        productCardViewHolder.productQuantity.setText(String.valueOf(quantity + 1));
                        product.setQuantity_sold(quantity + 1);
                        (new UpdateProductTask(product)).execute();
                    }
                }
            });

            MaterialButton minusButton = productCardViewHolder.minusButton;
            minusButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int quantity = 0;
                    if (!productCardViewHolder.productQuantity.getText().toString().equals("") && Integer.parseInt(productCardViewHolder.productQuantity.getText().toString()) > 0) {
                        quantity = Integer.parseInt(productCardViewHolder.productQuantity.getText().toString());
                        productCardViewHolder.productQuantity.setText(String.valueOf(quantity - 1));
                        productList.get(productCardViewHolder.getAdapterPosition()).setQuantity_sold(quantity - 1);
                        (new UpdateProductTask(product)).execute();
                    }
                }
            });
        }
    }

    public class UpdateProductTask extends AsyncTask<Void, Void, Boolean> {
        Product product;

        UpdateProductTask(Product product) {
            this.product = product;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            ApiResponse<Product> apiResponse = ((new CartService()).updateProductByCartIdAndProductId(product)
            );
            return apiResponse.isValidResponse();
        }

        @Override
        protected void onPostExecute(Boolean successfulSave) {
            if(successfulSave) {
               // ((TextView) cartView.findViewById(R.id.product_price)).setText(String.format(Locale.getDefault(), "$ %.2f", CartActivity.calculateSubtotal(productList)));
                ((TextView) cartView.findViewById(R.id.bottom_sheet_subtotal_price)).setText(String.format(Locale.getDefault(), "$ %.2f", CartActivity.calculateSubtotal(productList)));
                ((TextView) cartView.findViewById(R.id.bottom_sheet_taxes_price)).setText(String.format(Locale.getDefault(), "$   %.2f", CartActivity.calculateTaxes(productList)));
                ((TextView) cartView.findViewById(R.id.bottom_sheet_total_price)).setText(String.format(Locale.getDefault(), "$ %.2f", CartActivity.calculateTotal(productList)));
            }
        }
    }

    private class DeleteProductFromCartTask extends AsyncTask<Void, Void, Boolean> {
        Product product;
        public DeleteProductFromCartTask(Product product) {
            this.product = product;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return (new CartService())
                    .deleteProductByCartIdAndProductId(product.getId(),product.getCartId())
                    .isValidResponse();
        }

        @Override
        protected void onPostExecute(final Boolean successfulSave) {
            if(successfulSave) {
                ((TextView) cartView.findViewById(R.id.bottom_sheet_subtotal_price)).setText(String.format(Locale.getDefault(), "$ %.2f", CartActivity.calculateSubtotal(productList)));
                ((TextView) cartView.findViewById(R.id.bottom_sheet_taxes_price)).setText(String.format(Locale.getDefault(), "$   %.2f", CartActivity.calculateTaxes(productList)));
                ((TextView) cartView.findViewById(R.id.bottom_sheet_total_price)).setText(String.format(Locale.getDefault(), "$ %.2f", CartActivity.calculateTotal(productList)));
            }
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void deleteItem(int position){
        (new DeleteProductFromCartTask(productList.get(position))).execute();
        productList.remove(position);
        notifyDataSetChanged();
    }
}
