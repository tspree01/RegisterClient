package edu.uark.uarkregisterapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import edu.uark.uarkregisterapp.CartActivity;
import edu.uark.uarkregisterapp.ProductCardViewHolder;
import edu.uark.uarkregisterapp.R;
import edu.uark.uarkregisterapp.models.api.Product;

public class CartRecyclerViewAdapter extends RecyclerView.Adapter<ProductCardViewHolder> {
    public Context context;
    public List<Product> productList;
    private View cartView;
    public ProductCardViewHolder productCardViewHolder;

    public CartRecyclerViewAdapter(Context context, List<Product> productList, View cartView){
        this.context = context;
        this.productList = productList;
        this.cartView = cartView;
    }

    @NonNull
    @Override
    public ProductCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card, parent, false);
            productCardViewHolder = new ProductCardViewHolder(layoutView);
            return productCardViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductCardViewHolder productCardViewHolder, int position) {
        if(productList != null && position < productList.size()){
            Product product = productList.get(position);
            productCardViewHolder.productTitle.setText(product.getLookupCode());
            productCardViewHolder.productPrice.setText(String.format(Locale.getDefault(), "$ %.2f", product.getPrice()));
            productCardViewHolder.productQuantity.setText(String.format(Locale.getDefault(),"%d",product.getCount()));
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
