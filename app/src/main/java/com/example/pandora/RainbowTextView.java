package com.example.pandora;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;

public class RainbowTextView extends AppCompatTextView {
    public RainbowTextView(Context context, AttributeSet attrs) { super(context, attrs); }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0) {
            getPaint().setShader(new LinearGradient(0, 0, w, 0,
                    new int[]{Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE, Color.MAGENTA},
                    null, Shader.TileMode.CLAMP));
            invalidate();
        }
    }
}