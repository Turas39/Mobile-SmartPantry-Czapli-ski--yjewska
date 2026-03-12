package com.example.mobile_smart_pantry_project_iv

import android.os.Bundle
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

        binding.addButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val quantity = binding.quantityEditText.text.toString().toInt()
            val category = binding.categorySpinner.selectedItem.toString()

            val product = Product (
                uuid = UUID.randomUUID().toString(),
                name = name,
                quantity = quantity,
                category = category,
                imageRef = ""
            )

            inventoryList.add(product)
            saveInventoryToJsonFile()

            productTitles.add("${product.name} (${product.quantity})")
            listAdapter.notifyDataSetChanged()
        }

        binding.consumeButton.setOnClickListener {
            val position = binding.productListView.checkedItemPosition
            if(position != -1) {
                val product = inventoryList[position]

                val updateProduct = product.copy(
                    quantity = product.quantity - 1
                )
                inventoryList[position] = updateProduct
                productTitles[position] = "${updateProduct.name} (${updateProduct.quantity})"
                listAdapter.notifyDataSetChanged()
                saveInventoryToJsonFile()
            }
        }


        loadInventoryFromJsonFile()
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
}