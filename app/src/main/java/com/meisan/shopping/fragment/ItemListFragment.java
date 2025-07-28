package com.meisan.shopping.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import com.meisan.shopping.R;
import com.meisan.shopping.adapter.ItemAdapter;
import com.meisan.shopping.model.Item;

public class ItemListFragment extends Fragment {
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private List<Item> itemList;
    private List<Item> filteredItemList;
    private FloatingActionButton cartButton;
    public static List<Item> cartItems = new ArrayList<>();
    private Spinner categorySpinner;
    private FirebaseFirestore db;

    public ItemListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.loginFragment);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        cartButton = view.findViewById(R.id.cartButton);
        categorySpinner = view.findViewById(R.id.categorySpinner);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize lists
        itemList = new ArrayList<>();
        filteredItemList = new ArrayList<>();

        // Load items from Firestore
        loadItemsFromFirestore();

        itemAdapter = new ItemAdapter(filteredItemList, item -> {
            cartItems.add(item);
            Toast.makeText(getContext(), item.getName() + " added to cart", Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(itemAdapter);

        // Set up category spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"All", "Tech", "Clothing", "Home & Decor"});
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
                filterItems(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filterItems("All");
            }
        });

        cartButton.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_itemListFragment_to_cartFragment);
        });

        return view;
    }

    private void loadItemsFromFirestore() {
        db.collection("items")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        itemList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            Double price = document.getDouble("price");
                            String category = document.getString("category");

                            if (name != null && price != null && category != null) {
                                itemList.add(new Item(name, price, category));
                            }
                        }
                        // Update filtered list and notify adapter
                        filterItems("All");
                    } else {
                        Toast.makeText(getContext(), "Error loading items: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                        // Add sample data if Firestore fails
                        addSampleData();
                    }
                });
    }

    private void addSampleData() {
        // Fallback sample data if Firestore is empty or fails
        itemList.clear();
        itemList.add(new Item("Laptop", 999.99, "Tech"));
        itemList.add(new Item("Smartphone", 499.99, "Tech"));
        itemList.add(new Item("Headphones", 79.99, "Tech"));
        itemList.add(new Item("Tablet", 299.99, "Tech"));
        itemList.add(new Item("T-Shirt", 19.99, "Clothing"));
        itemList.add(new Item("Jacket", 89.99, "Clothing"));
        itemList.add(new Item("Sneakers", 59.99, "Clothing"));
        itemList.add(new Item("Table Lamp", 39.99, "Home & Decor"));
        itemList.add(new Item("Area Rug", 129.99, "Home & Decor"));
        itemList.add(new Item("Decorative Vase", 24.99, "Home & Decor"));

        filterItems("All");
    }

    private void filterItems(String category) {
        filteredItemList.clear();
        if (category.equals("All")) {
            filteredItemList.addAll(itemList);
        } else {
            for (Item item : itemList) {
                if (item.getCategory().equals(category)) {
                    filteredItemList.add(item);
                }
            }
        }
        if (itemAdapter != null) {
            itemAdapter.notifyDataSetChanged();
        }
    }
}