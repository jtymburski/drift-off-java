package com.jordantymburski.driftoff.domain;

import com.jordantymburski.driftoff.App;
import com.jordantymburski.driftoff.common.ContextProvider;
import com.jordantymburski.driftoff.di.testing.DaggerMockAppComponent;
import com.jordantymburski.driftoff.di.testing.MockDomainModule;
import com.jordantymburski.driftoff.domain.usecase.GetInfo;
import com.jordantymburski.driftoff.domain.usecase.RescheduleAlarm;
import com.jordantymburski.driftoff.domain.usecase.SetInfo;
import com.jordantymburski.driftoff.domain.usecase.StopAudio;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class MockDomain {
    /**
     * Get info domain use case
     */
    @Mock
    private GetInfo mGetInfo;

    /**
     * Reschedule alarm domain use case
     */
    @Mock
    private RescheduleAlarm mRescheduleAlarm;

    /**
     * Set info domain use case
     */
    @Mock
    private SetInfo mSetInfo;

    /**
     * Stop audio domain use case
     */
    @Mock
    private StopAudio mStopAudio;

    /**
     * Default constructor. Sets up the mocks and swaps the app main component with the new mocked
     * component
     */
    public MockDomain() {
        MockitoAnnotations.initMocks(this);

        ((App) ContextProvider.get().getApplicationContext())
                .component(DaggerMockAppComponent.builder()
                        .mockDomainModule(new MockDomainModule(
                                mGetInfo, mRescheduleAlarm, mSetInfo, mStopAudio))
                        .build());
    }

    /* ----------------------------------------------
     * INTERNAL FUNCTIONS
     * ---------------------------------------------- */

    /**
     * Fetches a primitive array of all use cases for usage in (Object... mocks) function calls
     * @return the array
     */
    private Object[] getAll() {
        return new Object[] {
                mGetInfo,
                mRescheduleAlarm,
                mSetInfo,
                mStopAudio
        };
    }

    /* ----------------------------------------------
     * PUBLIC FUNCTIONS
     * ---------------------------------------------- */

    /**
     * Clears all existing history of function calls for all domain use cases
     */
    public void clearInvocations() {
        Mockito.clearInvocations(getAll());
    }

    /**
     * @return the get info use case
     */
    public GetInfo getInfo() {
        return mGetInfo;
    }

    /**
     * @return the reschedule alarm use case
     */
    public RescheduleAlarm rescheduleAlarm() {
        return mRescheduleAlarm;
    }

    /**
     * @return the set info use case
     */
    public SetInfo setInfo() {
        return mSetInfo;
    }

    /**
     * @return the stop audio use case
     */
    public StopAudio stopAudio() {
        return mStopAudio;
    }

    /**
     * Verifies that there are no more interactions on all domain use cases
     */
    public void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(getAll());
    }

    /**
     * Verifies that there are zero interactions on all domain use cases
     */
    public void verifyZeroInteractions() {
        Mockito.verifyZeroInteractions(getAll());
    }
}
