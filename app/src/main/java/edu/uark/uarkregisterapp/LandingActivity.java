package edu.uark.uarkregisterapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import edu.uark.uarkregisterapp.models.transition.ProductTransition;

public class LandingActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_landing);
	}

	public void displayTransactionButtonOnClick(View view) {
		this.startActivity(new Intent(getApplicationContext(), ProductsListingActivity.class));
	}

	public void displayCreateEmployeeButtonOnClick(View view) {
		this.startActivity(new Intent(getApplicationContext(), ProductsListingActivity.class));
	}

	public void SalesReportButtonOnClick(View view) {
		Intent intent = new Intent(getApplicationContext(), ProductViewActivity.class);

		intent.putExtra(
			getString(R.string.intent_extra_product),
			new ProductTransition()
		);

		this.startActivity(intent);
	}
}
//adding commit