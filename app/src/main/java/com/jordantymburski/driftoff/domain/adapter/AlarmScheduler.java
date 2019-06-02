package com.jordantymburski.driftoff.domain.adapter;

public interface AlarmScheduler {
    void cancel();
    void schedule(long time);
}
