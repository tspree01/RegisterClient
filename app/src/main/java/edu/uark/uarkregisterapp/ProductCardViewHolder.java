package edu.uark.uarkregisterapp;

import androidx.annotation.NonNull;
import com.google.android.material.button.MaterialButton;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class ProductCardViewHolder extends RecyclerView.ViewHolder {

    public TextView productTitle;
    public TextView productPrice;
    public TextView productQuantity;
    public MaterialButton plusButton;
    public MaterialButton minusButton;

    public ProductCardViewHolder(@NonNull View itemView) {
        super(itemView);
        productTitle = itemView.findViewById(R.id.product_title);
        productPrice = itemView.findViewById(R.id.product_price);
        productQuantity = itemView.findViewById(R.id.product_quantity);
        plusButton = itemView.findViewById(R.id.material_icon_button_add);
        minusButton = itemView.findViewById(R.id.material_icon_button_minus);
    }
}
