package com.volha.yandex.school.musicartists.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;
import android.util.AttributeSet;

/**
 * Created by Volha on 28.07.2016.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class TransitionAnimation extends TransitionSet {
    public TransitionAnimation() {
        init();
    }

    /**
     * This constructor allows us to use this transition in XML
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TransitionAnimation( Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void init() {
        setOrdering(ORDERING_TOGETHER);
        addTransition(new ChangeBounds()).
                addTransition(new ChangeTransform()).
                addTransition(new ChangeImageTransform());
    }
}
