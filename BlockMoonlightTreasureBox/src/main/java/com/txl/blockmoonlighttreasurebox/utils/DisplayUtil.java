package com.txl.blockmoonlighttreasurebox.utils;

import android.content.Context;

/**
 * Copyright (c) 2018, 唐小陆 All rights reserved.
 * author：txl
 * date：2018/9/6
 * description：屏幕单位换算 dp、sp转换为px的工具类
 */
public class DisplayUtil {

    /**
     * 将px值转换为dip或dp
     * */
    public static int px2dip(Context context, float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue/scale+0.5f);
    }

    /**
     * 将dip或dp转换成px
     * */
    public static int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale+0.5f);
    }

    /**
     * 将px转换成sp
     * */
    public static int px2sp(Context context, float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue/scale+0.5f);
    }

    /**
     * sp转px
     * */
    public static int sp2px(Context context, float spValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (spValue * scale+0.5f);
    }
}
