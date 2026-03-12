package com.example.mobile_smart_pantry_project_iv

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.ImageView
import com.example.mobile_smart_pantry_project_iv.model.Product

class PantryAdapter(
    private val context: Context,
    private val products: MutableList<Product>,
    private val onDataChanged: () -> Unit
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

        val productImage = view.findViewById<ImageView>(R.id.productImage)

        val imageResId = context.resources.getIdentifier(
            product.imageRef, "drawable", context.packageName
        )
        if (imageResId != 0) {
            productImage.setImageResource(imageResId)
        } else {
            productImage.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        addButton.setOnClickListener {
            products[position] = product.copy(quantity = product.quantity + 1)
            notifyDataSetChanged()
            onDataChanged()
        }

        consumeButton.setOnClickListener {
            if (product.quantity > 0) {
                products[position] = product.copy(quantity = product.quantity - 1)
                notifyDataSetChanged()
                onDataChanged()
            }
        }

        if (product.quantity < 5) {
            quantityText.setTextColor(android.graphics.Color.RED)
        } else {
            quantityText.setTextColor(android.graphics.Color.WHITE)
        }

        return view
    }

    fun updateList(newList: List<Product>) {
        products.clear()
        products.addAll(newList)
        notifyDataSetChanged()
    }
}