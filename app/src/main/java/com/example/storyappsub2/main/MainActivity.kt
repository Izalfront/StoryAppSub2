package com.example.storyappsub2.main

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyappsub2.R
import com.example.storyappsub2.UserPreference
import com.example.storyappsub2.api.ApiClient
import com.example.storyappsub2.databinding.ActivityMainBinding
import com.example.storyappsub2.story.StoryAdapter
import com.example.storyappsub2.story.StoryLoadStateAdapter
import com.example.storyappsub2.story.StoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userPreference: UserPreference
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        userPreference = UserPreference.getInstance(applicationContext.dataStore)
        storyAdapter = StoryAdapter { story ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_STORY, story)
            startActivity(intent)
        }

        binding.rvStories.layoutManager = LinearLayoutManager(this)
        binding.rvStories.adapter = storyAdapter.withLoadStateFooter(
            footer = StoryLoadStateAdapter { storyAdapter.retry() }
        )

        userPreference.token.asLiveData().observe(this) { token ->
            if (token == null) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                fetchStories(token)
            }
        }

        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivityForResult(intent, ADD_STORY_REQUEST_CODE)
        }
    }

    private fun fetchStories(token: String) {
        lifecycleScope.launch {
            StoryRepository.getInstance(ApiClient.apiService, userPreference)
                .fetchStories(token).collectLatest { pagingData ->
                    storyAdapter.submitData(pagingData)
                    runLayoutAnimation()
                }
        }
    }

    private fun runLayoutAnimation() {
        val context = binding.rvStories.context
        val controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
        binding.rvStories.layoutAnimation = controller
        binding.rvStories.adapter?.notifyDataSetChanged()
        binding.rvStories.scheduleLayoutAnimation()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            R.id.action_maps -> {
                startActivity(Intent(this, MapsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        CoroutineScope(Dispatchers.IO).launch {
            userPreference.clearToken()
            runOnUiThread {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_STORY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            userPreference.token.asLiveData().observe(this) { token ->
                if (token != null) {
                    fetchStories(token)
                }
            }
        }
    }

    companion object {
        const val ADD_STORY_REQUEST_CODE = 1
    }
}
