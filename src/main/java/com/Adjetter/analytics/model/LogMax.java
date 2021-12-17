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
    @GeneratedValue
    @Column(name = "Id")
    private Long id;

    @Column(name = "Date", nullable = false)
    @JsonFormat(pattern="dd-MM-yyyy")
    private Date date;

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

}
