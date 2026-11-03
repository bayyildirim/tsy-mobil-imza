package com.tsy.selimimza

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class SelimImzaService : AccessibilityService() {

    private val TETIKLEYICI = ".."
    private var isUpdating = false

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (isUpdating || event == null) return
        val pkg = event.packageName?.toString() ?: return
        if (pkg != "com.whatsapp.w4b" && pkg != "com.whatsapp") return

        val root = rootInActiveWindow ?: return
        val nodes = root.findAccessibilityNodeInfosByViewId("$pkg:id/entry")
        if (nodes.isNullOrEmpty()) return

        val editNode = nodes[0]
        if (!editNode.isFocused) return

        val text = editNode.text?.toString() ?: ""
        if (text.isEmpty() || editNode.isShowingHintText) return

        // İmzayı anlık olarak ayarlardan oku
        val sharedPref = getSharedPreferences("TSY_Ayarlar", Context.MODE_PRIVATE)
        val guncelImza = sharedPref.getString("imza_metni", "*TSY SELİM*") ?: "*TSY SELİM*"

        if (text.endsWith(TETIKLEYICI) && !text.contains(guncelImza)) {
            isUpdating = true
            val temizMetin = text.substring(0, text.length - TETIKLEYICI.length)
            
            val yeniMetin = if (guncelImza.isNotEmpty()) "$guncelImza\n$temizMetin" else temizMetin
            
            val args = Bundle().apply {
                putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, yeniMetin)
            }
            editNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)

            val selArgs = Bundle().apply {
                putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_START_INT, yeniMetin.length)
                putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_END_INT, yeniMetin.length)
            }
            editNode.performAction(AccessibilityNodeInfo.ACTION_SET_SELECTION, selArgs)
            
            isUpdating = false
        }
    }

    override fun onInterrupt() {}
}
