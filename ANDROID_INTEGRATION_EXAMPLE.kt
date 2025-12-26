// ========================================
// File: StoryApiService.kt (Android)
// Đặt trong package: com.example.pandora.data.network
// ========================================

package com.example.pandora.data.network

import com.example.pandora.data.entity.Story
import com.example.pandora.data.request.CreateStoryRequest
import com.example.pandora.data.response.ApiResponse
import retrofit2.Call
import retrofit2.http.*

interface StoryApiService {
    
    @POST("stories/create")
    fun createStory(@Body request: CreateStoryRequest): Call<ApiResponse<Story>>
    
    @GET("stories/{id}")
    fun getStoryById(@Path("id") id: Long): Call<ApiResponse<Story>>
    
    @GET("stories/user/{userId}")
    fun getUserStories(@Path("userId") userId: Long): Call<ApiResponse<List<Story>>>
    
    @GET("stories/topic/{topic}")
    fun getStoriesByTopic(@Path("topic") topic: String): Call<ApiResponse<List<Story>>>
    
    @DELETE("stories/{id}")
    fun deleteStory(@Path("id") id: Long): Call<ApiResponse<Void>>
    
    @POST("stories/audio")
    fun generateAudio(@Body request: TextToSpeechRequest): Call<ApiResponse<String>>
    
    @GET("stories/topics")
    fun getAvailableTopics(): Call<ApiResponse<List<String>>>
    
    @GET("stories/styles")
    fun getAvailableStyles(): Call<ApiResponse<List<String>>>
}

// ========================================
// File: ApiResponse.kt (Android)
// ========================================

package com.example.pandora.data.response

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)

// ========================================
// File: CreateStoryRequest.kt (Android)
// ========================================

package com.example.pandora.data.request

import com.google.gson.annotations.SerializedName

data class CreateStoryRequest(
    @SerializedName("topic")
    val topic: String,
    
    @SerializedName("style")
    val style: String,
    
    @SerializedName("userId")
    val userId: Long?,
    
    @SerializedName("characters")
    val characters: List<CharacterRequest>
)

data class CharacterRequest(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("age")
    val age: String,
    
    @SerializedName("gender")
    val gender: String,
    
    @SerializedName("role")
    val role: String
)

data class TextToSpeechRequest(
    @SerializedName("storyId")
    val storyId: Long,
    
    @SerializedName("language")
    val language: String
)

// ========================================
// File: RetrofitClient.kt (Android)
// ========================================

package com.example.pandora.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    
    // Thay YOUR_SERVER_IP bằng IP máy chủ của bạn
    // Nếu test local: 10.0.2.2 (Android Emulator) hoặc 192.168.x.x (Real Device)
    private const val BASE_URL = "http://YOUR_SERVER_IP:8080/api/"
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val storyService: StoryApiService = retrofit.create(StoryApiService::class.java)
}

// ========================================
// File: StoryRepository.kt (Android)
// ========================================

package com.example.pandora.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pandora.data.entity.Story
import com.example.pandora.data.network.RetrofitClient
import com.example.pandora.data.request.CreateStoryRequest
import com.example.pandora.data.response.ApiResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryRepository {
    
    private val storyService = RetrofitClient.storyService
    
    fun createStory(request: CreateStoryRequest): LiveData<Result<Story>> {
        val result = MutableLiveData<Result<Story>>()
        
        storyService.createStory(request).enqueue(object : Callback<ApiResponse<Story>> {
            override fun onResponse(
                call: Call<ApiResponse<Story>>,
                response: Response<ApiResponse<Story>>
            ) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val story = response.body()?.data
                    if (story != null) {
                        result.value = Result.success(story)
                    } else {
                        result.value = Result.failure(Exception("Story data is null"))
                    }
                } else {
                    result.value = Result.failure(
                        Exception(response.body()?.message ?: "Unknown error")
                    )
                }
            }
            
            override fun onFailure(call: Call<ApiResponse<Story>>, t: Throwable) {
                Log.e("StoryRepository", "Error creating story", t)
                result.value = Result.failure(t)
            }
        })
        
        return result
    }
    
    fun getStoryById(id: Long): LiveData<Result<Story>> {
        val result = MutableLiveData<Result<Story>>()
        
        storyService.getStoryById(id).enqueue(object : Callback<ApiResponse<Story>> {
            override fun onResponse(
                call: Call<ApiResponse<Story>>,
                response: Response<ApiResponse<Story>>
            ) {
                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.data?.let {
                        result.value = Result.success(it)
                    }
                } else {
                    result.value = Result.failure(Exception("Failed to get story"))
                }
            }
            
            override fun onFailure(call: Call<ApiResponse<Story>>, t: Throwable) {
                result.value = Result.failure(t)
            }
        })
        
        return result
    }
    
    fun getUserStories(userId: Long): LiveData<Result<List<Story>>> {
        val result = MutableLiveData<Result<List<Story>>>()
        
        storyService.getUserStories(userId).enqueue(object : Callback<ApiResponse<List<Story>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<Story>>>,
                response: Response<ApiResponse<List<Story>>>
            ) {
                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.data?.let {
                        result.value = Result.success(it)
                    } ?: run {
                        result.value = Result.success(emptyList())
                    }
                } else {
                    result.value = Result.failure(Exception("Failed to get stories"))
                }
            }
            
            override fun onFailure(call: Call<ApiResponse<List<Story>>>, t: Throwable) {
                result.value = Result.failure(t)
            }
        })
        
        return result
    }
}

// ========================================
// File: CreateStoryViewModel.kt (Android)
// ========================================

package com.example.pandora.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pandora.data.entity.Story
import com.example.pandora.data.repository.StoryRepository
import com.example.pandora.data.request.CharacterRequest
import com.example.pandora.data.request.CreateStoryRequest

class CreateStoryViewModel : ViewModel() {
    
    private val repository = StoryRepository()
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    fun createStory(
        topic: String,
        style: String,
        characters: List<CharacterRequest>,
        userId: Long?
    ): LiveData<Result<Story>> {
        _isLoading.value = true
        
        val request = CreateStoryRequest(
            topic = topic,
            style = style,
            userId = userId,
            characters = characters
        )
        
        val result = repository.createStory(request)
        
        result.observeForever { outcome ->
            _isLoading.value = false
            outcome.onFailure { throwable ->
                _error.value = throwable.message
            }
        }
        
        return result
    }
}

// ========================================
// File: CreateStoryActivity.kt - UPDATED (Android)
// ========================================

package ui.story

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pandora.R
import com.example.pandora.data.entity.CharacterModel
import com.example.pandora.data.request.CharacterRequest
import com.example.pandora.ui.adapter.CharacterAdapter
import com.example.pandora.viewmodel.CreateStoryViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.progressindicator.CircularProgressIndicator

class CreateStoryActivity : AppCompatActivity() {
    
    private val viewModel: CreateStoryViewModel by viewModels()
    private lateinit var adapter: CharacterAdapter
    private val characterList = mutableListOf<CharacterModel>()
    
    private var selectedTopic: String = "Animals"
    private var selectedStyle: String = "Funny"
    
    private lateinit var progressIndicator: CircularProgressIndicator
    private lateinit var btnGenerate: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_story)
        
        setupRecyclerView()
        setupTopicSelection()
        setupStyleSelection()
        setupGenerateButton()
        observeViewModel()
        
        findViewById<View>(R.id.btnAddCharacter).setOnClickListener {
            showAddCharacterDialog()
        }
    }
    
    private fun setupRecyclerView() {
        val rv = findViewById<RecyclerView>(R.id.rvHeroes)
        adapter = CharacterAdapter(characterList)
        rv.layoutManager = GridLayoutManager(this, 3)
        rv.adapter = adapter
    }
    
    private fun setupTopicSelection() {
        val chipGroup = findViewById<ChipGroup>(R.id.chipGroupTopics)
        chipGroup.setOnCheckedChangeListener { _, checkedId ->
            val chip = findViewById<Chip>(checkedId)
            selectedTopic = chip.text.toString().split(" ")[0] // Remove emoji
        }
    }
    
    private fun setupStyleSelection() {
        // Handle style button clicks
        // Update selectedStyle when a style button is clicked
    }
    
    private fun setupGenerateButton() {
        btnGenerate = findViewById(R.id.btnGenerate)
        progressIndicator = CircularProgressIndicator(this)
        
        btnGenerate.setOnClickListener {
            if (characterList.isEmpty()) {
                Toast.makeText(this, "Please add at least one character!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            generateStory()
        }
        
        animateSparkle(btnGenerate)
    }
    
    private fun generateStory() {
        val characters = characterList.map { char ->
            CharacterRequest(
                name = char.name,
                age = char.age,
                gender = char.gender,
                role = char.role
            )
        }
        
        val userId = getUserId() // Get from SharedPreferences or session
        
        viewModel.createStory(selectedTopic, selectedStyle, characters, userId)
            .observe(this) { result ->
                result.onSuccess { story ->
                    Toast.makeText(this, "Story created! ✨", Toast.LENGTH_SHORT).show()
                    
                    // Navigate to ReadingActivity
                    val intent = Intent(this, ReadingActivity::class.java)
                    intent.putExtra("STORY_ID", story.id)
                    startActivity(intent)
                    finish()
                }
                
                result.onFailure { error ->
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
    
    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            btnGenerate.isEnabled = !isLoading
            btnGenerate.text = if (isLoading) "Creating Magic... ✨" else "MAGIC CREATE! ✨"
            // Show/hide progress indicator
        }
        
        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun showAddCharacterDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_character, null)
        val dialog = AlertDialog.Builder(this).setView(view).create()
        
        if (dialog.window != null) {
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
        
        val edtName = view.findViewById<EditText>(R.id.dialogEdtName)
        val edtAge = view.findViewById<EditText>(R.id.dialogEdtAge)
        val rbBoy = view.findViewById<RadioButton>(R.id.rbBoy)
        
        view.findViewById<View>(R.id.dialogBtnAdd).setOnClickListener {
            val name = edtName.text.toString()
            val age = edtAge.text.toString()
            val gender = if (rbBoy.isChecked) "Boy" else "Girl"
            
            if (name.isNotEmpty()) {
                characterList.add(
                    CharacterModel(name, "Hero", age, gender, R.drawable.ic_dino_color)
                )
                adapter.notifyDataSetChanged()
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show()
            }
        }
        
        view.findViewById<View>(R.id.dialogBtnCancel).setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }
    
    private fun animateSparkle(v: View) {
        val anim = ObjectAnimator.ofFloat(v, "alpha", 0.8f, 1.0f)
        anim.duration = 800
        anim.repeatCount = ValueAnimator.INFINITE
        anim.repeatMode = ValueAnimator.REVERSE
        anim.start()
    }
    
    private fun getUserId(): Long? {
        // Get from SharedPreferences or session
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        return prefs.getLong("user_id", -1).takeIf { it != -1L }
    }
}

// ========================================
// Thêm vào build.gradle.kts (Module: app)
// ========================================

dependencies {
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    
    // OkHttp Logging
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    
    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.16.0")
    
    // ViewModel and LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    
    // Existing dependencies...
}
