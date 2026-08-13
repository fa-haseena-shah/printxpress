package com.example.printxpress;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.appcompat.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeActivity extends AppCompatActivity {

    ListView lvProducts;
    TextView tvUserName;
    DatabaseHelper dbHelper;
    List<Product> products;
    SearchView searchViewLocation;
    ImageView imgPromoOpen;
    Spinner categorySpinner;
    ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        searchViewLocation = findViewById(R.id.searchView);
        categorySpinner = findViewById(R.id.categorySpinner);
        imgPromoOpen = findViewById(R.id.imgPromoOpen);
        tvUserName = findViewById(R.id.tvUserName);
        lvProducts = findViewById(R.id.lvProducts); // list view
        dbHelper = new DatabaseHelper(this);

        // initilaize btm navbar
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        BottomNavHelper.setupNav(this, bottomNav, R.id.nav_home);

        // open promo activity
        imgPromoOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PromosActivity.class);
                startActivity(intent);
            }
        });

        PromoHelper promoHelper = new PromoHelper();
        // send a random toast ab promos from what are currently valid
        List<Promo> currentPromos = promoHelper.getValidPromos();
        if (!currentPromos.isEmpty()) {
            Random random = new Random();
            int index = random.nextInt(currentPromos.size());
            Promo promo = currentPromos.get(index);
            Toast.makeText(this,  (int) promo.getDiscountPercent() +
                            "% OFF if you use " + promo.getCode(), Toast.LENGTH_LONG).show();
        }

        // get all the products and store it into list
        products = dbHelper.getProducts();
        adapter = new ProductAdapter(this, products); // pass list to adapter
        lvProducts.setAdapter(adapter); // setting product cards into list view using the adapter

        // getting name we stored at login from shared prefs
        SharedPreferences sharedPreferences = getSharedPreferences("cusPrefs", MODE_PRIVATE);
        String name = sharedPreferences.getString("name", "PrintXpresser");
        if(!name.isEmpty()) {
            tvUserName.setText(name);
        }

        setUpSearch();
        setUpFilters();
    }

    // to enable search by product name
    private void setUpSearch() {
        searchViewLocation.setQueryHint("Search by name...");
        searchViewLocation.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts();
                return true;
            }
        });
    }

    private void setUpFilters() {
        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterProducts();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        categorySpinner.setOnItemSelectedListener(listener);
    }

    private void filterProducts() {
        // get customer input/selection
        String searchedText = searchViewLocation.getQuery().toString().toLowerCase();
        String selectedCategory = categorySpinner.getSelectedItem().toString();
        // store filter products into another list
        List <Product> filteredList = new ArrayList<>();

        for (Product product : products) {
            // check if search value is empty, or if partial/full match found
            boolean searchMatch= searchedText.isEmpty() || product.getName().toLowerCase().contains(searchedText);
            // check if category value is selected
            boolean categoryMatch = selectedCategory.equals("All") || product.getCategoryName().equalsIgnoreCase(selectedCategory);
            if (categoryMatch && searchMatch) {
                filteredList.add(product);
            }
        }
        // update the adapter so the searched/filtered results show
        adapter.updateList(filteredList);
    }
}