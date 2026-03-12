package com.example.mobile_smart_pantry_project_iv

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.mobile_smart_pantry_project_iv.model.Product
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class PantryAdapter(
    private val context: Context,
    private val products: MutableList<Product>
) : BaseAdapter() {

    private val inflater = LayoutInflater.from(context)

    override fun getCount(): Int = products.size
    override fun getItem(position: Int): Product = products[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: inflater.inflate(R.layout.item_product, parent, false)
        val product = products[position]

        val nameText = view.findViewById<TextView>(R.id.productName)
        val quantityText = view.findViewById<TextView>(R.id.productQuantity)
        val categoryText = view.findViewById<TextView>(R.id.productCategory)
        val addButton = view.findViewById<Button>(R.id.addButton)
        val consumeButton = view.findViewById<Button>(R.id.consumeButton)

        nameText.text = product.name
        quantityText.text = product.quantity.toString()
        categoryText.text = product.category.capitalize()

        addButton.setOnClickListener {
            products[position] = product.copy(quantity = product.quantity + 1)
            notifyDataSetChanged()
            saveInventoryToJsonFile()
        }

        consumeButton.setOnClickListener {
            if (product.quantity > 0) {
                products[position] = product.copy(quantity = product.quantity - 1)
                notifyDataSetChanged()
                saveInventoryToJsonFile()
            }
        }

        if (product.quantity <= 3) {
            quantityText.setTextColor(android.graphics.Color.RED)
        } else {
            quantityText.setTextColor(android.graphics.Color.WHITE)
        }


        return view
    }

    private fun saveInventoryToJsonFile() {
        try {
            val json = Json { prettyPrint = true }
            val jsonString = json.encodeToString(products)
            val file = File(context.filesDir, "inventory.json")
            file.writeText(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateList(newList: List<Product>) {
        products.clear()
        products.addAll(newList)
        notifyDataSetChanged()
    }
}