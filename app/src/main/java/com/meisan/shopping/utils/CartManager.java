package com.meisan.shopping.utils;

import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meisan.shopping.model.Item;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartManager {
    private static final String TAG = "CartManager";
    private static CartManager instance;
    private DatabaseReference cartRef;
    private List<Item> cartItems;
    private List<CartUpdateListener> listeners;
    private ValueEventListener cartListener;

    public interface CartUpdateListener {
        void onCartUpdated(List<Item> items);
    }

    private CartManager() {
        cartItems = new ArrayList<>();
        listeners = new ArrayList<>();
        initializeDatabase();
    }

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    private void initializeDatabase() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();
            cartRef = FirebaseDatabase.getInstance().getReference("carts").child(userId);

            cartListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    cartItems.clear();
                    for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                        String name = itemSnapshot.child("name").getValue(String.class);
                        Double price = itemSnapshot.child("price").getValue(Double.class);
                        String category = itemSnapshot.child("category").getValue(String.class);

                        if (name != null && price != null && category != null) {
                            cartItems.add(new Item(name, price, category));
                        }
                    }
                    notifyListeners();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "Database error: " + databaseError.getMessage());
                }
            };

            cartRef.addValueEventListener(cartListener);
        }
    }

    public void addItem(Item item) {
        if (cartRef != null) {
            String itemId = cartRef.push().getKey();
            if (itemId != null) {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("name", item.getName());
                itemMap.put("price", item.getPrice());
                itemMap.put("category", item.getCategory());

                cartRef.child(itemId).setValue(itemMap)
                        .addOnFailureListener(e -> Log.e(TAG, "Failed to add item: " + e.getMessage()));
            }
        }
    }

    public void removeItemByName(String itemName) {
        if (cartRef != null) {
            cartRef.orderByChild("name")
                    .equalTo(itemName)
                    .limitToFirst(1)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                snapshot.getRef().removeValue()
                                        .addOnFailureListener(e -> Log.e(TAG, "Failed to remove item: " + e.getMessage()));
                                break;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, "Error removing item: " + databaseError.getMessage());
                        }
                    });
        }
    }

    public void removeItem(int position) {
        if (position >= 0 && position < cartItems.size()) {
            String itemName = cartItems.get(position).getName();
            removeItemByName(itemName);
        }
    }

    public void clearCart() {
        if (cartRef != null) {
            cartRef.removeValue()
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to clear cart: " + e.getMessage()));
        }
    }

    public List<Item> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public double getTotalPrice() {
        double total = 0;
        for (Item item : cartItems) {
            total += item.getPrice();
        }
        return total;
    }

    public int getItemCount() {
        return cartItems.size();
    }

    public void addCartUpdateListener(CartUpdateListener listener) {
        listeners.add(listener);
        // Immediately notify with current cart state
        listener.onCartUpdated(new ArrayList<>(cartItems));
    }

    public void removeCartUpdateListener(CartUpdateListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (CartUpdateListener listener : listeners) {
            listener.onCartUpdated(new ArrayList<>(cartItems));
        }
    }

    public void cleanup() {
        if (cartRef != null && cartListener != null) {
            cartRef.removeEventListener(cartListener);
        }
        listeners.clear();
        cartItems.clear();
        instance = null;
    }

    // Reinitialize for new user after login
    public void reinitialize() {
        if (cartRef != null && cartListener != null) {
            cartRef.removeEventListener(cartListener);
        }
        cartItems.clear();
        initializeDatabase();
    }
}