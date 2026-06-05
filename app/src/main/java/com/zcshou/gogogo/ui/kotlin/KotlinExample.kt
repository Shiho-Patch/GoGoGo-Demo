package com.zcshou.gogogo.ui.kotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zcshou.gogogo.databinding.ActivityKotlinExampleBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Kotlin 使用示例
 * <p>
 * 这个类展示了如何在项目中使用 Kotlin
 * 包含 ViewBinding、协程等现代 Android 开发特性
 */
class KotlinExample : AppCompatActivity() {

    // 延迟初始化 ViewBinding
    private lateinit var binding: ActivityKotlinExampleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 使用 ViewBinding
        binding = ActivityKotlinExampleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupViews()
        loadData()
    }
    
    private fun setupViews() {
        // 示例：Kotlin 风格的点击事件
        // binding.button.setOnClickListener {
        //     handleClick()
        // }
    }
    
    private fun loadData() {
        // 使用协程进行后台任务
        CoroutineScope(Dispatchers.Main).launch {
            val result = withContext(Dispatchers.IO) {
                // 后台任务
                "Data loaded"
            }
            // 更新 UI
            // binding.textView.text = result
        }
    }
    
    // Kotlin 扩展函数示例
    private fun String.addHello(): String {
        return "Hello, $this"
    }
    
    // Kotlin 数据类示例
    data class User(val name: String, val age: Int)
    
    override fun onDestroy() {
        super.onDestroy()
        // Fragment 中需要设置为 null，Activity 中是可选的
    }
}
