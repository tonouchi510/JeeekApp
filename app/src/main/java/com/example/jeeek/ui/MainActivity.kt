package com.example.jeeek.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.example.jeeek.R
import com.example.jeeek.adapter.ViewPagerAdapter
import com.example.jeeek.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var adapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_main
        )
        tabLayout = binding.tabLayout
        viewPager = binding.pager
        adapter = ViewPagerAdapter(supportFragmentManager, this)

        // Add fragments
        adapter.addFragment(FragmentTimeline(), "Home")

        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(pager)
    }
}
