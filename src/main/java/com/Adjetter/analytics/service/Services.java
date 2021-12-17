package com.Adjetter.analytics.service;


import com.Adjetter.analytics.model.CallData;
import com.Adjetter.analytics.model.LogMax;
import com.Adjetter.analytics.repository.CallDataRepository;
import com.Adjetter.analytics.repository.LogMaxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
public class Services {
    @Autowired
    private CallDataRepository repository;

    @Autowired
    private LogMaxRepository logMaxRepository;

    /******     Get functions       ******/
    public List<CallData> getCallLogs(){
        return repository.findAll();
    }

    public CallData getCallById(Long id){
        return repository.findById(id).orElse(null);
    }

    public List<CallData> getLogsByNumber(Long Number){
        return repository.findByNumber(Number);
    }

    public LogMax getRecordByDate(Date date){
        return logMaxRepository.findByDate(date);
    }

    // Gets a string containing call Analytics for a given date as well as that date week. (as given in assignment)
    public String getHighestCallVolumeAndLongest(Date date){
        Timestamp start = Timestamp.valueOf(new StringBuilder(String.valueOf(date)).append(" 00:00:00").toString());
        Timestamp end = Timestamp.valueOf(new StringBuilder(String.valueOf(date)).append(" 23:59:59").toString());

        StringBuilder final_result = new StringBuilder();

        List<Object> obj = repository.getCallVolumeFromTime(start, end);

        final_result.append("Call Analytics on : " + date + "\n\n");
        if(obj == null || obj.size() == 0)
            final_result.append("Error(day) : Unable to find calls at (" + date + ")\n");
        else
            final_result.append(getAnalyticsByDay(obj));

        // get day of the week, when calls are highest and volume are longest.
        Integer year = getYearFromDate(date), weekNo = getWeekNoFromDate(date);
        List<Object> highestCallDay = logMaxRepository.getMaxVolumeFromWeek(year, weekNo);
        List<Object> LongestCallDay = logMaxRepository.getLongestCallFromWeek(year, weekNo);

        //if any of them is null, assume both are null as they are in the same table.
        if(highestCallDay.size() > 0 || LongestCallDay.size() > 0)
            final_result.append(getAnalyticsByWeek(highestCallDay, LongestCallDay));
        else
            final_result.append("\n\nError(week) : Unable to find volume and longest from week\n");

        return final_result.toString();
    }


    /***************************************/
    /******     POST functions       ******/
    public CallData saveCall(CallData callData){
        if(callData.getDuration() == null)
            callData.setDuration(setDurationFromTimeStamp(callData.getStartTime(), callData.getEndTime()));

        CallData saveData = repository.save(callData);
        // check if table exists for date in LogMax
        Date date = getDateFromTimeStamp(saveData.getStartTime());
        LogMax logMax = getRecordByDate(date);
        if(logMax == null)
            saveLogMax(date);
        else{
            updateLogMax(logMax, saveData.getDuration());
        }
        return saveData;
    }

    public List<CallData> saveAllCalls(List<CallData> callsData){
        List<CallData> result = new ArrayList();
        for(CallData callData : callsData)
            result.add(saveCall(callData));
        // not using default saveAll from Jpa because submission date is near, and I don't want to complicate things???...
        // plus, it's not a prod assignment...
        return result;
    }

    public LogMax saveLogMax(Date date){
        LogMax logMax= new LogMax();

        logMax.setDayName(logMaxRepository.getDayNameFromDate(date));
        logMax.setLongestCall(getMaxCallByDateBetween(date));
        logMax.setYear(getYearFromDate(date));
        logMax.setDate(date);
        logMax.setTotalDuration(getDurationByDateBetween(date));
        logMax.setWeekNumber(getWeekNoFromDate(date));
        return logMaxRepository.save(logMax);
    }

    /***************************************/
    /******      PUT functions        ******/
    public LogMax updateLogMax(LogMax logMax, Long currDur){
        Long max = Math.max(logMax.getLongestCall(), currDur);
        logMax.setLongestCall(max);
        logMax.setTotalDuration(logMax.getTotalDuration() + currDur);
        return logMaxRepository.save(logMax);
    }

    public CallData updateLog(CallData callData){
        CallData existingRecord = repository.findById(callData.getId()).orElse(null);
        existingRecord.setStartTime(callData.getStartTime());
        existingRecord.setEndTime(callData.getEndTime());
        if(callData.getDuration() == null)
            callData.setDuration(setDurationFromTimeStamp(callData.getStartTime(), callData.getEndTime()));
        existingRecord.setDuration(callData.getDuration());

        return repository.save(existingRecord);
    }

    /*********************************************
     *             Helper Functions              *
     ********************************************/

    // gets sum of all duration & longest for a given date
    private Long getDurationByDateBetween(Date dateStr){
        // StringBuilder append is faster than string+string operation.
        String start = new StringBuilder(String.valueOf(dateStr)).append(" 00:00:00").toString();
        String end = new StringBuilder(String.valueOf(dateStr)).append(" 23:59:59").toString();

        Long sum = repository.findByDate(Timestamp.valueOf(start), Timestamp.valueOf(end));
        return (sum == null) ? 0L : sum;
    }

    private Long getMaxCallByDateBetween(Date dateStr){
        String start = new StringBuilder(String.valueOf(dateStr)).append(" 00:00:00").toString();
        String end = new StringBuilder(String.valueOf(dateStr)).append(" 23:59:59").toString();

        Long sum = repository.getMaxDuration(Timestamp.valueOf(start), Timestamp.valueOf(end));
        return (sum == null) ? 0L : sum;
    }

    // Helper function for getHighestCallVolumeAndLongest(Date date)
    private String getAnalyticsByWeek(List<Object> highestCallDay, List<Object> LongestCallDay){
        StringBuilder result = new StringBuilder();
        // obj pattern (l.dayName, l.totalDuration, l.date)
        String highestVolName = ((Object[])highestCallDay.get(0))[0].toString();
        Integer highestVolDuration = Integer.parseInt(((Object[])highestCallDay.get(0))[1].toString());
        Date highestVolDate = Date.valueOf(((Object[])highestCallDay.get(0))[2].toString());

        String LongestCallName = ((Object[])LongestCallDay.get(0))[0].toString();
        Integer LongestCallDuration = Integer.parseInt(((Object[])LongestCallDay.get(0))[1].toString());
        Date LongestCallDate = Date.valueOf(((Object[])LongestCallDay.get(0))[2].toString());

        result.append("\n\nHighest volume seen on " + highestVolName + "(" + highestVolDate + ") with total " + highestVolDuration + " seconds.\n");
        result.append("Longest call seen on " + LongestCallName + "(" + LongestCallDate + ") with duration " + LongestCallDuration + " seconds.\n");

        return result.toString();
    }

    private String getAnalyticsByDay(List<Object> obj){
        StringBuilder result = new StringBuilder();
        Integer startTime = Integer.parseInt(((Object[])obj.get(0))[0].toString());
        Integer callCount = Integer.parseInt(((Object[])obj.get(0))[1].toString());
        result.append("Highest volume of calls at ");
        result.append(digitToTime(startTime) + " - " + digitToTime(startTime + 1) + " with " + callCount + " calls.");

        // find Largest volume of calls
        Integer[] maxVol = new Integer[2];
        // save time & sum for object at index 0;
        maxVol[0] = startTime;
        maxVol[1] = Integer.parseInt(((Object[])obj.get(0))[2].toString());
        for(Object o : obj){
            Integer currSum = Integer.parseInt(((Object[])o)[2].toString());
            if(currSum > maxVol[1]){
                maxVol[0] = Integer.parseInt(((Object[])o)[0].toString());
                maxVol[1] = currSum;
            }
        }
        result.append("\nLongest calls at ");
        result.append(digitToTime(maxVol[0]) + " - " + digitToTime(maxVol[0] + 1) + " with total duration of " + maxVol[1] + " seconds.");

        return result.toString();
    }


    // Converter functions...
    private Long setDurationFromTimeStamp(Timestamp start, Timestamp end){
        return ((end.getTime() - start.getTime())/1000);
    }

    private Date getDateFromTimeStamp(Timestamp t){
        // 2021-00-00
        String str = String.valueOf(t).substring(0,10);
        //Date date = new Date(t.getTime());
        return Date.valueOf(str);
    }

    private Integer getWeekNoFromDate(Date date){
        return repository.WeekNoFromDate(date);
    }

    private Integer getYearFromDate(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    private String digitToTime(Integer n){
        return ("" + ((n <= 9) ? 0 : "") + n + ":00");
    }

}
