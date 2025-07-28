package com.meisan.shopping.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.meisan.shopping.R;
import com.meisan.shopping.adapter.CartAdapter;
import com.meisan.shopping.model.Item;
import com.meisan.shopping.utils.CartManager;

import java.util.List;

public class CartFragment extends Fragment implements CartManager.CartUpdateListener {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private TextView totalTextView;
    private Button checkoutButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        cartRecyclerView = view.findViewById(R.id.cartRecyclerView);
        totalTextView = view.findViewById(R.id.totalTextView);
        checkoutButton = view.findViewById(R.id.checkoutButton);

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Item> cartItems = CartManager.getInstance().getCartItems();
        cartAdapter = new CartAdapter(cartItems);
        cartRecyclerView.setAdapter(cartAdapter);

        updateTotal();

        checkoutButton.setOnClickListener(v -> {
            double total = CartManager.getInstance().getTotalPrice();
            CartFragmentDirections.ActionCartFragmentToCheckoutFragment action =
                    CartFragmentDirections.actionCartFragmentToCheckoutFragment((float) total);
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(action);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        CartManager.getInstance().addCartUpdateListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        CartManager.getInstance().removeCartUpdateListener(this);
    }

    @Override
    public void onCartUpdated(List<Item> items) {
        if (cartAdapter != null) {
            cartAdapter.updateItems(items);
            updateTotal();
        }
    }

    private void updateTotal() {
        double total = CartManager.getInstance().getTotalPrice();
        totalTextView.setText(String.format("Total: $%.2f", total));
    }
}