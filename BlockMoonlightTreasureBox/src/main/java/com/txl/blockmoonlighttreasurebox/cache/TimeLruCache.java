package com.txl.blockmoonlighttreasurebox.cache;

import android.os.SystemClock;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) 2021, 唐小陆 All rights reserved.
 * author：txl
 * date：2021/10/23
 * description：依据时间作为偏差，存储指定时间范围内的节点
 */
public class TimeLruCache<V> implements Serializable {
    /**
     * 默认30s  单位是ms
     * */
    private long offsetTime = 30 * 1000;
    private long lastPutTime = 0;
    private V lastValue;
    private static final long serialVersionUID = 1L;
    //按插入顺序保存 依次移除时间最早的
    private final LinkedHashMap<Long,V> linkedHashMap = new TimeLinkedHashMap(0, 0.75f, false);

    public TimeLruCache() {
    }



    /**
     * 当最后一个和第一个时间偏差超过该值的时候，会将LinkedHashMap 中链表表头的元素移除
     * */
    public TimeLruCache(long offsetTime) {
        this.offsetTime = offsetTime;
    }

    public void put(V value){
        put( SystemClock.elapsedRealtime(),value );
    }

    public void put(long baseTime, V value){
        linkedHashMap.put( baseTime,value );
        lastPutTime = baseTime;
        lastValue = value;
    }

    /**
     * 存储下最后一个元素，方便快速获取
     * */
    public V getLastValue() {
        return lastValue;
    }

    public List<V> getAll(){
        List<V> list = new ArrayList<>();
        for (Map.Entry<Long, V> entry : linkedHashMap.entrySet()) {//
            list.add(entry.getValue());
        }
        return list;
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
                Entry<Long, V> entry = iterator.next();
                if(entry == eldest){
                    Entry<Long, V> temp = null;
                    if(iterator.hasNext()){
                        temp = iterator.next();
                    }
                    //在去除第一个的时候，剩下的数据也大于指定时间
                    return temp != null && (lastPutTime - temp.getKey() > offsetTime);
                }
            }
            return false;
        }
    }
}
