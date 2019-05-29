package com.jordantymburski.driftoff;

import com.jordantymburski.driftoff.domain.model.AlarmInfo;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class DomainModelAlarmInfoTest {
    @Test
    public void constructorSimple() {
        final long alarmTime = new Date().getTime();
        final int timeHour = 14;
        final int timeMinute = 45;
        final AlarmInfo info = new AlarmInfo(alarmTime, timeHour, timeMinute);
        assertEquals(alarmTime, info.alarm);
        assertEquals(timeHour, info.timeHour);
        assertEquals(timeMinute, info.timeMinute);

        final long alarmTime2 = 0L;
        final int timeHour2 = 0;
        final int timeMinute2 = 0;
        final AlarmInfo info2 = new AlarmInfo(alarmTime2, timeHour2, timeMinute2);
        assertEquals(alarmTime2, info2.alarm);
        assertEquals(timeHour2, info2.timeHour);
        assertEquals(timeMinute2, info2.timeMinute);
    }

    @Test
    public void constructorModifyAlarm() {
        final AlarmInfo baseInfo = new AlarmInfo(new Date().getTime(), 8, 21);

        final long modifiedAlarm = 40000L;
        final AlarmInfo modifiedInfo = new AlarmInfo(baseInfo, modifiedAlarm);
        assertEquals(modifiedAlarm, modifiedInfo.alarm);
        assertEquals(baseInfo.timeHour, modifiedInfo.timeHour);
        assertEquals(baseInfo.timeMinute, modifiedInfo.timeMinute);

        final long modifiedAlarm2 = 0L;
        final AlarmInfo modifiedInfo2 = new AlarmInfo(baseInfo, modifiedAlarm2);
        assertEquals(modifiedAlarm2, modifiedInfo2.alarm);
        assertEquals(baseInfo.timeHour, modifiedInfo.timeHour);
        assertEquals(baseInfo.timeMinute, modifiedInfo.timeMinute);
    }

    @Test
    public void constructorModifyTime() {
        final AlarmInfo baseInfo = new AlarmInfo(new Date().getTime(), 21, 8);

        final int modifiedTimeHour = 4;
        final int modifiedTimeMinute = 59;
        final AlarmInfo modifiedInfo
                = new AlarmInfo(baseInfo, modifiedTimeHour, modifiedTimeMinute);
        assertEquals(baseInfo.alarm, modifiedInfo.alarm);
        assertEquals(modifiedTimeHour, modifiedInfo.timeHour);
        assertEquals(modifiedTimeMinute, modifiedInfo.timeMinute);

        final int modifiedTimeHour2 = baseInfo.timeHour + 2;
        final int modifiedTimeMinute2 = baseInfo.timeMinute - 4;
        final AlarmInfo modifiedInfo2
                = new AlarmInfo(baseInfo, modifiedTimeHour2, modifiedTimeMinute2);
        assertEquals(baseInfo.alarm, modifiedInfo.alarm);
        assertEquals(modifiedTimeHour2, modifiedInfo2.timeHour);
        assertEquals(modifiedTimeMinute2, modifiedInfo2.timeMinute);
    }

    @Test
    public void objectCompare() {
        final long alarmTime = new Date().getTime();
        final int timeHour = 7;
        final int timeMinute = 34;

        final AlarmInfo info1 = new AlarmInfo(alarmTime, timeHour, timeMinute);
        final AlarmInfo info2 = new AlarmInfo(alarmTime, timeHour, timeMinute);
        assertEquals(info1, info2);

        // Mod just the alarm value
        final AlarmInfo info2ModAlarm = new AlarmInfo(alarmTime + 1, timeHour, timeMinute);
        assertNotEquals(info1, info2ModAlarm);

        // Mod just the time hour value
        final AlarmInfo info2ModHour = new AlarmInfo(alarmTime, timeHour + 1, timeMinute);
        assertNotEquals(info1, info2ModHour);

        // Mod just the time minute value
        final AlarmInfo info2ModMinute
                = new AlarmInfo(alarmTime, timeHour, timeMinute + 1);
        assertNotEquals(info1, info2ModMinute);
    }

    @Test
    public void getHoursTillAlarm() {
        // Now
        final AlarmInfo nowInfo = new AlarmInfo(new Date().getTime(), 15, 14);
        assertEquals(0L, nowInfo.getHoursTillAlarm());

        // Just past
        final AlarmInfo pastInfo = new AlarmInfo(nowInfo,
                new Date().getTime() - TimeUnit.MINUTES.toMillis(1));
        assertEquals(0L, pastInfo.getHoursTillAlarm());

        // Almost here
        final AlarmInfo almostInfo = new AlarmInfo(nowInfo,
                new Date().getTime() + TimeUnit.MINUTES.toMillis(1));
        assertEquals(1L, almostInfo.getHoursTillAlarm());

        // Exactly 1 hour
        final AlarmInfo exactlyHourInfo = new AlarmInfo(nowInfo,
                new Date().getTime() + TimeUnit.HOURS.toMillis(1));
        assertEquals(1L, exactlyHourInfo.getHoursTillAlarm());

        // Just over 1 hour
        final AlarmInfo overHourInfo = new AlarmInfo(nowInfo,
                new Date().getTime() + TimeUnit.HOURS.toMillis(1) + 1);
        assertEquals(2L, overHourInfo.getHoursTillAlarm());

        // Much larger value
        final AlarmInfo farAwayInfo = new AlarmInfo(nowInfo,
                new Date().getTime() + TimeUnit.HOURS.toMillis(24));
        assertEquals(24L, farAwayInfo.getHoursTillAlarm());
    }

    @Test
    public void getMinutesTillAlarm() {
        // Now
        final AlarmInfo nowInfo = new AlarmInfo(new Date().getTime(), 15, 14);
        assertEquals(0L, nowInfo.getMinutesTillAlarm());

        // Just past
        final AlarmInfo pastInfo = new AlarmInfo(nowInfo,
                new Date().getTime() - TimeUnit.SECONDS.toMillis(1));
        assertEquals(0L, pastInfo.getMinutesTillAlarm());

        // More than just past
        final AlarmInfo pastInfoMore = new AlarmInfo(nowInfo,
                new Date().getTime() - TimeUnit.MINUTES.toMillis(1));
        assertEquals(0L, pastInfoMore.getMinutesTillAlarm());

        // Almost here
        final AlarmInfo almostInfo = new AlarmInfo(nowInfo,
                new Date().getTime() + TimeUnit.SECONDS.toMillis(1));
        assertEquals(1L, almostInfo.getMinutesTillAlarm());

        // Exactly 1 minute
        final AlarmInfo exactlyMinInfo = new AlarmInfo(nowInfo,
                new Date().getTime() + TimeUnit.MINUTES.toMillis(1));
        assertEquals(1L, exactlyMinInfo.getMinutesTillAlarm());

        // Just over 1 minute
        final AlarmInfo overHourInfo = new AlarmInfo(nowInfo,
                new Date().getTime() + TimeUnit.MINUTES.toMillis(1) + 1);
        assertEquals(2L, overHourInfo.getMinutesTillAlarm());

        // Much larger value
        final AlarmInfo farAwayInfo = new AlarmInfo(nowInfo,
                new Date().getTime() + TimeUnit.MINUTES.toMillis(60));
        assertEquals(60L, farAwayInfo.getMinutesTillAlarm());
    }

    @Test
    public void getTime() {
        // Just a random value
        final int randomHour = 13;
        final int randomMinute = 49;
        final AlarmInfo randomInfo = new AlarmInfo(0L, randomHour, randomMinute);
        final Calendar randomGen = randomInfo.getTime();
        assertEquals(randomHour, randomGen.get(Calendar.HOUR_OF_DAY));
        assertEquals(randomMinute, randomGen.get(Calendar.MINUTE));
        assertEquals(0, randomGen.get(Calendar.SECOND));

        // 1 minute ahead of now
        final Calendar plusOne = Calendar.getInstance();
        plusOne.add(Calendar.MINUTE, 1);
        final AlarmInfo plusOneInfo = new AlarmInfo(
                0L, plusOne.get(Calendar.HOUR_OF_DAY), plusOne.get(Calendar.MINUTE));
        final Calendar plusOneGen = plusOneInfo.getTime();
        assertTrue(plusOneGen.getTimeInMillis()
                > Calendar.getInstance().getTimeInMillis());
        assertEquals(plusOne.get(Calendar.HOUR_OF_DAY), plusOneGen.get(Calendar.HOUR_OF_DAY));
        assertEquals(plusOne.get(Calendar.MINUTE), plusOneGen.get(Calendar.MINUTE));
        assertEquals(0, plusOneGen.get(Calendar.SECOND));

        // 1 minute behind now
        final Calendar minusOne = Calendar.getInstance();
        minusOne.add(Calendar.MINUTE, -1);
        final AlarmInfo minusOneInfo = new AlarmInfo(
                0L, minusOne.get(Calendar.HOUR_OF_DAY), minusOne.get(Calendar.MINUTE));
        final Calendar minusOneGen = minusOneInfo.getTime();
        assertTrue(minusOneGen.getTimeInMillis()
                > Calendar.getInstance().getTimeInMillis());
        assertEquals(minusOne.get(Calendar.HOUR_OF_DAY), minusOneGen.get(Calendar.HOUR_OF_DAY));
        assertEquals(minusOne.get(Calendar.MINUTE), minusOneGen.get(Calendar.MINUTE));
        assertEquals(0, minusOneGen.get(Calendar.SECOND));
    }

    @Test
    public void getTimeInMillis() {
        // Just a random value
        final int randomHour = 13;
        final int randomMinute = 49;
        final AlarmInfo randomInfo = new AlarmInfo(0L, randomHour, randomMinute);
        assertEquals(randomInfo.getTime().getTimeInMillis(), randomInfo.getTimeInMillis());

        // 1 minute ahead of now
        final Calendar plusOne = Calendar.getInstance();
        plusOne.add(Calendar.MINUTE, 1);
        final AlarmInfo plusOneInfo = new AlarmInfo(
                0L, plusOne.get(Calendar.HOUR_OF_DAY), plusOne.get(Calendar.MINUTE));
        assertEquals(plusOneInfo.getTime().getTimeInMillis(), plusOneInfo.getTimeInMillis());

        // 1 minute behind now
        final Calendar minusOne = Calendar.getInstance();
        minusOne.add(Calendar.MINUTE, -1);
        final AlarmInfo minusOneInfo = new AlarmInfo(
                0L, minusOne.get(Calendar.HOUR_OF_DAY), minusOne.get(Calendar.MINUTE));
        assertEquals(minusOneInfo.getTime().getTimeInMillis(), minusOneInfo.getTimeInMillis());
    }

    @Test
    public void isActive() {
        // Reset
        final AlarmInfo resetInfo = new AlarmInfo(0L, 14, 44);
        assertFalse(resetInfo.isActive());

        // 1 minute ahead of now
        final Calendar plusOne = Calendar.getInstance();
        plusOne.add(Calendar.MINUTE, 1);
        final AlarmInfo plusOneInfo = new AlarmInfo(plusOne.getTimeInMillis(),
                plusOne.get(Calendar.HOUR_OF_DAY), plusOne.get(Calendar.MINUTE));
        assertTrue(plusOneInfo.isActive());

        // 1 minute behind now
        final Calendar minusOne = Calendar.getInstance();
        minusOne.add(Calendar.MINUTE, -1);
        final AlarmInfo minusOneInfo = new AlarmInfo(minusOne.getTimeInMillis(),
                minusOne.get(Calendar.HOUR_OF_DAY), minusOne.get(Calendar.MINUTE));
        assertFalse(minusOneInfo.isActive());

        // Random
        final AlarmInfo randomInfo = new AlarmInfo(resetInfo.getTimeInMillis(),
                resetInfo.timeHour, resetInfo.timeMinute);
        assertTrue(randomInfo.isActive());
    }
}
