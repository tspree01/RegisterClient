package edu.uark.uarkregisterapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.uark.uarkregisterapp.adapters.EmployeeSalesListAdapter;
import edu.uark.uarkregisterapp.adapters.ProductSalesListAdapter;
import edu.uark.uarkregisterapp.models.api.ApiResponse;
import edu.uark.uarkregisterapp.models.api.Employee;
import edu.uark.uarkregisterapp.models.api.Product;
import edu.uark.uarkregisterapp.models.api.services.EmployeeService;
import edu.uark.uarkregisterapp.models.api.services.ProductService;
import edu.uark.uarkregisterapp.models.transition.EmployeeTransition;

public class BestSellingProductActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employees_listing);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        this.products = new ArrayList<>();
        this.productSalesListAdapter = new ProductSalesListAdapter(this, this.products);

        this.getProductsListView().setAdapter(this.productSalesListAdapter);
        this.getProductsListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), EmployeeViewActivity.class);

                intent.putExtra(
                        getString(R.string.intent_extra_product),
                        new EmployeeTransition((Employee) getProductsListView().getItemAtPosition(position))
                );

                startActivity(intent);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();

        (new BestSellingProductActivity.RetrieveProductsTask()).execute();
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
    private ListView getProductsListView() {
        return (ListView) this.findViewById(R.id.list_view_employees);
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

                Iterator<Product> iterator = products.iterator();
                Product product = products.get(0);
                double bigger_number = 0;
                while(iterator.hasNext()){

                    Product producte = iterator.next();
                    if (producte.getQuantity_sold() > bigger_number) {
                        product = producte;
                        bigger_number = product.getQuantity_sold();
                    }

                }
                products.clear();
                products.add(product);
            }


            return apiResponse;
        }
        @Override
        protected void onPostExecute(ApiResponse<List<Product>> apiResponse) {
            if (apiResponse.isValidResponse()) {
                productSalesListAdapter.notifyDataSetChanged();
            }

            this.loadingProductsAlert.dismiss();

            if (!apiResponse.isValidResponse()) {
                new AlertDialog.Builder(edu.uark.uarkregisterapp.BestSellingProductActivity.this).
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
            this.loadingProductsAlert = new AlertDialog.Builder(edu.uark.uarkregisterapp.BestSellingProductActivity.this).
                    setMessage(R.string.alert_dialog_products_loading).
                    create();
        }
    }

    private List<Product> products;
    private ProductSalesListAdapter productSalesListAdapter;

    }
