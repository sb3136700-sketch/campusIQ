package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.presentation.CampusApp
import com.example.presentation.viewmodel.AiViewModel
import com.example.presentation.viewmodel.AuthViewModel
import com.example.presentation.viewmodel.CampusViewModel
import com.example.presentation.viewmodel.CampusViewModelFactory
import com.example.ui.theme.CampusIqTheme

class MainActivity : ComponentActivity() {

    private val app by lazy { application as CampusApplication }

    private val factory by lazy {
        CampusViewModelFactory(
            authRepository = app.authRepository,
            campusRepository = app.campusRepository,
            aiRepository = app.aiRepository
        )
    }

    private val authViewModel: AuthViewModel by viewModels { factory }
    private val campusViewModel: CampusViewModel by viewModels { factory }
    private val aiViewModel: AiViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CampusIqTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CampusApp(
                        authViewModel = authViewModel,
                        campusViewModel = campusViewModel,
                        aiViewModel = aiViewModel
                    )
                }
            }
        }
    }
}
