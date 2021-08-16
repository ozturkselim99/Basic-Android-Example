package com.selim.basicexample.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.selim.basicexample.R
import com.selim.basicexample.adapter.CategoryAdapter
import com.selim.basicexample.adapter.CategoryMenuAdapter
import com.selim.basicexample.adapter.CoffeeAdapter
import com.selim.basicexample.adapter.CoffeeHomeAdapter
import com.selim.basicexample.data.MockData
import com.selim.basicexample.model.Coffee
import com.selim.basicexample.model.CoffeeCategory
import kotlinx.android.synthetic.main.activity_coffee_category.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var auth: FirebaseAuth? = null
    private val buttonSignOut: Button by lazy { findViewById(R.id.button_sign_out) }
    var firestore: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        firestore = FirebaseFirestore.getInstance()
        Log.d("MainActivityLIFECYCLE", "onCreate")
        checkUser()

        //Sepette gösterilecek ürünler
        var basketList = arrayListOf<Coffee>()

        //Toplam sepet miktarı
        var totalBasket: Double = 0.0

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Adapter
        val layoutManager = LinearLayoutManager(this)
        recycler_view_product.layoutManager = layoutManager

        val coffeeAdapter = CoffeeHomeAdapter(MockData.getCoffeeList())
        recycler_view_product.adapter = coffeeAdapter

        val gridLayoutManager = GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false)
        recycler_view_category.layoutManager = gridLayoutManager

        // todo: berkhan firestore çekelim CoffeeCategoryActivity
        val categoryAdapter = CategoryAdapter(MockData.getCoffeeCategories()) { categoryId ->
            getCoffeesAsCategory(categoryId)
        }

        recycler_view_category.adapter = categoryAdapter

        //Adapter içindeki total değişkenimizi gözlemliyoruz. Değişkende bir değişiklik olduğunda activity_xml içindeki total_price textini değiştiriyoruz.
        coffeeAdapter.total.observe(this, Observer {
            total_price.text = "Toplam Tutar: " + it.toString() + "₺"
            totalBasket = it
        })
        coffeeAdapter.basket.observe(this, Observer {
            basketList = it
        })

        //Siparis listesine gitme ve veri yollama
        list.setOnClickListener {
            val intent = Intent(this, BasketActivity::class.java)
            intent.putExtra("list", basketList)
            startActivity(intent)
        }

        buttonSignOut.setOnClickListener {
            auth?.signOut()
            checkUser()
        }

        btn_address.setOnClickListener {
            val intent = Intent(this, AddressActivity::class.java)
            startActivity(intent)
        }

        btn_categories.setOnClickListener {
            val intent = Intent(this, CoffeeCategoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        getCategories()
    }

    private fun getCategories() {
        firestore?.collection("category")?.get()?.addOnSuccessListener { snapshot ->
            val list = arrayListOf<CoffeeCategory>()

            snapshot.documents.forEach { documentSnapshot ->
                documentSnapshot.toObject(CoffeeCategory::class.java)?.let { category ->
                    category.id = documentSnapshot.id
                    list.add(category)
                }
            }

            loadCategories(list)

        }
    }

    private fun loadCategories(list: ArrayList<CoffeeCategory>) {
        val categoryMenuAdapter = CategoryMenuAdapter(this, list)
        recycler_view_category.adapter = categoryMenuAdapter
    }

    private fun getCoffeesAsCategory(categoryId: String) {
        //todo: selim firestore CoffeesActivity
    }

    private fun checkUser() {
        auth = Firebase.auth

        if (auth?.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}

