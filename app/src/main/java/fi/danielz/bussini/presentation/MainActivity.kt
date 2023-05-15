package fi.danielz.bussini.presentation

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import fi.danielz.bussini.R

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onBackPressed() {
        val navCtrl = findNavController(R.id.navHost)
        val isLastFragment =
            navCtrl.previousBackStackEntry?.destination == null
        if (isLastFragment) {
            super.onBackPressedDispatcher.onBackPressed()
        } else {
            navCtrl.popBackStack()
        }
    }
}