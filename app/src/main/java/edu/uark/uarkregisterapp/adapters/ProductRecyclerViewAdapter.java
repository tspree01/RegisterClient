package edu.uark.uarkregisterapp.adapters;

import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.uark.uarkregisterapp.ProductListViewHolder;
import edu.uark.uarkregisterapp.R;
import edu.uark.uarkregisterapp.models.api.ApiResponse;
import edu.uark.uarkregisterapp.models.api.Product;
import edu.uark.uarkregisterapp.models.api.services.CartService;
import edu.uark.uarkregisterapp.models.transition.EmployeeTransition;

public class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductListViewHolder> {
    private Context context;
    private List<Product> productList;
    private View productView;
    private EmployeeTransition employeeTransition;
    private static List<Product> productsInCart = new ArrayList<>();

    @NonNull
    @Override
    public ProductListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewtype) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_item_product, parent, false);
        return new ProductListViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductListViewHolder productListViewHolder, int position) {

        if (productList != null && position < productList.size()) {
            final Product product = productList.get(position);
            int zero = 0;
            productListViewHolder.productTitle.setText(product.getLookupCode());
            productListViewHolder.productCount.setText(String.format(Locale.getDefault(), "%d", product.getCount()));

            ImageButton upButton = productListViewHolder.upButton;
            upButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int quantity = 0;
                    if (productListViewHolder.productQuantityEditText.getText().toString().equals("")) {
                        productListViewHolder.productQuantityEditText.setText(String.valueOf(quantity + 1));
                        product.setCount(quantity + 1);
                    } else {
                        quantity = Integer.parseInt(productListViewHolder.productQuantityEditText.getText().toString());
                        productListViewHolder.productQuantityEditText.setText(String.valueOf(quantity + 1));
                        product.setCount(quantity + 1);
                    }
                }
            });

            ImageButton downButton = productListViewHolder.downButton;
            downButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int quantity = 0;
                    if (!productListViewHolder.productQuantityEditText.getText().toString().equals("") && Integer.parseInt(productListViewHolder.productQuantityEditText.getText().toString()) > 0) {
                        quantity = Integer.parseInt(productListViewHolder.productQuantityEditText.getText().toString());
                        productListViewHolder.productQuantityEditText.setText(String.valueOf(quantity - 1));
                        product.setCount(quantity - 1);
                    }
                }
            });

            Button addButton = productListViewHolder.addButton;
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    product.setCartId(employeeTransition.getId());
                    String quantityEditTextString = productListViewHolder.productQuantityEditText.getText().toString();
                    if (quantityEditTextString.equals("")) {
                        productListViewHolder.productQuantityLayout.setError("Quantity can't be blank");
                    } else {
                        productListViewHolder.productQuantityLayout.setErrorEnabled(false);
                        product.setQuantity_sold(Integer.parseInt(productListViewHolder.productQuantityEditText.getText().toString()));
                        (new RetrieveProductsTask()).execute();
                        (new SaveProductToCartTask(product, context, productsInCart)).execute();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class SaveProductToCartTask extends AsyncTask<Void, Void, Boolean> {
        Product product;
        Context context;
        List<Product> productsInCart;

        SaveProductToCartTask(Product product, Context context, List<Product> productsInCart) {
            this.product = product;
            this.context = context;
            this.productsInCart = productsInCart;
        }

        @Override
        protected void onPreExecute() {
        }

        private boolean isProductInCart(Product product) {
            for (Product productInCart : productsInCart) {
                if (productInCart.getCartId().equals(product.getCartId()) && productInCart.getId().equals(product.getId())) {
                    return true;
                }
            }
            return false;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean test = isProductInCart(product);

            ApiResponse<Product> apiResponse = (
                    (test)
                            ? (new CartService()).updateProductByCartIdAndProductIdFromProductListing(product)
                            : (new CartService()).createProduct(product)
            );
            return apiResponse.isValidResponse();
        }

        @Override
        protected void onPostExecute(Boolean successfulSave) {
            if (successfulSave) {
                Snackbar.make(productView, R.string.add_item_to_cart_message, Snackbar.LENGTH_SHORT)
                        .show();
            } else {
                Snackbar.make(productView, R.string.add_item_to_cart_error_message, Snackbar.LENGTH_SHORT)
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

    public ProductRecyclerViewAdapter(Context context, List<Product> productList, View productView, EmployeeTransition employeeTransition) {
        this.context = context;
        this.productList = productList;
        this.productView = productView;
        this.employeeTransition = employeeTransition;
    }

}