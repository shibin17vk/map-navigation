package com.example.shibin.myapplication.utils;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

/**
 * @author shibin
 * @version 1.0
 * @date 29/05/17
 */

public class AnimationUtility {


    public synchronized static void expandViewPanel(final View expandView) {
        if (expandView.getVisibility() == View.GONE) {

            int ANIMATION_DURATION = 200;
            expandView.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            final int targtetHeight = expandView.getMeasuredHeight();

            expandView.getLayoutParams().height = 0;
            expandView.setVisibility(View.VISIBLE);
            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    expandView.getLayoutParams().height = interpolatedTime == 1
                            ? LinearLayout.LayoutParams.WRAP_CONTENT
                            : (int) (targtetHeight * interpolatedTime);
                    expandView.requestLayout();
                }

                @Override
                public boolean willChangeBounds() {
                    return true;
                }
            };

            a.setDuration(ANIMATION_DURATION);
            expandView.startAnimation(a);

        }
    }

    public synchronized static void collapseViewPanel(final View expandView) {
        if (expandView.getVisibility() == View.VISIBLE) {

            ValueAnimator valueAnimator = ValueAnimator.ofInt(expandView.getHeight(), 0);
            valueAnimator.setInterpolator(new DecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    expandView.getLayoutParams().height = (int) animation.getAnimatedValue();
                    expandView.requestLayout();
                }

            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    expandView.setVisibility(View.GONE);
                }
            });
            valueAnimator.setInterpolator(new DecelerateInterpolator());
            valueAnimator.setDuration(200);
            valueAnimator.start();

        }
    }

}
