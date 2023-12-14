package com.example.loadinganimation

import android.os.Bundle
import androidx.activity.ComponentActivity

import com.example.mytestpeoject.R

class LottieAnimation : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.lottie_loading)

		/*setContent {
			LOadingAnimationTheme {
				// A surface container using the 'background' color from the theme
				Surface(
					modifier = Modifier.fillMaxSize(),
					color = MaterialTheme.colorScheme.background
				) {
					Greeting("Android")
				}
			}
		}*/
	}
}

