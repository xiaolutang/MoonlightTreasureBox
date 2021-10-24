package com.txl.blockmoonlighttreasurebox.cache;

import android.os.SystemClock;

import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Copyright (c) 2021, 唐小陆 All rights reserved.
 * author：txl
 * date：2021/10/23
 * description：依据时间作为偏差，存储指定时间范围内的节点
 */
public class TimeLruCache<V> {
    /**
     * 默认30s  单位是ms
     * */
    private long offsetTime = 30 * 1000;
    private long lastPutTime = 0;
    private final LinkedHashMap<Long,V> linkedHashMap = new TimeLinkedHashMap(0, 0.75f, true);

    public TimeLruCache() {
    }



    public TimeLruCache(long offsetTime) {
        this.offsetTime = offsetTime;
    }

    public void put(V value){
        put( SystemClock.elapsedRealtime(),value );
    }

    public void put(long baseTime, V value){
        linkedHashMap.put( baseTime,value );
        lastPutTime = baseTime;
    }


    private class TimeLinkedHashMap extends LinkedHashMap<Long,V>{

        public TimeLinkedHashMap(int initialCapacity, float loadFactor, boolean accessOrder) {
            super( initialCapacity, loadFactor, accessOrder );
        }

        @Override
        protected boolean removeEldestEntry(Entry<Long, V> eldest) {
            //这样会不会导致存储的数据不够 offsetTime ？
            Iterator<Entry<Long,V>> iterator = linkedHashMap.entrySet().iterator();
            while (iterator.hasNext()){
                if(iterator.next() == eldest){
                    Entry<Long, V> temp = iterator.next();
                    //在去除第一个的时候，剩下的数据也大于指定时间
                    return temp != null && (lastPutTime - temp.getKey() > offsetTime);
                }
            }
            return false;
        }
    }
}
