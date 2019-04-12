package edu.uark.uarkregisterapp.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.uark.uarkregisterapp.ProductCardViewHolder;
import edu.uark.uarkregisterapp.R;
import edu.uark.uarkregisterapp.models.api.Product;

public class ProductCardRecyclerViewAdapter extends RecyclerView.Adapter<ProductCardViewHolder> {

    public List<Product> productList;

    public ProductCardRecyclerViewAdapter(List<Product> productList){
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card, parent, false);
            return new ProductCardViewHolder(layoutView);

    }

    @Override
    public void onBindViewHolder(@NonNull ProductCardViewHolder productCardViewHolder, int position) {
        if(productList != null && position < productList.size()){
            Product product = productList.get(position);
            productCardViewHolder.productTitle.setText(product.getLookupCode());
            productCardViewHolder.productPrice.setText(String.format(Locale.getDefault(), "$ %d", product.getPrice()));
            productCardViewHolder.productQuantity.setText(String.format(Locale.getDefault(),"%d",product.getCount()));
        }

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
