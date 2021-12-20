package com.Adjetter.analytics.repository;

import com.Adjetter.analytics.model.LogMax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.util.List;

public interface LogMaxRepository extends JpaRepository<LogMax, Long> {
    LogMax findByDate(String date);

    LogMax findByYearAndWeekNumber(Integer year, Integer weekNumber);

    @Query(value = "SELECT DAYNAME(:date)", nativeQuery = true)
    String getDayNameFromDate(Date date);

    //@Query("SELECT dayName, totalDuration, date FROM LogMax INNER JOIN (Select MAX(totalDuration) as totalDuration from LogMax WHERE year=:year AND weekNumber = :weekNumber) as max USING(totalDuration)")
    @Query("SELECT l FROM LogMax l WHERE l.totalDuration = (SELECT MAX(n.totalDuration) FROM LogMax n WHERE n.year = :year AND n.weekNumber = :weekNumber) AND l.year = :year AND l.weekNumber = :weekNumber")
    List<LogMax> getMaxVolumeFromWeek(Integer year, Integer weekNumber);

    @Query("SELECT l FROM LogMax l WHERE l.year = :year AND l.weekNumber = :weekNumber AND l.LongestCall = (SELECT MAX(n.LongestCall) FROM LogMax n WHERE n.year = :year AND n.weekNumber = :weekNumber)")
    List<LogMax> getLongestCallFromWeek(Integer year, Integer weekNumber);


}
