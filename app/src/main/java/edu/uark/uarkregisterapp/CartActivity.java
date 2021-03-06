package edu.uark.uarkregisterapp;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.transition.Explode;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import edu.uark.uarkregisterapp.adapters.CartRecyclerViewAdapter;
import edu.uark.uarkregisterapp.adapters.ProductListAdapter;
import edu.uark.uarkregisterapp.models.api.ApiResponse;
import edu.uark.uarkregisterapp.models.api.Employee;
import edu.uark.uarkregisterapp.models.api.Product;
import edu.uark.uarkregisterapp.models.api.services.CartService;
import edu.uark.uarkregisterapp.models.api.services.EmployeeService;
import edu.uark.uarkregisterapp.models.api.services.ProductService;
import edu.uark.uarkregisterapp.models.transition.EmployeeTransition;
import edu.uark.uarkregisterapp.models.transition.ProductTransition;

public class CartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_listing);
        Toolbar myToolbar = findViewById(R.id.cart_toolbar);
        setSupportActionBar(myToolbar);
        RecyclerView recyclerView = getProductsListView();
        cartView = findViewById(R.id.shopping_cart_activity);

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
        dividerItemDecoration.setDrawable(Objects.requireNonNull(getDrawable(R.drawable.product_list_divider)));
        recyclerView.addItemDecoration(dividerItemDecoration);
        this.employeeTransition = this.getIntent().getParcelableExtra(this.getString(R.string.intent_extra_employee));
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

    public static float calculateSubtotal(List<Product> products) {
        float subtotal = 0.0f;
        for (Product product : products) {
            subtotal += product.getPrice() * product.getQuantity_sold();
        }
        return subtotal;
    }

    public static float calculateTotal(List<Product> products) {
        total = calculateSubtotal(products) + calculateTaxes(products);
        return total;
    }

    public static float calculateTaxes(List<Product> products) {
        float taxRate = 0.0975f;
        return (calculateSubtotal(products) * (taxRate + 1)) - calculateSubtotal(products);
    }

    public void transactionButtonOnClick(View view) {
        Employee loggedInEmployee = new Employee();
        loggedInEmployee.setId(employeeTransition.getId());
        loggedInEmployee.setAmount_Of_Money_Made(total);
        (new TransactionTask(products,loggedInEmployee)).execute();
    }

    // create an action bar button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:  // Respond to the action bar's Up/Home button
                this.finish();
                getWindow().setExitTransition(new Explode());
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

    private class TransactionTask extends AsyncTask<Void, Void, Boolean> {
        List<Product> products;
        Employee loggedInEmployee;

        TransactionTask(List<Product> products, Employee loggedInEmployee) {
            this.products = products;
            this.loggedInEmployee = loggedInEmployee;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            ApiResponse<Product> apiResponse = new ApiResponse<Product>();
            ApiResponse<Employee> apiResponseEmployee = new ApiResponse<Employee>();
            apiResponseEmployee = (new EmployeeService().updateEmployee(loggedInEmployee));

            for (Product product : products) {
                apiResponse= ((new ProductService()).updateProduct(product));
            }
            return apiResponse.isValidResponse();
        }

        @Override
        protected void onPostExecute(Boolean successfulSave) {
            if (successfulSave) {
                Snackbar.make(cartView, R.string.transaction_complete_message, Snackbar.LENGTH_SHORT)
                        .show();
                (new DeleteProductsInCartTask()).execute();
            } else {
                Snackbar.make(cartView, R.string.transaction_complete_error_message, Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private class DeleteProductsInCartTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean isValidResponse = false;

            for (Product productInCart : products) {
                isValidResponse = (new CartService())
                        .deleteProductByCartId(employeeTransition.getId())
                        .isValidResponse();
            }
            return isValidResponse;
        }

        @Override
        protected void onPostExecute(final Boolean successfulSave) {
            if(successfulSave) {
                productCardAdapter.deleteAll();
            }
        }
    }

    private class RetrieveProductsTask extends AsyncTask<Void, Void, ApiResponse<List<Product>>> {

        @Override
        protected void onPreExecute() {
            this.loadingProductsAlert.show();
        }

        @Override
        protected ApiResponse<List<Product>> doInBackground(Void... params) {
            ApiResponse<List<Product>> apiResponse = (new CartService()).getProductsByCartId(employeeTransition.getId());

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
                new AlertDialog.Builder(CartActivity.this,R.style.Theme_MaterialComponents_Dialog_Alert).
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
            ((TextView) findViewById(R.id.bottom_sheet_taxes_price)).setText(String.format(Locale.getDefault(), "$   %.2f", calculateTaxes(products)));
            ((TextView) findViewById(R.id.bottom_sheet_total_price)).setText(String.format(Locale.getDefault(), "$ %.2f", calculateTotal(products)));
        }

        private AlertDialog loadingProductsAlert;
        private RetrieveProductsTask() {
            this.loadingProductsAlert = new AlertDialog.Builder(CartActivity.this,R.style.Theme_MaterialComponents_Dialog_Alert).
                    setMessage(R.string.alert_dialog_products_loading).
                    create();
        }
    }

    private List<Product> products;
    private Context context;
    View cartView;
    static float total = 0.0f;
    private ConstraintLayout layoutBottomSheet;
    private ProductTransition productTransition;
    private BottomSheetBehavior bottomSheetBehavior;
    private ProductListAdapter productListAdapter;
    private CartRecyclerViewAdapter productCardAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private EmployeeTransition employeeTransition;
}
