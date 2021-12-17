package com.Adjetter.analytics.controller;

import com.Adjetter.analytics.model.CallData;
import com.Adjetter.analytics.service.Services;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;


@RestController
@RequestMapping(path = "/api/")
public class CallDataController {
    @Autowired
    private Services service;

    @GetMapping("/getDate/{date}")
    public String getCallAnalyticsFromDate(@PathVariable @JsonFormat(pattern = "yyyy-MM-dd") Date date){
        return service.getHighestCallVolumeAndLongest(date);
    }

    @GetMapping("/getAll/")
    public List<CallData> getAllLogs(){
        return service.getCallLogs();
    }

    @PostMapping()
    public CallData addCall(@RequestBody CallData callData){
        return service.saveCall(callData);
    }

    @PostMapping("/saveCalls/")
    public List<CallData> addMultipleCalls(@RequestBody List<CallData> callsData){
        return service.saveAllCalls(callsData);
    }

}
