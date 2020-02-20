package edu.uark.uarkregisterapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.uark.uarkregisterapp.adapters.ProductSalesListAdapter;
import edu.uark.uarkregisterapp.models.api.ApiResponse;
import edu.uark.uarkregisterapp.models.api.Employee;
import edu.uark.uarkregisterapp.models.api.Product;
import edu.uark.uarkregisterapp.models.api.services.ProductService;
import edu.uark.uarkregisterapp.models.transition.EmployeeTransition;

public class BestSellingProductActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_sales_report_listing);
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
        return (ListView) this.findViewById(R.id.list_view_products);
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
                Product best_selling_product = products.get(0);
                Product second_best_selling_product = products.get(1);
                Product third_best_selling_product = products.get(2);
                Product product_iterator;
                Product temp_prod;

                double bigger_number = 0;
                while(iterator.hasNext()){
                    Product producte = iterator.next();
                    if (producte.getTotal_Sales() > bigger_number) {
                        product_iterator = producte;
                        if(product_iterator.getTotal_Sales() > best_selling_product.getTotal_Sales()){
                            temp_prod = best_selling_product;
                            best_selling_product = product_iterator;
                            product_iterator = temp_prod;
                        }
                        if(best_selling_product.getTotal_Sales() < second_best_selling_product.getTotal_Sales()) {
                            temp_prod = best_selling_product;
                            best_selling_product = second_best_selling_product;
                            second_best_selling_product = temp_prod;
                        }
                        if(second_best_selling_product.getTotal_Sales() < third_best_selling_product.getTotal_Sales()){
                            temp_prod = second_best_selling_product;
                            second_best_selling_product = third_best_selling_product;
                            third_best_selling_product = temp_prod;
                        }
                        if(third_best_selling_product.getTotal_Sales() > best_selling_product.getTotal_Sales())
                        {
                            temp_prod = third_best_selling_product;
                            third_best_selling_product = best_selling_product;
                            best_selling_product = temp_prod;
                        }
                        bigger_number = product_iterator.getTotal_Sales();
                    }


                }
                products.clear();
                products.add(best_selling_product);
                products.add(second_best_selling_product);
                products.add(third_best_selling_product);
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
                new AlertDialog.Builder(edu.uark.uarkregisterapp.BestSellingProductActivity.this,R.style.Theme_MaterialComponents_Dialog_Alert).
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
            this.loadingProductsAlert = new AlertDialog.Builder(edu.uark.uarkregisterapp.BestSellingProductActivity.this,R.style.Theme_MaterialComponents_Dialog_Alert).
                    setMessage(R.string.alert_dialog_products_loading).
                    create();
        }
    }

    private List<Product> products;
    private ProductSalesListAdapter productSalesListAdapter;

    }
