package io.agora.sample.rtegame.util;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsAnimationCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;


/**
 * Add translationY to a view when Keyboard shows
 * TODO currently just support the RoomFragment, considering to support common view
 */
public class TranslateInsetsAnimationListener extends WindowInsetsAnimationCompat.Callback implements OnApplyWindowInsetsListener {
    private final int desiredInsetsType = WindowInsetsCompat.Type.ime();
    private final View view;
    private WindowInsetsCompat lastWindowInsets = null;

    private boolean deferredThisTime;

    public TranslateInsetsAnimationListener(@NonNull View view) {
        super(WindowInsetsAnimationCompat.Callback.DISPATCH_MODE_STOP);
        this.view = view;
    }

    @SuppressLint("UnknownNullness")
    @Override
    public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
        // Keep this time
        lastWindowInsets = insets;
        int type = deferredThisTime ? -1 : desiredInsetsType;
        setTranslationY(insets, type);
        // Normal-op
        return insets;
    }

    @Override
    public void onPrepare(@NonNull WindowInsetsAnimationCompat animation) {
        if ((animation.getTypeMask() & desiredInsetsType) != 0)
            deferredThisTime = true;
    }

    @NonNull
    @Override
    public WindowInsetsCompat onProgress(@NonNull WindowInsetsCompat insets, @NonNull List<WindowInsetsAnimationCompat> runningAnimations) {
        if (deferredThisTime && !runningAnimations.isEmpty())
            setTranslationY(insets, desiredInsetsType);
        return insets;
    }

    @Override
    public void onEnd(@NonNull WindowInsetsAnimationCompat animation) {
        if (deferredThisTime && (animation.getTypeMask() & desiredInsetsType) != 0){
            deferredThisTime = false;
            if (lastWindowInsets != null && view != null) {
                ViewCompat.dispatchApplyWindowInsets(view, lastWindowInsets);
                view.post(this::checkFocus);
            }
        }

    }

    private void setTranslationY(WindowInsetsCompat insets,int type){
        if (view != null){
            int desiredY;
            if (type == -1) desiredY = 0;
            else desiredY = -insets.getInsets(type).bottom;
            view.setTranslationY(desiredY);
        }
    }

    private void checkFocus() {
        WindowInsetsCompat insets = ViewCompat.getRootWindowInsets(view);
        if (insets != null) {
            boolean imeVisible = insets.isVisible(desiredInsetsType);
            if (imeVisible) {
                view.requestFocus();
            } else {
                view.clearFocus();
            }
        }
    }
}
