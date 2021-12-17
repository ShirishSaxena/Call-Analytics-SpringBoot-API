package com.Adjetter.analytics.repository;

import com.Adjetter.analytics.model.CallData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;


public interface CallDataRepository extends JpaRepository<CallData, Long> {
    List<CallData> findByNumber(Long number);

    @Query("SELECT SUM(c.duration) FROM CallData c where (c.startTime between :startTime and :endTime)")
    Long findByDate(Timestamp startTime, Timestamp endTime);

    @Query(value = "SELECT WEEK(:currDate)", nativeQuery = true)
    Integer WeekNoFromDate(Date currDate);

    @Query("SELECT MAX(c.duration) FROM CallData c where (c.startTime between :startTime and :endTime)")
    Long getMaxDuration(Timestamp startTime, Timestamp endTime);

    @Query("SELECT HOUR(c.startTime), COUNT(*), SUM(duration) FROM CallData c WHERE (startTime BETWEEN :startTime AND :endTime) GROUP BY HOUR(startTime)")
    List<Object> getCallVolumeFromTime(Timestamp startTime, Timestamp endTime);
}
