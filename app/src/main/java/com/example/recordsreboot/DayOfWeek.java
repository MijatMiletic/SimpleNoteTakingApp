package com.example.recordsreboot;

import java.util.HashMap;

public enum DayOfWeek {
    SUN(1),
    MON(2),
    TUE(3),
    WED(4),
    THU(5),
    FRI(6),
    SAT(7);

    private final int value;
    /**
     * With this map you can get an enum with a integer
     */
    private static HashMap<Integer, DayOfWeek> map = new HashMap<Integer, DayOfWeek>();

    DayOfWeek(int value){
        this.value = value;
    }

    /*
    This code executes first to make a map
     */
    static {
        for(DayOfWeek day : DayOfWeek.values()){
            map.put(day.value, day);
        }
    }

    public static DayOfWeek valueOf(int num) {
        return map.get(num);
    }
}
