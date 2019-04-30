package edu.uark.uarkregisterapp;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class ProductListViewHolder extends RecyclerView.ViewHolder {

    public TextView productTitle;
    public TextView productPrice;
    public TextView productCount;
    public EditText productQuantityEditText;
    public TextInputLayout productQuantityLayout;
    public ImageButton upButton;
    public ImageButton downButton;
    public Button addButton;

    public ProductListViewHolder(@NonNull View itemView) {
        super(itemView);
        productTitle = itemView.findViewById(R.id.list_view_item_product_title);
        productCount = itemView.findViewById(R.id.list_view_item_product_count);
        productQuantityEditText = itemView.findViewById(R.id.quantityEditText);
        productQuantityLayout = itemView.findViewById(R.id.quantityTextLayout);
        upButton = itemView.findViewById(R.id.upArrow);
        downButton = itemView.findViewById(R.id.downArrow);
        addButton = itemView.findViewById(R.id.button_add);
    }
}
