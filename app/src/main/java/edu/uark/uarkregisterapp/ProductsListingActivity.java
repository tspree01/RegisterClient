package edu.uark.uarkregisterapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
		productCardAdapter = new ProductCardRecyclerViewAdapter(products);
		recyclerView.setAdapter(productCardAdapter);

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


	private int getTotal(List<Product> productList){
		int total = 0;
		for (Product product : productList) {
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

			case R.id.cart:
				LayoutInflater inflater = (LayoutInflater)
						getSystemService(LAYOUT_INFLATER_SERVICE);

				View popupView = inflater.inflate(R.layout.activity_products_listing, null);
				this.products = new ArrayList<>();
				this.productListAdapter = new ProductListAdapter(this, this.products);

				//this.getProductsListView().setAdapter(this.productListAdapter);
				(new RetrieveProductsTask()).execute();

				// create the popup window
				int width = LinearLayout.LayoutParams.MATCH_PARENT;
				int height = LinearLayout.LayoutParams.WRAP_CONTENT;
				boolean focusable = true; // lets taps outside the popup also dismiss it
				final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
				popupWindow.setElevation(10);

				// show the popup window
				// which view you pass in doesn't matter, it is only used for the window tolken
				popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

				// dismiss the popup window when touched
				popupView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						popupWindow.dismiss();
					}

/*                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        return true;
                    }*/
				});
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
		}

		private AlertDialog loadingProductsAlert;

		private RetrieveProductsTask() {
			this.loadingProductsAlert = new AlertDialog.Builder(ProductsListingActivity.this).
				setMessage(R.string.alert_dialog_products_loading).
				create();
		}
	}

	private List<Product> products;
	private ProductListAdapter productListAdapter;
	private ProductCardRecyclerViewAdapter productCardAdapter;
	RecyclerView.LayoutManager layoutManager;
}
