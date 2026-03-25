package com.github.aarcangeli.serioussamandroid.keyboard;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.PopupWindow;

public class KeyboardHeightProvider extends PopupWindow {
    private final Activity activity;
    private View contentView;
    private KeyboardListener listener;
    private int keyboardHeight;
    private boolean isKeyboardShowing;

    public interface KeyboardListener {
        void onHeightChanged(int height);
    }

    public KeyboardHeightProvider(Activity activity) {
        super(activity);
        this.activity = activity;
        this.keyboardHeight = 0;
        this.isKeyboardShowing = false;
        this.contentView = new View(activity);
        setContentView(contentView);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        setWidth(0);
        setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                updateKeyboardHeight();
            }
        });
    }

    private void updateKeyboardHeight() {
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        
        Point screenSize = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(screenSize);
        
        int visibleHeight = rect.bottom - rect.top;
        int totalHeight = screenSize.y;
        int newKeyboardHeight = totalHeight - visibleHeight;
        
        if (newKeyboardHeight != keyboardHeight) {
            keyboardHeight = newKeyboardHeight;
            if (listener != null) {
                listener.onHeightChanged(keyboardHeight);
            }
        }
    }

    public void addKeyboardListener(KeyboardListener listener) {
        this.listener = listener;
    }

    public void removeKeyboardListener(KeyboardListener listener) {
        this.listener = null;
    }

    public void onResume() {
        if (!isShowing() && activity.getWindow().getDecorView().getParent() != null) {
            showAtLocation(activity.getWindow().getDecorView(), Gravity.NO_GRAVITY, 0, 0);
            isKeyboardShowing = true;
        }
    }

    public void onPause() {
        isKeyboardShowing = false;
        dismiss();
    }

    public void hideKeyboard() {
        isKeyboardShowing = false;
        dismiss();
    }
}
