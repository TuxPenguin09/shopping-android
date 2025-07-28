package com.meisan.shopping.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.meisan.shopping.R;
import com.meisan.shopping.ReceiptActivity;
import com.meisan.shopping.utils.CartManager;

import java.util.ArrayList;

public class CheckoutFragment extends Fragment {

    private static final String ARG_CART_TOTAL = "cart_total";
    private static final String ARG_CART_ITEMS = "cart_items";
    private static final int RECEIPT_REQUEST_CODE = 1001;

    private EditText nameEditText, addressEditText;
    private TextView totalTextView;
    private Button confirmButton;

    public CheckoutFragment() {
        // Required empty public constructor
    }

    public static CheckoutFragment newInstance(float cartTotal, ArrayList<String> cartItems) {
        CheckoutFragment fragment = new CheckoutFragment();
        Bundle args = new Bundle();
        args.putFloat(ARG_CART_TOTAL, cartTotal);
        args.putStringArrayList(ARG_CART_ITEMS, cartItems);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_checkout, container, false);

        nameEditText = view.findViewById(R.id.nameEditText);
        addressEditText = view.findViewById(R.id.addressEditText);
        totalTextView = view.findViewById(R.id.totalTextView);
        confirmButton = view.findViewById(R.id.confirmButton);

        Bundle args = getArguments();
        float cartTotal = 0.0f;
        ArrayList<String> cartItems = new ArrayList<>();

        if (args != null) {
            cartTotal = args.getFloat(ARG_CART_TOTAL, 0.0f);
            cartItems = args.getStringArrayList(ARG_CART_ITEMS);
            if (cartItems == null) {
                // Fallback: get cart items from CartManager if not passed
                cartItems = new ArrayList<>();
                for (var item : CartManager.getInstance().getCartItems()) {
                    cartItems.add(item.getName() + " - $" + String.format("%.2f", item.getPrice()));
                }
            }
        } else {
            // Fallback: get data from CartManager if no arguments passed
            cartTotal = (float) CartManager.getInstance().getTotalPrice();
            for (var item : CartManager.getInstance().getCartItems()) {
                cartItems.add(item.getName() + " - $" + String.format("%.2f", item.getPrice()));
            }
        }

        totalTextView.setText(String.format("Total: $%.2f", cartTotal));

        final float finalCartTotal = cartTotal;
        final ArrayList<String> finalCartItems = cartItems;

        confirmButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String address = addressEditText.getText().toString().trim();

            if (name.isEmpty() || address.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(getActivity(), ReceiptActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("address", address);
            intent.putExtra("cartItems", finalCartItems);
            intent.putExtra("total", finalCartTotal);
            startActivityForResult(intent, RECEIPT_REQUEST_CODE);
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RECEIPT_REQUEST_CODE) {
            // Navigate back to shop when returning from receipt
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_checkoutFragment_to_itemListFragment);
        }
    }
}