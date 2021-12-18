package com.Adjetter.analytics.repository;

import com.Adjetter.analytics.model.LogMax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.util.List;

public interface LogMaxRepository extends JpaRepository<LogMax, Long> {
    LogMax findByDate(Date date);

    LogMax findByYearAndWeekNumber(Integer year, Integer weekNumber);

    @Query(value = "SELECT DAYNAME(:date)", nativeQuery = true)
    String getDayNameFromDate(Date date);

    @Query("SELECT l.dayName, l.totalDuration, l.date FROM LogMax l WHERE l.totalDuration = (SELECT MAX(n.totalDuration) FROM LogMax n WHERE n.year = :year AND n.weekNumber = :weekNumber) AND l.year = :year AND l.weekNumber = :weekNumber")
    List<Object> getMaxVolumeFromWeek(Integer year, Integer weekNumber);

    @Query("SELECT l.dayName, l.LongestCall, l.date FROM LogMax l WHERE l.LongestCall = (SELECT MAX(n.LongestCall) FROM LogMax n WHERE n.year = :year AND n.weekNumber = :weekNumber) AND l.year = :year AND l.weekNumber = :weekNumber")
    List<Object> getLongestCallFromWeek(Integer year, Integer weekNumber);

}
