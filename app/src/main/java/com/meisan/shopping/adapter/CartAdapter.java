package com.meisan.shopping.adapter;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import com.meisan.shopping.R;
import com.meisan.shopping.model.Item;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<Item> cartItems;
    private OnItemRemovedListener onItemRemovedListener;

    public interface OnItemRemovedListener {
        void onItemRemoved();
    }

    public CartAdapter(List<Item> cartItems, OnItemRemovedListener listener) {
        this.cartItems = cartItems;
        this.onItemRemovedListener = listener;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {
        Item item = cartItems.get(position);
        holder.cartItemName.setText(item.getName());
        holder.cartItemPrice.setText(String.format("$%.2f", item.getPrice()));

        holder.itemView.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Remove Item")
                    .setMessage("Do you want to remove " + item.getName() + " from cart?")
                    .setPositiveButton("Remove", (dialog, which) -> {
                        cartItems.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, cartItems.size());
                        if (onItemRemovedListener != null) {
                            onItemRemovedListener.onItemRemoved();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView cartItemName, cartItemPrice;

        public CartViewHolder(View itemView) {
            super(itemView);
            cartItemName = itemView.findViewById(R.id.cartItemName);
            cartItemPrice = itemView.findViewById(R.id.cartItemPrice);
        }
    }
}