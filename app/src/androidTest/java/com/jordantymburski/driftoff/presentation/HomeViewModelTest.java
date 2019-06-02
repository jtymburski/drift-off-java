package com.jordantymburski.driftoff.presentation;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.jordantymburski.driftoff.domain.model.AlarmInfo;
import com.jordantymburski.driftoff.domain.usecase.GetInfo;
import com.jordantymburski.driftoff.domain.usecase.SetInfo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.Random;

import static org.junit.Assert.*;

public class HomeViewModelTest {
    /**
     * Starting alarm info
     */
    private AlarmInfo mAlarmInfo;

    /**
     * Get info use case mock class
     */
    @Mock
    private GetInfo mGetInfo;

    /**
     * The fake observable
     */
    private MutableLiveData<AlarmInfo> mObservable;

    /**
     * Random number generator instance
     */
    private final Random mRandom = new Random();

    /**
     * Set info use case mock class
     */
    @Mock
    private SetInfo mSetInfo;

    /**
     * The presentation view model
     */
    private HomeViewModel mViewModel;

    @Rule
    public TestRule syncRule = new InstantTaskExecutorRule();

    @Before
    public void setup() {
        // Initialize mocks
        MockitoAnnotations.initMocks(this);

        // Make the get info mock return the proper observable
        mObservable = new MutableLiveData<>();
        mAlarmInfo = new AlarmInfo(
                new Date().getTime(), mRandom.nextInt(24), mRandom.nextInt(60));
        mObservable.setValue(mAlarmInfo);
        Mockito.when(mGetInfo.observable()).thenReturn(mObservable);

        // Create the model
        mViewModel = new HomeViewModelFactory(mGetInfo, mSetInfo).create(HomeViewModel.class);

        // Clear out any invocations
        Mockito.clearInvocations(mGetInfo, mSetInfo);
    }

    @Test
    public void getInfoObservable() {
        assertNotNull(mViewModel.getInfoObservable());
        assertEquals(mObservable, mViewModel.getInfoObservable());
        assertEquals(mAlarmInfo, mViewModel.getInfoObservable().getValue());
    }

    @Test
    public void resetAlarm() {
        // Execute
        mViewModel.resetAlarm();

        // Check
        Mockito.verifyZeroInteractions(mGetInfo);
        Mockito.verify(mSetInfo).resetAlarm();
        Mockito.verifyNoMoreInteractions(mSetInfo);
    }

    @Test
    public void setAlarm() {
        // Execute
        mViewModel.setAlarm();

        // Check
        Mockito.verifyZeroInteractions(mGetInfo);
        Mockito.verify(mSetInfo).setAlarm();
        Mockito.verifyNoMoreInteractions(mSetInfo);
    }

    @Test
    public void setTime() {
        // Execute
        final int hour = mRandom.nextInt(24);
        final int minute = mRandom.nextInt(60);
        mViewModel.setTime(hour, minute);

        // Check
        Mockito.verifyZeroInteractions(mGetInfo);
        Mockito.verify(mSetInfo).setTime(hour, minute);
        Mockito.verifyNoMoreInteractions(mSetInfo);
    }
}
