package com.selim.basicexample.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.selim.basicexample.R
import com.selim.basicexample.adapter.BasketAdapter
import com.selim.basicexample.model.Coffee

class BasketActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basket)
        val recyclerViewBasket by lazy { findViewById<RecyclerView>(R.id.basketRecyclerView) }
        val totalBasket by lazy { findViewById<TextView>(R.id.total_basket) }

        var _totalBasket:Double=0.0
        val basketList=getIntent().getSerializableExtra("list") as ArrayList<Coffee>

        if (basketList != null) {
            basketList.forEach {
                _totalBasket+=it.price!!.toDouble()
            }
            totalBasket.text="Toplam Tutar "+_totalBasket.toString()+"₺"
            val layoutManager= GridLayoutManager(this,2)
            recyclerViewBasket.layoutManager=layoutManager

            val adapter= BasketAdapter(basketList)
            recyclerViewBasket.adapter=adapter
        }
    }
}