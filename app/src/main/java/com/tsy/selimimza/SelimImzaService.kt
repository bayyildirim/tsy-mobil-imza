package com.tsy.selimimza

import android.accessibilityservice.AccessibilityService
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class SelimImzaService : AccessibilityService() {

    private val IMZA = "*TSY SELİM*"
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

        // Metin ".." ile bitiyorsa ve içinde henüz imza yoksa devreye gir
        if (text.endsWith(TETIKLEYICI) && !text.contains("TSY SELİM")) {
            isUpdating = true
            
            // Sondaki iki noktayı (..) sil
            val temizMetin = text.substring(0, text.length - TETIKLEYICI.length)
            
            // İmzayı en başa ekle
            val yeniMetin = "$IMZA\n$temizMetin"
            
            val args = Bundle().apply {
                putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, yeniMetin)
            }
            editNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)

            // İmleci mesajın sonuna taşı
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
