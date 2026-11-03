package com.tsy.selimimza

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.graphics.Color
import android.view.Gravity

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(60, 100, 60, 60)
            setBackgroundColor(Color.parseColor("#F5F5F5"))
        }
        
        val baslik = TextView(this).apply {
            text = "İmza Ayarları"
            textSize = 28f
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 50)
        }
        
        val aciklama = TextView(this).apply {
            text = "Mesajın sonuna .. yazdığınızda metnin en başına eklenecek imzayı aşağıya girin:"
            textSize = 16f
            setTextColor(Color.DKGRAY)
            setPadding(0, 0, 0, 30)
        }

        // Hafızadaki mevcut imzayı çek (Yoksa varsayılan TSY SELİM)
        val sharedPref = getSharedPreferences("TSY_Ayarlar", Context.MODE_PRIVATE)
        val mevcutImza = sharedPref.getString("imza_metni", "*TSY SELİM*")

        val girdi = EditText(this).apply {
            setText(mevcutImza)
            textSize = 18f
            setBackgroundColor(Color.WHITE)
            setPadding(40, 40, 40, 40)
        }

        val bosluk = TextView(this).apply { height = 50 }

        val kaydetButon = Button(this).apply {
            text = "AYARLARI KAYDET"
            setBackgroundColor(Color.parseColor("#007BFF"))
            setTextColor(Color.WHITE)
            setPadding(30, 30, 30, 30)
            setOnClickListener {
                val yeni = girdi.text.toString().trim()
                sharedPref.edit().putString("imza_metni", yeni).apply()
                Toast.makeText(context, "Yeni imza kaydedildi!", Toast.LENGTH_LONG).show()
            }
        }

        layout.addView(baslik)
        layout.addView(aciklama)
        layout.addView(girdi)
        layout.addView(bosluk)
        layout.addView(kaydetButon)

        setContentView(layout)
    }
}
