package com.jordantymburski.driftoff.domain.adapter;

import com.jordantymburski.driftoff.domain.model.AlarmInfo;

public interface Storage {
    AlarmInfo load();
    void save(AlarmInfo info);
}
