package com.zcshou.gogogo.ui.viewbinding;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.zcshou.gogogo.databinding.ActivityViewBindingExampleBinding;

/**
 * ViewBinding 使用示例
 * <p>
 * 这个类展示了如何使用 ViewBinding 替代 findViewById
 * ViewBinding 会自动为每个布局文件生成一个绑定类
 * 例如 activity_main.xml 会生成 ActivityMainBinding
 */
public class ViewBindingExample extends AppCompatActivity {

    // ViewBinding 会自动生成这个类
    private ActivityViewBindingExampleBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 使用 ViewBinding 绑定布局
        binding = ActivityViewBindingExampleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // 直接通过 binding 访问视图，不需要 findViewById
        setupViews();
    }
    
    private void setupViews() {
        // 示例：设置文本
        // binding.textView.setText("Hello ViewBinding!");
        
        // 示例：设置点击事件
        // binding.button.setOnClickListener(v -> {
        //     // 处理点击
        // });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清理 binding 引用，防止内存泄漏（Activity 中可选，但 Fragment 中必须）
        binding = null;
    }
}
