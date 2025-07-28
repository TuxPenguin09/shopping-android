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

public class CartFragment extends Fragment {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private TextView totalTextView;
    private Button checkoutButton;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        cartRecyclerView = view.findViewById(R.id.cartRecyclerView);
        totalTextView = view.findViewById(R.id.totalTextView);
        checkoutButton = view.findViewById(R.id.checkoutButton);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        cartAdapter = new CartAdapter(ItemListFragment.cartItems, this::updateTotal);
        cartRecyclerView.setAdapter(cartAdapter);

        updateTotal();

        checkoutButton.setOnClickListener(v -> {
            float total = 0;
            for (Item item : ItemListFragment.cartItems) {
                total += item.getPrice();
            }
            CartFragmentDirections.ActionCartFragmentToCheckoutFragment action =
                    CartFragmentDirections.actionCartFragmentToCheckoutFragment(total);
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(action);
        });

        return view;
    }

    private void updateTotal() {
        double total = 0;
        for (Item item : ItemListFragment.cartItems) {
            total += item.getPrice();
        }
        totalTextView.setText(String.format("Total: $%.2f", total));
    }
}