package edu.uark.uarkregisterapp;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import edu.uark.uarkregisterapp.adapters.CartRecyclerViewAdapter;
import edu.uark.uarkregisterapp.adapters.ProductListAdapter;
import edu.uark.uarkregisterapp.models.api.ApiResponse;
import edu.uark.uarkregisterapp.models.api.Product;
import edu.uark.uarkregisterapp.models.api.services.CartService;
import edu.uark.uarkregisterapp.models.api.services.ProductService;
import edu.uark.uarkregisterapp.models.transition.ProductTransition;

public class CartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_listing);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        RecyclerView recyclerView = getProductsListView();
        View cartView = findViewById(R.id.shopping_cart_activity);
        EditText productQuantity = findViewById(R.id.product_quantity);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        layoutBottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);

        this.products = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        productCardAdapter = new CartRecyclerViewAdapter(this, products, cartView);
        recyclerView.setAdapter(productCardAdapter);
        recyclerView.addItemDecoration((new ProductCardHeaderViewDecoration(recyclerView.getContext(), recyclerView, R.layout.product_card_header)));

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDelete(productCardAdapter));

        itemTouchHelper.attachToRecyclerView(recyclerView);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getDrawable(R.drawable.product_list_divider));
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    public void expandBottomSheet(View view) {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            ImageView nav_arrows = (ImageView) findViewById(R.id.nav_arrows);
            nav_arrows.setImageResource(R.drawable.animatied_nav_arrows);

            // Get the background, which has been compiled to an AnimationDrawable object.
            AnimationDrawable frameAnimation = (AnimationDrawable) nav_arrows.getDrawable();

            // Start the animation (looped playback by default).
            frameAnimation.start();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            ImageView nav_arrows = (ImageView) findViewById(R.id.nav_arrows);
            nav_arrows.setImageResource(R.drawable.animatied_nav_arrows);

            // Get the background, which has been compiled to an AnimationDrawable object.
            AnimationDrawable frameAnimation = (AnimationDrawable) nav_arrows.getDrawable();

            // Start the animation (looped playback by default).
            frameAnimation.setOneShot(false);
            frameAnimation.setOneShot(true);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public void shoppingCartFloatingActionOnClick(View view) {
        final View shoppingCartView = findViewById(R.id.shopping_cart_activity);

        startActivity(new Intent(getApplicationContext(), CartActivity.class),
                ActivityOptions.makeClipRevealAnimation(shoppingCartView, shoppingCartView.getWidth(), shoppingCartView.getHeight(), 50, 50).toBundle());
    }

    public static double calculateSubtotal(List<Product> products) {
        double subtotal = 0.0;
        for (Product product : products) {
            subtotal += product.getPrice() * product.getCount();
        }
        return subtotal;
    }

    public static double calculateTotal(List<Product> products) {
        double total = 0.0;
        total = calculateSubtotal(products) + calculateTaxes(products);
        return total;
    }

    public static double calculateTaxes(List<Product> products) {
        double taxRate = 0.0975;
        return (calculateSubtotal(products) * (taxRate + 1)) - calculateSubtotal(products);
    }

/*    public void productQuantityEditTextOnClick(View view) {
        productCardAdapter.productCardViewHolder.productQuantity.addTextChangedListener(new TextWatcher() {
            int newProductQuantity;
            boolean _ignore = false;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                newProductQuantity = Integer.parseInt(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {

                String productTitle = findViewById(R.id.product_title).toString();
                //String productQuantity = getProductCountTextInputEditText().toString();
                Product foundProduct = getProductFromList(productTitle);
                foundProduct.setCount(newProductQuantity);
                updateDataBase(foundProduct);

                ((TextView) findViewById(R.id.bottom_sheet_subtotal_price)).setText(String.format(Locale.getDefault(), "$ %.2f", calculateSubtotal(products)));
                ((TextView) findViewById(R.id.bottom_sheet_taxes_price)).setText(String.format(Locale.getDefault(), "$ %.2f", calculateTaxes(products)));
                ((TextView) findViewById(R.id.bottom_sheet_total_price)).setText(String.format(Locale.getDefault(), "$ %.2f", calculateTotal(products)));
            }
        });
    }*/

    public void updateDataBase(Product product) {
        ApiResponse<Product> apiResponse = ((new CartService()).updateProduct(product)
        );
    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_buttons, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:  // Respond to the action bar's Up/Home button
                this.finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        (new RetrieveProductsTask()).execute();
    }

    private RecyclerView getProductsListView() {
        return (RecyclerView) this.findViewById(R.id.recycler_view);
    }

    Product getProductFromList(String product_title) {
        Product foundProduct = new Product();
        for (Product product : products) {
            if (product.getLookupCode() == product_title) {
                foundProduct = product;
            }
        }
        return foundProduct;
    }

    private class RetrieveProductsTask extends AsyncTask<Void, Void, ApiResponse<List<Product>>> {
        @Override
        protected void onPreExecute() {
            this.loadingProductsAlert.show();
        }

        @Override
        protected ApiResponse<List<Product>> doInBackground(Void... params) {
            ApiResponse<List<Product>> apiResponse = (new CartService()).getProducts();

            if (apiResponse.isValidResponse()) {
                products.clear();
                products.addAll(apiResponse.getData());
            }
            return apiResponse;
        }

        @Override
        protected void onPostExecute(ApiResponse<List<Product>> apiResponse) {
            if (apiResponse.isValidResponse()) {
                productCardAdapter.notifyDataSetChanged();
            }
            this.loadingProductsAlert.dismiss();

            if (!apiResponse.isValidResponse()) {
                new AlertDialog.Builder(CartActivity.this).
                        setMessage(R.string.alert_dialog_products_load_failure).
                        setPositiveButton(
                                R.string.button_dismiss,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                }
                        ).
                        create().
                        show();
            }
            ((TextView) findViewById(R.id.bottom_sheet_subtotal_price)).setText(String.format(Locale.getDefault(), "$ %.2f", calculateSubtotal(products)));
            ((TextView) findViewById(R.id.bottom_sheet_taxes_price)).setText(String.format(Locale.getDefault(), "$ %.2f", calculateTaxes(products)));
            ((TextView) findViewById(R.id.bottom_sheet_total_price)).setText(String.format(Locale.getDefault(), "$ %.2f", calculateTotal(products)));
        }

        private AlertDialog loadingProductsAlert;

        private RetrieveProductsTask() {
            this.loadingProductsAlert = new AlertDialog.Builder(CartActivity.this).
                    setMessage(R.string.alert_dialog_products_loading).
                    create();
        }
    }

    private List<Product> products;
    ConstraintLayout layoutBottomSheet;
    private ProductTransition productTransition;
    BottomSheetBehavior bottomSheetBehavior;
    private ProductListAdapter productListAdapter;
    private CartRecyclerViewAdapter productCardAdapter;
    RecyclerView.LayoutManager layoutManager;
}
