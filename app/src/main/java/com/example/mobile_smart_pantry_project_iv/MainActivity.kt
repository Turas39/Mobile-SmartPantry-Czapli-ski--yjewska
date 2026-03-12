package com.example.mobile_smart_pantry_project_iv

import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mobile_smart_pantry_project_iv.databinding.ActivityMainBinding
import com.example.mobile_smart_pantry_project_iv.model.Product
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private val inventoryList = mutableListOf<Product>()
    private lateinit var adapter: PantryAdapter
    lateinit var binding: ActivityMainBinding

    private var currentQuery: String = ""
    private var currentCategory: String = "all"


    private val addProductLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val newProduct = if (android.os.Build.VERSION.SDK_INT >= 33) {
                result.data?.getSerializableExtra(Keys.NEW_PRODUCT, Product::class.java)
            } else {
                @Suppress("DEPRECATION")
                result.data?.getSerializableExtra(Keys.NEW_PRODUCT) as? Product
            }
            if (newProduct != null) {
                inventoryList.add(newProduct)
                saveInventoryToJsonFile()
                applyFilters()
                Toast.makeText(this, "Dodano: ${newProduct.name}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = PantryAdapter(this, mutableListOf()) { saveInventoryToJsonFile() }
        binding.productListView.adapter = adapter

        setupSearchView()
        setupRadioFilters()
        loadInventoryFromJsonFile()

        binding.addProductButton.setOnClickListener {
            val intent = android.content.Intent(this, AddProductActivity::class.java)
            addProductLauncher.launch(intent)
        }
    }

    private fun applyFilters() {
        var filtered = when (currentCategory) {
            "food" -> inventoryList.filter { it.category.equals("Food", true) }
            "oxygen" -> inventoryList.filter { it.category.equals("Life Support", true) }
            else -> inventoryList.toList()
        }
        if (currentQuery.isNotEmpty()) {
            filtered = filtered.filter { it.name.contains(currentQuery, ignoreCase = true) }
        }
        adapter.updateList(filtered)
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                currentQuery = newText ?: ""
                applyFilters()
                return true
            }
        })
    }

    private fun loadInventoryFromJsonFile() {
        try {
            val file = File(filesDir, "inventory.json")
            val json = Json { ignoreUnknownKeys = true }

            val jsonString = if (file.exists()) {
                file.readText()
            } else {
                resources.openRawResource(R.raw.pantry).bufferedReader().use { it.readText() }
            }

            val loadedList = json.decodeFromString<List<Product>>(jsonString)
            inventoryList.clear()
            inventoryList.addAll(loadedList)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        adapter.updateList(inventoryList)
    }

    private fun saveInventoryToJsonFile() {
        try {
            val json = Json { prettyPrint = true }
            val jsonString = json.encodeToString(inventoryList)
            val file = File(filesDir, "inventory.json")
            file.writeText(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupRadioFilters() {
        binding.filterGroup.setOnCheckedChangeListener { _, checkedId ->
            currentCategory = when (checkedId) {
                R.id.foodRadio -> "food"
                R.id.oxygenRadio -> "oxygen"
                else -> "all"
            }
            applyFilters()
        }
    }
}