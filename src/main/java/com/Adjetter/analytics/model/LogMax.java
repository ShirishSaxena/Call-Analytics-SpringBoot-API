package com.Adjetter.analytics.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "DurationDays")
public class LogMax {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernnateLogMax")
    @SequenceGenerator(name = "hibernnateLogMax", sequenceName = "hibernnateLogMax", initialValue = 1, allocationSize = 1)
    @Column(name = "Id")
    private Long id;

    @Column(name = "Date")
    //@JsonFormat(pattern = "yyyy-MM-dd")
    private String date;

    @Column(name = "totalDuration")
    private Long totalDuration;

    @Column(name = "LongestCall")
    private Long LongestCall;

    @Column(name = "dayName")
    private String dayName;

    @Column(name = "year")
    private Integer year;

    @Column(name = "weekNumber")
    private Integer weekNumber;

    // l.date, l.totalDuration, l.LongestCall, l.dayName, l.year, l.weekNumber

}
