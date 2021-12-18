package com.Adjetter.analytics.repository;

import com.Adjetter.analytics.model.CallData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.util.List;


public interface CallDataRepository extends JpaRepository<CallData, Long> {
    List<CallData> findByNumber(Long number);

    @Query("SELECT SUM(c.duration) FROM CallData c WHERE c.startTime BETWEEN CONCAT(:date, ' 00:00:00') AND CONCAT(:date, ' 23:59:59')")
    Long findByDate(Date date);

    @Query(value = "SELECT WEEK(:currDate)", nativeQuery = true)
    Integer WeekNoFromDate(Date currDate);

    @Query("SELECT MAX(c.duration) FROM CallData c WHERE c.startTime BETWEEN CONCAT(:date, ' 00:00:00') AND CONCAT(:date, ' 23:59:59')")
    Long getMaxDuration(Date date);

    @Query("SELECT HOUR(c.startTime), COUNT(*), SUM(c.duration) FROM CallData c WHERE c.startTime BETWEEN CONCAT(:date, ' 00:00:00') AND CONCAT(:date, ' 23:59:59') GROUP BY HOUR(c.startTime)")
    List<Object> getCallVolumeFromHour(Date date);
}
