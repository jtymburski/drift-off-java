package com.jordantymburski.driftoff.di.testing;

import com.jordantymburski.driftoff.domain.usecase.GetInfo;
import com.jordantymburski.driftoff.domain.usecase.RescheduleAlarm;
import com.jordantymburski.driftoff.domain.usecase.SetInfo;
import com.jordantymburski.driftoff.domain.usecase.StopAudio;

import dagger.Module;
import dagger.Provides;

@Module
public class MockDomainModule {
    /**
     * Domain direct access variables
     */
    private final GetInfo getInfo;
    private final RescheduleAlarm rescheduleAlarm;
    private final SetInfo setInfo;
    private final StopAudio stopAudio;

    public MockDomainModule(GetInfo getInfo, RescheduleAlarm rescheduleAlarm, SetInfo setInfo,
                            StopAudio stopAudio) {
        this.getInfo = getInfo;
        this.rescheduleAlarm = rescheduleAlarm;
        this.setInfo = setInfo;
        this.stopAudio = stopAudio;
    }

    @Provides
    GetInfo provideGetInfo() {
        return getInfo;
    }

    @Provides
    RescheduleAlarm provideRescheduleAlarm() {
        return rescheduleAlarm;
    }

    @Provides
    SetInfo provideSetInfo() {
        return setInfo;
    }

    @Provides
    StopAudio provideStopAudio() {
        return stopAudio;
    }
}
