package com.example.mobile_smart_pantry_project_iv

import android.os.Bundle
import android.widget.SearchView
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
import java.io.InputStreamReader
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private val inventoryList = mutableListOf<Product>()
    private lateinit var adapter: PantryAdapter
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = PantryAdapter(this, mutableListOf())
        binding.productListView.adapter = adapter

        setupSearchView()
        setupRadioFilters()
        loadInventoryFromJsonFile()
    }

    private fun searchName(query: String): List<Product> {
        return inventoryList.filter { it.name.contains(query, ignoreCase = true) }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredList = if (!newText.isNullOrEmpty()) searchName(newText) else inventoryList
                adapter.updateList(filteredList)
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
                resources.openRawResource(R.raw.pantry).bufferedReader().use { it.readText()}
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

    private fun filterFood(): List<Product> {
        return inventoryList.filter { it.category.equals("Food", true) }
    }

    private fun filterOxygen(): List<Product> {
        return inventoryList.filter { it.category.equals("Life Support", true) }
    }

    private fun setupRadioFilters() {

        binding.filterGroup.setOnCheckedChangeListener { _, checkedId ->

            val filteredList = when (checkedId) {

                R.id.foodRadio -> filterFood()

                R.id.oxygenRadio -> filterOxygen()

                R.id.allRadio -> inventoryList

                else -> inventoryList
            }

            adapter.updateList(filteredList)
        }
    }


}