package com.jordantymburski.driftoff;

import androidx.test.platform.app.InstrumentationRegistry;

import com.jordantymburski.driftoff.data.PreferenceStorage;
import com.jordantymburski.driftoff.domain.model.AlarmInfo;

import org.junit.Test;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class DataPreferenceStorageTest {
    /**
     * Creates a storage instance
     * @param deleteAll TRUE to delete all existing data. FALSE to leave as is
     * @return the preference storage
     */
    private PreferenceStorage createStorage(boolean deleteAll) {
        PreferenceStorage storage = new PreferenceStorage(
                InstrumentationRegistry.getInstrumentation().getTargetContext());
        if (deleteAll) {
            storage.deleteAll();
        }
        return storage;
    }

    @Test
    public void loadDefault() {
        PreferenceStorage storage = createStorage(true);
        AlarmInfo info = storage.load();
        assertEquals(0, info.alarm);
        assertEquals(21, info.timeHour);
        assertEquals(30, info.timeMinute);
    }

    @Test
    public void setValue() {
        final PreferenceStorage storage = createStorage(true);
        final AlarmInfo info = new AlarmInfo(new Date().getTime(), 14, 22);
        storage.save(info);
        assertEquals(info, storage.load());

        final AlarmInfo info2 = new AlarmInfo(41523456L, 8, 14);
        storage.save(info2);
        assertEquals(info2, storage.load());
    }

    @Test
    public void loadOldValue() {
        final PreferenceStorage storage = createStorage(true);
        final AlarmInfo info = new AlarmInfo(
                new Date().getTime() - TimeUnit.DAYS.toMillis(4),
                14, 22);
        storage.save(info);

        final PreferenceStorage storageNew = createStorage(false);
        assertEquals(info, storageNew.load());
    }
}
