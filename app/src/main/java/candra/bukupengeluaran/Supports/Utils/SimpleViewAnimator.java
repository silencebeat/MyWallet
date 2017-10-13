package candra.bukupengeluaran.Supports.Utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

/**
 * Created by Candra Triyadi on 29/05/2017.
 */

public class SimpleViewAnimator extends LinearLayout
{
    private Animation inAnimation;
    private Animation outAnimation;
    Context context;

    public SimpleViewAnimator(Context context)
    {
        super(context);
        this.context = context;
    }

    public SimpleViewAnimator(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
    }

    public SimpleViewAnimator(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public void setAnimationRes(int animIn, int animOut){
        inAnimation = (Animation) AnimationUtils.loadAnimation(context, animIn);
        outAnimation = (Animation) AnimationUtils.loadAnimation(context, animOut);
    }

    public void setInAnimation(Animation inAnimation)
    {
        this.inAnimation = inAnimation;
    }

    public void setOutAnimation(Animation outAnimation)
    {
        this.outAnimation = outAnimation;
    }

    @Override
    public void setVisibility(int visibility)
    {
        if (getVisibility() != visibility)
        {
            if (visibility == VISIBLE)
            {
                if (inAnimation != null) startAnimation(inAnimation);
            }
            else if ((visibility == INVISIBLE) || (visibility == GONE))
            {
                if (outAnimation != null) startAnimation(outAnimation);
            }
        }

        super.setVisibility(visibility);
    }
}
