package com.zcshou.gogogo.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.zcshou.gogogo.R;
import com.zcshou.gogogo.data.entity.Route;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 路线列表 Adapter
 */
public class RouteAdapter extends ListAdapter<Route, RouteAdapter.RouteViewHolder> {

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Route route);
    }

    public RouteAdapter() {
        super(new RouteDiffCallback());
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_route, parent, false);
        return new RouteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        Route route = getItem(position);
        holder.bind(route);
    }

    class RouteViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvRouteName;
        private final TextView tvRouteDate;

        public RouteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRouteName = itemView.findViewById(R.id.tv_route_name);
            tvRouteDate = itemView.findViewById(R.id.tv_route_date);

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
                    onItemClickListener.onItemClick(getItem(position));
                }
            });
        }

        public void bind(Route route) {
            tvRouteName.setText(route.routeName);
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String dateStr = sdf.format(new Date(route.timestamp));
            tvRouteDate.setText(dateStr);
        }
    }

    static class RouteDiffCallback extends DiffUtil.ItemCallback<Route> {
        @Override
        public boolean areItemsTheSame(@NonNull Route oldItem, @NonNull Route newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Route oldItem, @NonNull Route newItem) {
            return oldItem.routeName.equals(newItem.routeName) && 
                   oldItem.pointsJson.equals(newItem.pointsJson) &&
                   oldItem.timestamp == newItem.timestamp;
        }
    }
}
