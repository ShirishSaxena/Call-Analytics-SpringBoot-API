package com.Adjetter.analytics.service;


import ch.qos.logback.core.net.SyslogOutputStream;
import com.Adjetter.analytics.model.CallData;
import com.Adjetter.analytics.model.LogMax;
import com.Adjetter.analytics.repository.CallDataRepository;
import com.Adjetter.analytics.repository.LogMaxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;

import java.util.*;



@Service
public class Services {
    @Autowired
    private CallDataRepository repository;

    @Autowired
    private LogMaxRepository logMaxRepository;

    /******     Get functions       ******/

    public List<CallData> getCallLogs() {
        return repository.findAll();
    }

    public CallData getCallById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<CallData> getLogsByNumber(Long Number) {
        return repository.findByNumber(Number);
    }


    public LogMax getRecordByDate(Date date) {
        return logMaxRepository.findByDate(String.valueOf(date));
    }

    // Gets a string containing call Analytics for a given date as well as that date week. (as given in assignment)
    @Cacheable(value = "getDate")
    public String getHighestCallVolumeAndLongest(Date date) {
        StringBuilder final_result = new StringBuilder();

        List<Object> obj = repository.getCallVolumeFromHour(date);

        final_result.append("Call Analytics on : " + date + "\n\n");
        if (obj == null || obj.size() == 0)
            final_result.append("Error(day) : Unable to find calls at (" + date + ")\n");
        else
            final_result.append(getAnalyticsByDay(obj));

        // get day of the week, when calls are highest and volume are longest.
        Integer year = getYearFromDate(date), weekNo = getWeekNoFromDate(date);

        List<LogMax> highestCallDay = logMaxRepository.getMaxVolumeFromWeek(year, weekNo);
        List<LogMax> LongestCallDay = logMaxRepository.getLongestCallFromWeek(year, weekNo);

        //System.out.println("\n\n" + date + " | " + year + " | " + weekNo + " --> " + highestCallDay.size() + " | " + LongestCallDay.size() + " ");
        //if any of them is null, assume both are null as they are in the same table.
        if (highestCallDay.size() > 0 || LongestCallDay.size() > 0)
            final_result.append(getAnalyticsByWeek(highestCallDay, LongestCallDay));
        else
            final_result.append("\n\nError(week) : Unable to find volume and longest from week");

        return final_result.toString();
    }


    /***************************************/
    /******     POST functions       ******/
    @CacheEvict(value = "getDate", allEntries=true)
    public CallData saveCall(CallData callData) {
        if (callData.getDuration() == null)
            callData.setDuration(setDurationFromTimeStamp(callData.getStartTime(), callData.getEndTime()));

        CallData saveData = repository.save(callData);
        // check if table exists for date in LogMax
        Date date = getDateFromTimeStamp(saveData.getStartTime());
        LogMax logMax = getRecordByDate(date);
        if (logMax == null)
            saveLogMax(date);
        else {
            updateLogMax(logMax, saveData.getDuration());
        }
        return saveData;
    }

    @CacheEvict(value = "getDate", allEntries=true)
    public Map<String, ?> saveAllCalls(List<CallData> callsData) {
        long start = System.currentTimeMillis();
        for (CallData callData : callsData) {
            if (callData.getDuration() == null)
                callData.setDuration(setDurationFromTimeStamp(callData.getStartTime(), callData.getEndTime()));
        }

        List<CallData> result = repository.saveAll(callsData); // one transaction call which improves performance kinda...?

        int updated = 0, created = 0;
        // get all dates currently on LogMax table
        HashSet<Date> dateHashSet = new HashSet<>();
        for (CallData saveData : result) {
            Date date = getDateFromTimeStamp(saveData.getStartTime());

            if (!dateHashSet.contains(date)) {
                LogMax logMax = getRecordByDate(date);
                if (logMax == null) {
                    created++;
                    saveLogMax(date);
                }
                else {
                    updated++;
                    updateLogsMax(logMax, date);
                }
                dateHashSet.add(date);
            }
        }
        long end = System.currentTimeMillis();


        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        if (result.size() == 0) {
            map.put("status", "failed");
            map.put("error", "List size is 0");
        } else {
            Float timeTook = (Float.valueOf(end - start) / 1000);
            Float reqPerSecond = result.size() / timeTook;
            map.put("status", "success");
            map.put("totalRecords", result.size());
            map.put("requestPerSecond", reqPerSecond);
            map.put("totalTime", timeTook);
        }

        System.out.println("\nAdded(CallLog) -> " + result.size() + " records | Added(LogMax) -> " + created + " | Updated(LogMax) -> " + updated);

        return map;
    }

    public LogMax saveLogMax(Date date) {
        LogMax logMax = new LogMax();

        logMax.setDayName(logMaxRepository.getDayNameFromDate(date));
        logMax.setLongestCall(getMaxCallByDateBetween(date));
        logMax.setYear(getYearFromDate(date));
        logMax.setDate(String.valueOf(date));
        logMax.setTotalDuration(getDurationByDateBetween(date));
        logMax.setWeekNumber(getWeekNoFromDate(date));
        return logMaxRepository.save(logMax);
    }


    /***************************************/
    /******      PUT functions        ******/
    public LogMax updateLogMax(LogMax logMax, Long currDur) {
        Long max = Math.max(logMax.getLongestCall(), currDur);
        logMax.setLongestCall(max);
        logMax.setTotalDuration(logMax.getTotalDuration() + currDur);
        return logMaxRepository.save(logMax);
    }

    public LogMax updateLogsMax(LogMax logMax, Date date) {
        logMax.setLongestCall(getMaxCallByDateBetween(date));
        logMax.setTotalDuration(getDurationByDateBetween(date));
        return logMaxRepository.save(logMax);
    }

    public CallData updateLog(CallData callData) {
        CallData existingRecord = repository.findById(callData.getId()).orElse(null);
        existingRecord.setStartTime(callData.getStartTime());
        existingRecord.setEndTime(callData.getEndTime());
        if (callData.getDuration() == null)
            callData.setDuration(setDurationFromTimeStamp(callData.getStartTime(), callData.getEndTime()));
        existingRecord.setDuration(callData.getDuration());

        return repository.save(existingRecord);
    }

    /*********************************************
     *             Helper Functions              *
     ********************************************/

    // gets sum of all duration & longest for a given date
    private Long getDurationByDateBetween(Date date) {
        Long sum = repository.findByDate(date);
        return (sum == null) ? 0L : sum;
    }

    private Long getMaxCallByDateBetween(Date date) {
        Long sum = repository.getMaxDuration(date);
        return (sum == null) ? 0L : sum;
    }

    // Helper function for getHighestCallVolumeAndLongest(Date date)
    private String getAnalyticsByWeek(List<LogMax> highestCallDay, List<LogMax> LongestCallDay) {
        StringBuilder result = new StringBuilder();
        // obj pattern (l.dayName, l.totalDuration, l.date)

        String highestVolName = highestCallDay.get(0).getDayName();
        Long highestVolDuration = highestCallDay.get(0).getTotalDuration();
        String highestVolDate = highestCallDay.get(0).getDate();

        String LongestCallName = LongestCallDay.get(0).getDayName();
        Long LongestCallDuration = LongestCallDay.get(0).getLongestCall();
        String LongestCallDate = LongestCallDay.get(0).getDate();

        result.append("\n\nHighest volume seen on " + highestVolName + "(" + highestVolDate + ") with total " + highestVolDuration + " seconds.\n");
        result.append("Longest call seen on " + LongestCallName + "(" + LongestCallDate + ") with duration " + LongestCallDuration + " seconds.");

        return result.toString();
    }

    private String getAnalyticsByDay(List<Object> obj) {
        StringBuilder result = new StringBuilder();
        Integer startTime = Integer.parseInt(((Object[]) obj.get(0))[0].toString());
        Integer callCount = Integer.parseInt(((Object[]) obj.get(0))[1].toString());
        result.append("Highest volume of calls at ");
        result.append(digitToTime(startTime) + " - " + digitToTime(startTime + 1) + " with " + callCount + " calls.");

        // find Largest volume of calls
        Integer[] maxVol = new Integer[2];
        // save time & sum for object at index 0;
        maxVol[0] = startTime;
        maxVol[1] = Integer.parseInt(((Object[]) obj.get(0))[2].toString());
        for (Object o : obj) {
            Integer currSum = Integer.parseInt(((Object[]) o)[2].toString());
            if (currSum > maxVol[1]) {
                maxVol[0] = Integer.parseInt(((Object[]) o)[0].toString());
                maxVol[1] = currSum;
            }
        }
        result.append("\nLongest calls at ");
        result.append(digitToTime(maxVol[0]) + " - " + digitToTime(maxVol[0] + 1) + " with total duration of " + maxVol[1] + " seconds.");

        return result.toString();
    }


    // Converter functions...
    private Long setDurationFromTimeStamp(Timestamp start, Timestamp end) {
        return ((end.getTime() - start.getTime()) / 1000);
    }

    private Date getDateFromTimeStamp(Timestamp t) {
        // 2021-00-00
        String str = String.valueOf(t).substring(0, 10);
        //Date date = new Date(t.getTime());
        return Date.valueOf(str);
    }

    private Integer getWeekNoFromDate(Date date) {
        return repository.WeekNoFromDate(date);
    }

    private Integer getYearFromDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    private String digitToTime(Integer n) {
        return ("" + ((n <= 9) ? 0 : "") + n + ":00");
    }

}
