package com.finance.valuespend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.finance.valuespend.ui.navigation.ValueSpendRoot
import com.finance.valuespend.ui.theme.ValueSpendTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ValueSpendTheme {
                ValueSpendRoot()
            }
        }
    }
}