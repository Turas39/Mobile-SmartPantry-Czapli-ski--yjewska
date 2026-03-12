package com.example.mobile_smart_pantry_project_iv

import android.app.Activity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mobile_smart_pantry_project_iv.databinding.ActivityAddProductBinding
import com.example.mobile_smart_pantry_project_iv.model.Product
import java.util.UUID

class AddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val categories = resources.getStringArray(R.array.product_categories)
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = spinnerAdapter

        binding.saveProductButton.setOnClickListener {
            val name = binding.editName.text.toString()
            val quantityText = binding.editQuantity.text.toString()
            val category = binding.spinnerCategory.selectedItem.toString()
            val imageRef = binding.editImageRef.text.toString()

            if (name.isBlank()) {
                Toast.makeText(this, "Podaj nazwę produktu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (quantityText.isBlank()) {
                Toast.makeText(this, "Podaj ilość", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newProduct = Product(
                uuid = UUID.randomUUID().toString(),
                name = name,
                quantity = quantityText.toInt(),
                category = category,
                imageRef = imageRef
            )

            val returnIntent = android.content.Intent()
            returnIntent.putExtra(Keys.NEW_PRODUCT, newProduct)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }
    }
}