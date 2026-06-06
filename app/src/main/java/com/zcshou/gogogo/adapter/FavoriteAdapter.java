package com.zcshou.gogogo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zcshou.gogogo.databinding.ItemFavoriteBinding;
import com.zcshou.gogogo.data.entity.FavoriteLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * 收藏夹列表 Adapter
 */
public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {
    private List<FavoriteLocation> favorites = new ArrayList<>();
    private OnFavoriteClickListener listener;

    public interface OnFavoriteClickListener {
        void onFavoriteClick(FavoriteLocation favorite);
        void onDeleteClick(FavoriteLocation favorite);
    }

    public void setOnFavoriteClickListener(OnFavoriteClickListener listener) {
        this.listener = listener;
    }

    public void setFavorites(List<FavoriteLocation> favorites) {
        this.favorites = favorites != null ? favorites : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFavoriteBinding binding = ItemFavoriteBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false);
        return new FavoriteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        FavoriteLocation favorite = favorites.get(position);
        holder.bind(favorite);
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder {
        private final ItemFavoriteBinding binding;

        public FavoriteViewHolder(ItemFavoriteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(FavoriteLocation favorite) {
            binding.tvFavoriteName.setText(favorite.name);
            binding.tvFavoriteAddress.setText(favorite.address);

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFavoriteClick(favorite);
                }
            });

            binding.btnDeleteFavorite.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(favorite);
                }
            });
        }
    }
}
