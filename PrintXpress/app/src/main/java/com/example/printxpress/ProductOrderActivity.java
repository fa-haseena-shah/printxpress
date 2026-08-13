package com.example.printxpress;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProductOrderActivity extends AppCompatActivity {
    LinearLayout layoutSpecs;
    EditText etCustomText, etQty;
    TextView tvProductName, tvBack, tvSelectedImgName;
    Button btnUploadArtwork, btnPlaceOrder;
    // stores one RadioGroup per spec name : used later to capture selections
    private Map<String, RadioGroup> specRadioGroups = new LinkedHashMap<>();
    DatabaseHelper databaseHelper;
    private int productId, customerId;
    private int selectedDesignId = -1;
    private ActivityResultLauncher<Intent> designPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_order);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        layoutSpecs = findViewById(R.id.layoutSpecs);
        etCustomText = findViewById(R.id.etCustomTxt);
        etQty = findViewById(R.id.etQty);
        btnUploadArtwork = findViewById(R.id.btnUploadArtwork);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        tvProductName = findViewById(R.id.tvProductName);
        tvBack = findViewById(R.id.tvBack);
        tvSelectedImgName = findViewById(R.id.tvSelectedImgName);
        databaseHelper = new DatabaseHelper(this);
        productId = getIntent().getIntExtra("product_id", -1);

        // get product object
        Product product = databaseHelper.getProductById(productId);
        if(product!=null) {
            tvProductName.setText(product.getName());
        } else {
            tvProductName.setText(" ");
        }

        // back button
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProductDetailActivity.class);
                intent.putExtra("product_id", productId);
                startActivity(intent);
            }
        });

        loadSpecs();

        designPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedDesignId = result.getData().getIntExtra("design_id", -1);
                        Design design = databaseHelper.getDesignById(selectedDesignId);
                        if(design!=null) {
                            tvSelectedImgName.setText(design.getTitle());
                        } else {
                            tvSelectedImgName.setText(" ");
                        }
                        Toast.makeText(ProductOrderActivity.this, "Design selected!", Toast.LENGTH_SHORT).show();
                    }
                });

        // add design
        btnUploadArtwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectDesignActivity.class);
                intent.putExtra("product_id", productId);
                designPickerLauncher.launch(intent);
            }
        });

        // confirm order details and goo to next activity
        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmOrder();
            }
        });
    }

    // get specs for this specific product, create ui for it dynamically
    private void loadSpecs() {
        // get specList for this specific product
        List<Specification> specList = databaseHelper.getSpecsByProductId(productId);

        // group spec values by spec nam
        // using linked hash map to store the specs as key=> value pairs i.e. spec=>spec value opt
        Map<String, List<Specification>> grouped = new LinkedHashMap<>();
        // group specs with the same name into a single list
        for (Specification spec : specList) {
            if (!grouped.containsKey(spec.getSpecName())) { // create one if it doesnt exist alreadyy
                grouped.put(spec.getSpecName(), new ArrayList<>());
            }
            grouped.get(spec.getSpecName()).add(spec); // simply add directly
        }

        // build UI
        for (Map.Entry<String, List<Specification>> entry : grouped.entrySet()) {
            String specName = entry.getKey();
            List<Specification> options = entry.getValue();

            // spec name label
            TextView tvSpecName = new TextView(this);
            tvSpecName.setText(specName);
            tvSpecName.setTextSize(14);
            tvSpecName.setPadding(0, 16, 0, 8);
            layoutSpecs.addView(tvSpecName);

            // radio group for options
            RadioGroup radioGroup = new RadioGroup(this);
            radioGroup.setOrientation(RadioGroup.HORIZONTAL);

            for (Specification spec : options) {
                RadioButton radioButton = new RadioButton(this);
                radioButton.setId(View.generateViewId()); // unique view id to prevent any collisions
                radioButton.setTag(spec.getSpecId()); // store spec_id here instead
                radioButton.setText(spec.getSpecValue());
                radioGroup.addView(radioButton);
            }
            layoutSpecs.addView(radioGroup);
            specRadioGroups.put(specName, radioGroup); // store for later use
        }
    }

    // get specifications customer selected
    private List<Integer> getSelectedSpecs() {
        List<Integer> selectedSpecIds = new ArrayList<>();
        for (Map.Entry<String, RadioGroup> entry : specRadioGroups.entrySet()) {
            RadioGroup group = entry.getValue();
            int checkedId = group.getCheckedRadioButtonId();
            if (checkedId != -1) {
                RadioButton selected = group.findViewById(checkedId);
                int specId = (int) selected.getTag();
                selectedSpecIds.add(specId);
            }
        }
        return selectedSpecIds;
    }

    private boolean confirmOrder() {
        List<Integer> selectedSpecIds = getSelectedSpecs(); // using the selected spec ids

        // validations
        if (selectedSpecIds.size() < specRadioGroups.size()) {
            // user must select all specs so we check if seelctedSpecId size = radio group size
            Toast.makeText(ProductOrderActivity.this, "Please select all specifications", Toast.LENGTH_SHORT).show();
            return false;
        }

        String qtyStr = etQty.getText().toString().trim();
        if (qtyStr.isEmpty()) { // empty qty
            Toast.makeText(ProductOrderActivity.this, "Please enter quantity", Toast.LENGTH_SHORT).show();
            return false;
        }

        int qty = Integer.parseInt(qtyStr);
        if (qty < 1) { // qty greater than 1
            Toast.makeText(ProductOrderActivity.this, "Minimum order quantity is 1...", Toast.LENGTH_SHORT).show();
            return false;
        }

        String customText = etCustomText.getText().toString().trim();
        boolean hasCustomText = !customText.isEmpty();  // if customText is empty -> true then hasCustomText -> false
        boolean hasDesign = selectedDesignId != -1;  // if selectedDesignId is not -1 (actual designId was passed) then hasDesign -> true

        if (!hasCustomText && !hasDesign) {
            Toast.makeText(ProductOrderActivity.this, "Please upload artwork or enter custom text", Toast.LENGTH_SHORT).show();
            return false;
        }

        // product object to get its price and store as unit price + calculate total
        Product product = databaseHelper.getProductById(productId);
        // in case user quits before delivery, we insert the entire order record in DeliveryActivity only
        // so we forward order related deets to deliveery activity
        // still show a toast for better ux
        Toast.makeText(ProductOrderActivity.this, "Almost there!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), DeliveryActivity.class);
        intent.putExtra("product_id", productId);
        intent.putExtra("qty", qty);
        intent.putExtra("custom_text", hasCustomText ? customText : "");
        intent.putExtra("design_id", hasDesign ? selectedDesignId : -1);
        intent.putExtra("unit_price", product.getBasePrice());
        intent.putIntegerArrayListExtra("spec_ids", new ArrayList<>(selectedSpecIds));
        startActivity(intent);
        return true;
    }
}