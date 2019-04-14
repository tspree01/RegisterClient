package edu.uark.uarkregisterapp;

import android.animation.Animator;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.uark.uarkregisterapp.adapters.ProductCardRecyclerViewAdapter;
import edu.uark.uarkregisterapp.adapters.ProductListAdapter;
import edu.uark.uarkregisterapp.models.api.ApiResponse;
import edu.uark.uarkregisterapp.models.api.Product;
import edu.uark.uarkregisterapp.models.api.services.ProductService;
import edu.uark.uarkregisterapp.models.transition.ProductTransition;

public class ProductsListingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_listing);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        RecyclerView recyclerView = getProductsListView();

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }



        this.products = new ArrayList<>();

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        productCardAdapter = new ProductCardRecyclerViewAdapter(this, products);
        recyclerView.setAdapter(productCardAdapter);
        recyclerView.addItemDecoration((new ProductCardHeaderViewDecoration(recyclerView.getContext(), recyclerView, R.layout.product_card_header)));

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDelete(productCardAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);


        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getDrawable(R.drawable.product_list_divider));
        recyclerView.addItemDecoration(dividerItemDecoration);



/*		this.getProductsListView().setAdapter(this.productListAdapter);
		this.getProductsListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getApplicationContext(), ProductViewActivity.class);

				intent.putExtra(
					getString(R.string.intent_extra_product),
					new ProductTransition((Product) getProductsListView().getItemAtPosition(position))
				);

				startActivity(intent);
			}
		});*/
    }

/*    public void expandBottomSheet(View view) {
        if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            ImageView nav_arrows = (ImageView) findViewById(R.id.nav_arrows);
            nav_arrows.setImageResource(R.drawable.animatied_nav_arrows);

            // Get the background, which has been compiled to an AnimationDrawable object.
            AnimationDrawable frameAnimation = (AnimationDrawable) nav_arrows.getDrawable();

            // Start the animation (looped playback by default).
            frameAnimation.start();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
        else {
            ImageView nav_arrows = (ImageView) findViewById(R.id.nav_arrows);
            nav_arrows.setImageResource(R.drawable.animatied_nav_arrows);

            // Get the background, which has been compiled to an AnimationDrawable object.
            AnimationDrawable frameAnimation = (AnimationDrawable) nav_arrows.getDrawable();

            // Start the animation (looped playback by default).
            frameAnimation.setOneShot(false);
            frameAnimation.setOneShot(true);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }*/

    public void shoppingCartFloatingActionOnClick(View view) {
        startActivity(new Intent(getApplicationContext(), CartActivity.class),
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
/*        shoppingCartView = findViewById(R.id.include_cart_listing);
        View appBar = findViewById(R.id.appBarLayout);
        RecyclerView recyclerViewCart = getCartProductsListView();

        this.productsCart = new ArrayList<>();
        layoutCartManager = new LinearLayoutManager(shoppingCartView.getContext());
        recyclerViewCart.setLayoutManager(layoutCartManager);
        productCardCartAdapter = new ProductCardRecyclerViewAdapter((shoppingCartView.getContext()),productsCart);
        recyclerViewCart.setAdapter(productCardCartAdapter);
        recyclerViewCart.addItemDecoration((new ProductCardHeaderViewDecoration(recyclerViewCart.getContext(),recyclerViewCart,R.layout.product_card_header)));
        ItemTouchHelper itemTouchHelperCart = new
                ItemTouchHelper(new SwipeToDelete(productCardCartAdapter));
        itemTouchHelperCart.attachToRecyclerView(recyclerViewCart);

        DividerItemDecoration dividerCartItemDecoration = new DividerItemDecoration(recyclerViewCart.getContext(), DividerItemDecoration.VERTICAL);
        dividerCartItemDecoration.setDrawable(getDrawable(R.drawable.product_list_divider));
        recyclerViewCart.addItemDecoration(dividerCartItemDecoration);*/
/*
        (new RetrieveCartProductsTask()).execute();
        // previously invisible view
            // get the center for the clipping circle
            int cx = shoppingCartView.getWidth();
            int cy = shoppingCartView.getHeight();

            // get the final radius for the clipping circle
            float finalRadius = (float) Math.hypot(cx, cy);

            // create the animator for this view (the start radius is zero)
            Animator anim = ViewAnimationUtils.createCircularReveal(shoppingCartView, cx, cy, 0f, finalRadius);
            Animator animAppBar = ViewAnimationUtils.createCircularReveal(appBar, cx, cy, 0f, finalRadius);

            // make the view visible and start the animation
        shoppingCartView.setVisibility(View.VISIBLE);
        appBar.setVisibility(View.VISIBLE);
            anim.start();
            animAppBar.start()*/;


    }

    public int getTotal() {
        int total = 0;
        for (Product product : products) {
            total += product.getPrice();
        }
        return total;
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


    private class RetrieveProductsTask extends AsyncTask<Void, Void, ApiResponse<List<Product>>> {
        @Override
        protected void onPreExecute() {
            this.loadingProductsAlert.show();
        }

        @Override
        protected ApiResponse<List<Product>> doInBackground(Void... params) {
            ApiResponse<List<Product>> apiResponse = (new ProductService()).getProducts();

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
                new AlertDialog.Builder(ProductsListingActivity.this).
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

           /* ((TextView) findViewById(R.id.bottom_sheet_product_total)).setText(String.format(Locale.getDefault(), "$ %d", getTotal()));*/
        }

        private AlertDialog loadingProductsAlert;

        private RetrieveProductsTask() {
            this.loadingProductsAlert = new AlertDialog.Builder(ProductsListingActivity.this).
                    setMessage(R.string.alert_dialog_products_loading).
                    create();
        }
    }


    private List<Product> products;
    View shoppingCartView;
    private ProductListAdapter productListAdapter;
    private ProductCardRecyclerViewAdapter productCardAdapter;
    RecyclerView.LayoutManager layoutManager;
}
