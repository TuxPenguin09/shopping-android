package com.meisan.shopping;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.meisan.shopping.utils.CartManager;

import java.util.ArrayList;

public class ReceiptActivity extends AppCompatActivity {

    private TextView receiptTextView;
    private Button shareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        receiptTextView = findViewById(R.id.receiptTextView);
        shareButton = findViewById(R.id.shareButton);

        String name = getIntent().getStringExtra("name");
        String address = getIntent().getStringExtra("address");
        ArrayList<String> cartItems = getIntent().getStringArrayListExtra("cartItems");
        float total = getIntent().getFloatExtra("total", 0f);

        StringBuilder receipt = new StringBuilder();
        receipt.append("RECEIPT\n");
        receipt.append("==================\n\n");
        receipt.append("Customer: ").append(name).append("\n");
        receipt.append("Address: ").append(address).append("\n\n");
        receipt.append("Items Purchased:\n");
        receipt.append("------------------\n");

        if (cartItems != null) {
            for (String item : cartItems) {
                receipt.append(item).append("\n");
            }
        }

        receipt.append("------------------\n");
        receipt.append("TOTAL: $").append(String.format("%.2f", total)).append("\n");
        receipt.append("==================\n");
        receipt.append("Thank you for shopping!\n");

        receiptTextView.setText(receipt.toString());

        shareButton.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, receipt.toString());
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Shopping Receipt");
            startActivity(Intent.createChooser(shareIntent, "Share Receipt"));
        });
    }

    @Override
    public void onBackPressed() {
        CartManager.getInstance().clearCart();
        Toast.makeText(this, "Cart cleared successfully", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            CartManager.getInstance().clearCart();
        }
    }
}