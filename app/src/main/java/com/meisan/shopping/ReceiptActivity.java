package com.meisan.shopping;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.meisan.shopping.model.Item;

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
        ArrayList<Item> cartItems = (ArrayList<Item>) getIntent().getSerializableExtra("cartItems");
        float total = getIntent().getFloatExtra("total", 0f);

        StringBuilder receipt = new StringBuilder();
        receipt.append("Receipt\n\n");
        receipt.append("Name: ").append(name).append("\n");
        receipt.append("Address: ").append(address).append("\n\n");
        receipt.append("Items Purchased:\n");
        for (Item item : cartItems) {
            receipt.append(item.getName()).append(": $").append(String.format("%.2f", item.getPrice())).append("\n");
        }
        receipt.append("\nTotal: $").append(String.format("%.2f", total));
        receiptTextView.setText(receipt.toString());

        shareButton.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, receipt.toString());
            startActivity(Intent.createChooser(shareIntent, "Share Receipt"));
        });
    }
}
