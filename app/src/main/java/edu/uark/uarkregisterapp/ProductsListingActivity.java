package edu.uark.uarkregisterapp;

import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.uark.uarkregisterapp.adapters.ProductListAdapter;
import edu.uark.uarkregisterapp.adapters.ProductRecyclerViewAdapter;
import edu.uark.uarkregisterapp.adapters.ProductSearchResultsRecyclerViewAdapter;
import edu.uark.uarkregisterapp.models.api.ApiResponse;
import edu.uark.uarkregisterapp.models.api.Product;
import edu.uark.uarkregisterapp.models.api.services.ProductService;
import edu.uark.uarkregisterapp.models.api.services.SearchService;
import edu.uark.uarkregisterapp.models.transition.EmployeeTransition;
import edu.uark.uarkregisterapp.models.transition.ProductTransition;

public  class ProductsListingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_listing);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        RecyclerView recyclerView = getProductsRecyclerView();
        productListView = findViewById(R.id.product_listing);

        ActionBar productListActionBar = this.getSupportActionBar();
        if (productListActionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        this.employeeTransition = this.getIntent().getParcelableExtra(this.getString(R.string.intent_extra_employee));

        this.products = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getDrawable(R.drawable.product_list_divider));
        recyclerView.addItemDecoration(dividerItemDecoration);

        productRecyclerViewAdapter = new ProductRecyclerViewAdapter(this, products, productListView, employeeTransition);
        recyclerView.setAdapter(productRecyclerViewAdapter);

        this.searchProducts = new ArrayList<>();
        searchRecyclerView = this.findViewById(R.id.includeSearchResults).findViewById(R.id.list_view_products);
        searchedLayoutManager = new LinearLayoutManager(this);
        searchRecyclerView.setLayoutManager(searchedLayoutManager);
        handleIntent(getIntent());

/*       this.products = new ArrayList<>();
        this.productListAdapter = new ProductListAdapter(this, this.products);
		this.getProductsListView().setAdapter(this.productListAdapter);*/
    }


    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String searchQuery = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            (new RetrieveSearchedProductsTask(searchQuery)).execute();
            ProductSearchResultsRecyclerViewAdapter = new ProductSearchResultsRecyclerViewAdapter(this, searchProducts, productListView, employeeTransition);
            searchRecyclerView.setAdapter(ProductSearchResultsRecyclerViewAdapter);

            DividerItemDecoration searchdividerItemDecoration = new DividerItemDecoration(searchRecyclerView.getContext(), DividerItemDecoration.VERTICAL);
            searchdividerItemDecoration.setDrawable(getDrawable(R.drawable.product_list_divider));
            searchRecyclerView.addItemDecoration(searchdividerItemDecoration);
        }
    }

    public void shoppingCartFloatingActionOnClick(View view) {
        final View shoppingCartView = findViewById(R.id.shopping_cart_activity);
        Intent intent = new Intent(getApplicationContext(), CartActivity.class);

        intent.putExtra(
                getString(R.string.intent_extra_employee),
                employeeTransition
        );
               startActivity(intent,
                ActivityOptions.makeClipRevealAnimation(shoppingCartView, shoppingCartView.getWidth(), shoppingCartView.getHeight(), 50, 50).toBundle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_buttons, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search_product_list).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search Products");
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// Respond to the action bar's Up/Home button
                this.finish();

                return true;
            case R.id.search_product_list:
                this.findViewById(R.id.includeSearchResults).setVisibility(View.VISIBLE);
                searchButtonPushed = true;
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!searchButtonPushed) {
            (new RetrieveProductsTask()).execute();
        }
    }

    class RetrieveSearchedProductsTask extends AsyncTask<Void, Void, ApiResponse<List<Product>>> {
        String searchQuery;

        RetrieveSearchedProductsTask(String searchQuery) {
            this.searchQuery = searchQuery;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected ApiResponse<List<Product>> doInBackground(Void... params) {
            ApiResponse<List<Product>> apiResponse = (new SearchService().searchProducts(searchQuery));

            if (apiResponse.isValidResponse()) {
                searchProducts.clear();
                searchProducts.addAll(apiResponse.getData());
            }
            return apiResponse;
        }

        @Override
        protected void onPostExecute(ApiResponse<List<Product>> apiResponse) {
            if (apiResponse.isValidResponse()) {
                productRecyclerViewAdapter.notifyDataSetChanged();
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
        }

        private AlertDialog loadingProductsAlert;
        private RetrieveSearchedProductsTask() {
            this.loadingProductsAlert = new AlertDialog.Builder(ProductsListingActivity.this).
                    setMessage(R.string.alert_dialog_products_loading).
                    create();
        }
    }

/*    private ListView getProductsListView() {
        return (ListView) this.findViewById(R.id.list_view_products);
    }*/
    private RecyclerView getProductsRecyclerView() {
        return (RecyclerView) this.findViewById(R.id.list_view_products);
    }

    private TextView getProductLookupCodeEditText() {
        return (TextView) this.findViewById(R.id.edit_text_product_lookup_code);
    }

    private  EditText getProductCountEditText() {
        return (EditText) this.findViewById(R.id.edit_text_product_count);
    }

    public class RetrieveProductsTask extends AsyncTask<Void, Void, ApiResponse<List<Product>>> {
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
                productRecyclerViewAdapter.notifyDataSetChanged();
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
        }

        private AlertDialog loadingProductsAlert;
        private RetrieveProductsTask() {
            this.loadingProductsAlert = new AlertDialog.Builder(ProductsListingActivity.this).
                    setMessage(R.string.alert_dialog_products_loading).
                    create();
        }
    }

    private List<Product> searchProducts;
    private boolean searchButtonPushed = false;
    private View productListView;
    private RecyclerView searchRecyclerView;
    private edu.uark.uarkregisterapp.adapters.ProductSearchResultsRecyclerViewAdapter ProductSearchResultsRecyclerViewAdapter;
    private RecyclerView.LayoutManager searchedLayoutManager;
    private View searchedProductListView;
    private List<Product> products;
    private static ProductTransition productTransition;
    private ProductListAdapter productListAdapter;
    private ProductRecyclerViewAdapter productRecyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private EmployeeTransition employeeTransition;

}
