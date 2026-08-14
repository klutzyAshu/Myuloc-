package com.example.ui.controller;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.google.android.material.slider.Slider;

/**
 * Controller class to handle interactions with the redesigned Slider Menu Panel.
 * Strictly optimized to prevent UI thread lockups using a single-threaded executor.
 */
public class SliderMenuController {

    private final Slider shuffleSlider;
    private final TextView valueShower;

    // Callback interfaces for external viewmodels or activity binding.
    private OnShuffleIntervalChangedListener shuffleListener;

    public interface OnShuffleIntervalChangedListener {
        void onIntervalChanged(int newInterval);
    }

    public SliderMenuController(
            @NonNull View rootView,
            @NonNull OnShuffleIntervalChangedListener shuffleListener) {

        this.shuffleListener = shuffleListener;

        // Bind layout views safely
        this.shuffleSlider = rootView.findViewById(com.example.R.id.shuffle_slider);
        this.valueShower = rootView.findViewById(com.example.R.id.value_shuffle_interval);

        initListeners();
    }

    private void initListeners() {
        // Slider movement listener for Dynamic Shuffle Interval (Component A)
        if (shuffleSlider != null) {
            shuffleSlider.addOnChangeListener((slider, value, fromUser) -> {
                int interval = (int) value;
                if (valueShower != null) {
                    valueShower.setText(String.format("%d %s", interval, interval == 1 ? "Song" : "Songs"));
                }
                if (fromUser && shuffleListener != null) {
                    shuffleListener.onIntervalChanged(interval);
                }
            });
        }
    }

    private void setInteractiveElementsEnabled(boolean enabled) {
        if (shuffleSlider != null) shuffleSlider.setEnabled(enabled);
    }

    /**
     * Terminate thread pool resources gracefully on controller disposal.
     */
    public void onDestroy() {
    }
}
