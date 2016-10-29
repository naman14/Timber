package com.naman14.timber.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.naman14.timber.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by naman on 29/10/16.
 */
public class DonateActivity extends BaseThemedActivity implements BillingProcessor.IBillingHandler {

    private static final String DONATION_1 = "naman14.timber.donate_1";
    private static final String DONATION_2 = "naman14.timber.donate_2";
    private static final String DONATION_3 = "naman14.timber.donate_3";
    private static final String DONATION_5 = "naman14.timber.donate_5";
    private static final String DONATION_10 = "naman14.timber.donate_10";

    private boolean readyToPurchase = false;
    BillingProcessor bp;

    private LinearLayout productListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        bp = new BillingProcessor(this, getString(R.string.play_billing_license_key), this);

        productListView = (LinearLayout) findViewById(R.id.product_list);

        ((Button) findViewById(R.id.btn_donate)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (readyToPurchase)
                    bp.purchase(DonateActivity.this, DONATION_1);
                else
                    Toast.makeText(DonateActivity.this, "Not initialised", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBillingInitialized() {
        readyToPurchase = true;
        getProducts();
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    private void getProducts() {

        new AsyncTask<Void,Void,List<SkuDetails>>() {
            @Override
            protected List<SkuDetails> doInBackground(Void... voids) {
                ArrayList<String> products = new ArrayList<>();

                products.add(DONATION_1);
                products.add(DONATION_2);
                products.add(DONATION_3);
                products.add(DONATION_5);
                products.add(DONATION_10);

                return bp.getPurchaseListingDetails(products);
            }

            @Override
            protected void onPostExecute(List<SkuDetails> productList) {
                super.onPostExecute(productList);
                for (int i = 0; i < productList.size(); i++) {
                    final SkuDetails product = productList.get(i);
                    View rootView = LayoutInflater.from(DonateActivity.this).inflate(R.layout.item_donate_product, productListView, false);

                    TextView detail = (TextView) rootView.findViewById(R.id.product_detail);
                    detail.setText(product.title);

                    rootView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            bp.purchase(DonateActivity.this, product.productId);
                        }
                    });
                    productListView.addView(rootView);

                }
            }
        }.execute();
    }

}
