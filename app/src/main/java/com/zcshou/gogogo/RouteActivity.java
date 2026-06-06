package com.zcshou.gogogo;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.zcshou.gogogo.data.entity.Route;
import com.zcshou.gogogo.databinding.ActivityRouteBinding;
import com.zcshou.gogogo.ui.adapter.RouteAdapter;
import com.zcshou.gogogo.ui.base.BaseActivity;
import com.zcshou.gogogo.ui.viewmodel.RouteViewModel;
import com.zcshou.gogogo.utils.RouteIOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RouteActivity extends BaseActivity {

    private static final int PICK_FILE_REQUEST = 1001;
    
    private ActivityRouteBinding binding;
    private RouteViewModel viewModel;
    private RouteAdapter adapter;
    
    private ActivityResultLauncher<Intent> pickFileLauncher;

    @Override
    protected void initViewModel() {
        viewModel = new ViewModelProvider(this).get(RouteViewModel.class);
    }

    @Override
    protected void initViews() {
        binding = ActivityRouteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 设置Toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 初始化RecyclerView
        initRecyclerView();

        // 初始化SwipeRefreshLayout
        binding.swipeRefresh.setEnabled(false);

        // FAB点击事件 - 显示导入选项
        binding.fabImport.setOnClickListener(v -> showImportOptionsDialog());
        
        // 初始化文件选择器
        initFilePicker();
    }

    @Override
    protected void initData() {
        // 数据加载由ViewModel处理
    }

    @Override
    protected void initObservers() {
        viewModel.getAllRoutes().observe(this, routes -> {
            if (routes == null || routes.isEmpty()) {
                binding.rvRoutes.setVisibility(android.view.View.GONE);
                binding.llEmptyState.setVisibility(android.view.View.VISIBLE);
            } else {
                binding.rvRoutes.setVisibility(android.view.View.VISIBLE);
                binding.llEmptyState.setVisibility(android.view.View.GONE);
                adapter.submitList(routes);
            }
        });

        viewModel.getSuccessMessage().observe(this, message -> {
            if (!TextUtils.isEmpty(message)) {
                showToast(message);
                viewModel.clearSuccess();
            }
        });

        viewModel.getErrorMessage().observe(this, message -> {
            if (!TextUtils.isEmpty(message)) {
                showToast(message);
                viewModel.clearError();
            }
        });
    }

    private void initRecyclerView() {
        adapter = new RouteAdapter();
        adapter.setOnItemClickListener(this::showActionDialog);
        binding.rvRoutes.setLayoutManager(new LinearLayoutManager(this));
        binding.rvRoutes.setAdapter(adapter);
    }
    
    private void initFilePicker() {
        pickFileLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri fileUri = result.getData().getData();
                if (fileUri != null) {
                    importRouteFromUri(fileUri);
                }
            }
        });
    }

    /**
     * 显示导入选项对话框
     */
    private void showImportOptionsDialog() {
        String[] options = {
            getString(R.string.import_from_clipboard),
            getString(R.string.import_from_file)
        };

        new MaterialAlertDialogBuilder(this)
            .setTitle(R.string.import_options)
            .setItems(options, (dialog, which) -> {
                switch (which) {
                    case 0:
                        showImportFromClipboardDialog();
                        break;
                    case 1:
                        openFilePicker();
                        break;
                }
            })
            .show();
    }

    /**
     * 从剪贴板导入对话框
     */
    private void showImportFromClipboardDialog() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard == null || !clipboard.hasPrimaryClip() || clipboard.getPrimaryClip().getItemCount() == 0) {
            showToast(getString(R.string.clipboard_empty));
            return;
        }

        CharSequence text = clipboard.getPrimaryClip().getItemAt(0).getText();
        if (TextUtils.isEmpty(text)) {
            showToast(getString(R.string.clipboard_invalid));
            return;
        }
        String jsonContent = text.toString();

        final EditText input = new EditText(this);
        input.setHint(R.string.enter_route_name);
        input.setPadding(
            getResources().getDimensionPixelSize(R.dimen.spacing_md),
            getResources().getDimensionPixelSize(R.dimen.spacing_md),
            getResources().getDimensionPixelSize(R.dimen.spacing_md),
            getResources().getDimensionPixelSize(R.dimen.spacing_md)
        );

        new MaterialAlertDialogBuilder(this)
            .setTitle(R.string.import_route_title)
            .setMessage(R.string.import_route_message)
            .setView(input)
            .setPositiveButton(R.string.save, (dialog, which) -> {
                String name = input.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    name = "未命名路线_" + System.currentTimeMillis();
                }
                viewModel.saveRoute(name, jsonContent);
            })
            .setNegativeButton(R.string.cancel, null)
            .show();
    }

    /**
     * 打开文件选择器
     */
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        pickFileLauncher.launch(Intent.createChooser(intent, getString(R.string.file_picker_title)));
    }

    /**
     * 从Uri导入路线
     */
    private void importRouteFromUri(Uri fileUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            if (inputStream == null) {
                showToast("无法打开文件");
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

            String jsonContent = sb.toString();
            if (viewModel.importRouteFromJson(jsonContent)) {
                showToast(getString(R.string.route_imported_from_file));
            }
        } catch (Exception e) {
            showToast("导入失败: " + e.getMessage());
        }
    }

    /**
     * 显示路线操作对话框
     */
    private void showActionDialog(Route route) {
        String[] options = {
            getString(R.string.export_to_clipboard),
            getString(R.string.export_to_file),
            getString(R.string.run_route),
            getString(R.string.delete_route_option)
        };

        new MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.action) + ": " + route.routeName)
            .setItems(options, (dialog, which) -> {
                switch (which) {
                    case 0:
                        exportToClipboard(route);
                        break;
                    case 1:
                        exportToFile(route);
                        break;
                    case 2:
                        runRoute(route);
                        break;
                    case 3:
                        confirmDelete(route);
                        break;
                }
            })
            .show();
    }

    /**
     * 导出路线到剪贴板
     */
    private void exportToClipboard(Route route) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            String jsonString = viewModel.routeToJson(route);
            if (jsonString != null) {
                ClipData clip = ClipData.newPlainText("RouteData", jsonString);
                clipboard.setPrimaryClip(clip);
                showToast(getString(R.string.route_exported));
            }
        }
    }

    /**
     * 导出路线到文件
     */
    private void exportToFile(Route route) {
        if (viewModel.exportRouteToFile(route)) {
            showToast(getString(R.string.route_exported_to_file));
        }
    }

    /**
     * 运行路线
     */
    private void runRoute(Route route) {
        // 启动 RouteRunActivity 并传递路线数据
        Intent intent = new Intent(this, RouteRunActivity.class);
        // 这里我们可以传递路线ID，让 RouteRunActivity 加载
        intent.putExtra("routeId", route.id);
        startActivity(intent);
    }

    /**
     * 确认删除对话框
     */
    private void confirmDelete(Route route) {
        new MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete_route)
            .setMessage(String.format(getString(R.string.confirm_delete_route), route.routeName))
            .setPositiveButton(R.string.delete, (dialog, which) -> {
                viewModel.deleteRoute(route);
            })
            .setNegativeButton(R.string.cancel, null)
            .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
