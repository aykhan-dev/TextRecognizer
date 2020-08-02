package ev.aykhan.textrecognizer.ui.activity.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import ev.aykhan.textrecognizer.R
import ev.aykhan.textrecognizer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val navController by lazy { findNavController(R.id.fragment_nav_host) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        bindUI()
        configureToolbar()
    }

    private fun bindUI(): Unit = with(binding) {
        lifecycleOwner = this@MainActivity
    }

    private fun configureToolbar(): Unit = with(binding.toolbar) {
        setupWithNavController(navController)
    }

}