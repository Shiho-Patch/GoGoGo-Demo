package com.zcshou.gogogo.ui.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;
import com.zcshou.gogogo.R;
import com.zcshou.gogogo.databinding.DialogMapOptionsBinding;
import com.zcshou.gogogo.map.MapEngine;

public class MapOptionsDialogFragment extends BottomSheetDialogFragment {

    private static final String ARG_CURRENT_PROVIDER = "current_provider";
    private static final String ARG_CURRENT_TYPE = "current_type";

    public interface MapOptionsListener {
        void onProviderChanged(MapEngine.MapProvider provider);
        void onMapTypeChanged(MapEngine.MapType type);
    }

    private DialogMapOptionsBinding binding;
    private MapOptionsListener listener;
    private MapEngine.MapProvider currentProvider = MapEngine.MapProvider.BAIDU;
    private MapEngine.MapType currentType = MapEngine.MapType.NORMAL;

    public static MapOptionsDialogFragment newInstance(
            MapEngine.MapProvider provider, 
            MapEngine.MapType type,
            MapOptionsListener listener
    ) {
        MapOptionsDialogFragment fragment = new MapOptionsDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CURRENT_PROVIDER, provider.name());
        args.putString(ARG_CURRENT_TYPE, type.name());
        fragment.setListener(listener);
        return fragment;
    }

    public void setListener(MapOptionsListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, 
                          @Nullable ViewGroup container, 
                          @Nullable Bundle savedInstanceState) {
        binding = DialogMapOptionsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            String providerStr = getArguments().getString(ARG_CURRENT_PROVIDER);
            String typeStr = getArguments().getString(ARG_CURRENT_TYPE);
            if (providerStr != null) {
                currentProvider = MapEngine.MapProvider.valueOf(providerStr);
            }
            if (typeStr != null) {
                currentType = MapEngine.MapType.valueOf(typeStr);
            }
        }

        setupUI();
        setupListeners();
    }

    private void setupUI() {
        updateProviderSelection(currentProvider);
        updateTypeSelection(currentType);
    }

    private void updateProviderSelection(MapEngine.MapProvider provider) {
        int primaryColor = requireContext().getColor(R.color.md_theme_primary);
        int variantColor = requireContext().getColor(R.color.md_theme_onSurfaceVariant);

        // Reset both
        binding.cardBaidu.setStrokeWidth(0);
        binding.cardAmap.setStrokeWidth(0);

        if (provider == MapEngine.MapProvider.BAIDU) {
            binding.cardBaidu.setStrokeWidth(2);
            binding.cardBaidu.setStrokeColor(primaryColor);
            setCardTextColor(binding.cardBaidu, true);
        } else {
            binding.cardAmap.setStrokeWidth(2);
            binding.cardAmap.setStrokeColor(primaryColor);
            setCardTextColor(binding.cardAmap, true);
        }
    }

    private void updateTypeSelection(MapEngine.MapType type) {
        int primaryColor = requireContext().getColor(R.color.md_theme_primary);

        // Reset all
        binding.cardMapNormal.setStrokeWidth(0);
        binding.cardMapSatellite.setStrokeWidth(0);
        binding.cardMapTraffic.setStrokeWidth(0);

        switch (type) {
            case NORMAL:
                binding.cardMapNormal.setStrokeWidth(2);
                binding.cardMapNormal.setStrokeColor(primaryColor);
                setCardTextColor(binding.cardMapNormal, true);
                break;
            case SATELLITE:
                binding.cardMapSatellite.setStrokeWidth(2);
                binding.cardMapSatellite.setStrokeColor(primaryColor);
                setCardTextColor(binding.cardMapSatellite, true);
                break;
            case TRAFFIC:
                binding.cardMapTraffic.setStrokeWidth(2);
                binding.cardMapTraffic.setStrokeColor(primaryColor);
                setCardTextColor(binding.cardMapTraffic, true);
                break;
        }
    }

    private void setCardTextColor(MaterialCardView card, boolean selected) {
        // This is a simplified version - in real implementation, you'd find the text views
        // and change their colors based on selection state
    }

    private void setupListeners() {
        binding.cardBaidu.setOnClickListener(v -> {
            if (currentProvider != MapEngine.MapProvider.BAIDU) {
                currentProvider = MapEngine.MapProvider.BAIDU;
                updateProviderSelection(currentProvider);
                if (listener != null) {
                    listener.onProviderChanged(currentProvider);
                }
            }
        });

        binding.cardAmap.setOnClickListener(v -> {
            if (currentProvider != MapEngine.MapProvider.AMAP) {
                currentProvider = MapEngine.MapProvider.AMAP;
                updateProviderSelection(currentProvider);
                if (listener != null) {
                    listener.onProviderChanged(currentProvider);
                }
            }
        });

        binding.cardMapNormal.setOnClickListener(v -> {
            if (currentType != MapEngine.MapType.NORMAL) {
                currentType = MapEngine.MapType.NORMAL;
                updateTypeSelection(currentType);
                if (listener != null) {
                    listener.onMapTypeChanged(currentType);
                }
            }
        });

        binding.cardMapSatellite.setOnClickListener(v -> {
            if (currentType != MapEngine.MapType.SATELLITE) {
                currentType = MapEngine.MapType.SATELLITE;
                updateTypeSelection(currentType);
                if (listener != null) {
                    listener.onMapTypeChanged(currentType);
                }
            }
        });

        binding.cardMapTraffic.setOnClickListener(v -> {
            if (currentType != MapEngine.MapType.TRAFFIC) {
                currentType = MapEngine.MapType.TRAFFIC;
                updateTypeSelection(currentType);
                if (listener != null) {
                    listener.onMapTypeChanged(currentType);
                }
            }
        });

        binding.btnCloseMapOptions.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}
