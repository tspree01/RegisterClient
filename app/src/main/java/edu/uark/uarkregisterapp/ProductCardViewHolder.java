package edu.uark.uarkregisterapp;

import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
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
