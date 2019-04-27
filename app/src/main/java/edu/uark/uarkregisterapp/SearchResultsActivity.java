package edu.uark.uarkregisterapp;

import android.app.Activity;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import edu.uark.uarkregisterapp.adapters.ProductListAdapter;
import edu.uark.uarkregisterapp.adapters.ProductRecyclerViewAdapter;
import edu.uark.uarkregisterapp.adapters.ProductSearchResultsRecyclerViewAdapter;
import edu.uark.uarkregisterapp.models.api.ApiResponse;
import edu.uark.uarkregisterapp.models.api.Product;
import edu.uark.uarkregisterapp.models.api.services.CartService;
import edu.uark.uarkregisterapp.models.api.services.ProductService;
import edu.uark.uarkregisterapp.models.api.services.SearchService;
import edu.uark.uarkregisterapp.models.transition.EmployeeTransition;
import edu.uark.uarkregisterapp.models.transition.ProductTransition;

public class SearchResultsActivity extends AppCompatActivity {
    private List<Product> products;
    private RecyclerView recyclerView;
    private static ProductTransition productTransition;
    private ProductListAdapter productListAdapter;
    private ProductSearchResultsRecyclerViewAdapter ProductSearchResultsRecyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private View productListView;
    private EmployeeTransition employeeTransition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_listing_search_results);
        recyclerView = this.findViewById(R.id.list_view_products);
        productListView = findViewById(R.id.product_listing);

        this.products = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String searchQuery = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            (new RetrieveFoundProductsTask(searchQuery)).execute();
            ProductSearchResultsRecyclerViewAdapter = new ProductSearchResultsRecyclerViewAdapter(this, products, productListView, employeeTransition);
            recyclerView.setAdapter(ProductSearchResultsRecyclerViewAdapter);

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
            dividerItemDecoration.setDrawable(getDrawable(R.drawable.product_list_divider));
            recyclerView.addItemDecoration(dividerItemDecoration);
        }
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

    class RetrieveFoundProductsTask extends AsyncTask<Void, Void, ApiResponse<List<Product>>> {
        String searchQuery;

        public RetrieveFoundProductsTask(String searchQuery) {
            this.searchQuery = searchQuery;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected ApiResponse<List<Product>> doInBackground(Void... params) {
            ApiResponse<List<Product>> apiResponse = (new SearchService().searchProducts(searchQuery));

            if (apiResponse.isValidResponse()) {
                products.clear();
                products.addAll(apiResponse.getData());
            }
            return apiResponse;
        }

        @Override
        protected void onPostExecute(ApiResponse<List<Product>> apiResponse) {
        }
    }

}
