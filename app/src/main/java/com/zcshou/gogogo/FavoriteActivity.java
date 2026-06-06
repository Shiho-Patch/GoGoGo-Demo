package com.zcshou.gogogo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.zcshou.gogogo.adapter.FavoriteAdapter;
import com.zcshou.gogogo.databinding.ActivityFavoriteBinding;
import com.zcshou.gogogo.data.entity.FavoriteLocation;
import com.zcshou.gogogo.ui.viewmodel.FavoriteViewModel;

/**
 * 收藏夹 Activity
 */
public class FavoriteActivity extends AppCompatActivity {
    public static final String EXTRA_FAVORITE_ID = "favorite_id";
    public static final String EXTRA_LONGITUDE = "longitude";
    public static final String EXTRA_LATITUDE = "latitude";
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_ADDRESS = "address";

    private ActivityFavoriteBinding binding;
    private FavoriteViewModel viewModel;
    private FavoriteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupViewModel();
        setupRecyclerView();
        setupSwipeRefresh();
        setupFab();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbarFavorite);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);
        
        viewModel.getAllFavorites().observe(this, favorites -> {
            adapter.setFavorites(favorites);
            updateEmptyState(favorites != null && favorites.size() > 0);
        });
    }

    private void setupRecyclerView() {
        adapter = new FavoriteAdapter();
        binding.rvFavorites.setLayoutManager(new LinearLayoutManager(this));
        binding.rvFavorites.setAdapter(adapter);

        adapter.setOnFavoriteClickListener(new FavoriteAdapter.OnFavoriteClickListener() {
            @Override
            public void onFavoriteClick(FavoriteLocation favorite) {
                returnResult(favorite);
            }

            @Override
            public void onDeleteClick(FavoriteLocation favorite) {
                showDeleteConfirmationDialog(favorite);
            }
        });
    }

    private void setupSwipeRefresh() {
        binding.swipeRefreshFavorite.setEnabled(false);
    }

    private void setupFab() {
        binding.fabClearFavorites.setOnClickListener(v -> showClearAllConfirmationDialog());
    }

    private void updateEmptyState(boolean hasItems) {
        if (hasItems) {
            binding.emptyStateFavorite.setVisibility(android.view.View.GONE);
            binding.rvFavorites.setVisibility(android.view.View.VISIBLE);
            binding.fabClearFavorites.setVisibility(android.view.View.VISIBLE);
        } else {
            binding.emptyStateFavorite.setVisibility(android.view.View.VISIBLE);
            binding.rvFavorites.setVisibility(android.view.View.GONE);
            binding.fabClearFavorites.setVisibility(android.view.View.GONE);
        }
    }

    private void returnResult(FavoriteLocation favorite) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_FAVORITE_ID, favorite.id);
        resultIntent.putExtra(EXTRA_LONGITUDE, Double.parseDouble(favorite.longitudeCustom));
        resultIntent.putExtra(EXTRA_LATITUDE, Double.parseDouble(favorite.latitudeCustom));
        resultIntent.putExtra(EXTRA_NAME, favorite.name);
        resultIntent.putExtra(EXTRA_ADDRESS, favorite.address);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void showDeleteConfirmationDialog(FavoriteLocation favorite) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete)
                .setMessage(R.string.confirm_delete_favorite)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    viewModel.deleteFavorite(favorite);
                    showSnackbar(getString(R.string.favorite_deleted));
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showClearAllConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.clear_all)
                .setMessage(R.string.confirm_clear_all_favorites)
                .setPositiveButton(R.string.clear_all, (dialog, which) -> {
                    viewModel.clearAllFavorites();
                    showSnackbar(getString(R.string.favorites_cleared));
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showSnackbar(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
