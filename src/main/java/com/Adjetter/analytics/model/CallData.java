package com.Adjetter.analytics.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "callLogger")
public class CallData {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="hibernnateCallData")
    @SequenceGenerator(name = "hibernnateCallData", sequenceName = "hibernnateCallData", initialValue = 1, allocationSize=1)
    @Column(name = "Id")
    private Long id;

    @Column(name = "number", nullable = false)
    private Long number;

    // choosing pattern as dd-MM-YYYY instead of dd/MM/YYYY to make it more readable and doesn't conflict with 'GET' requests
    @Column(name = "startTime", nullable = false)
    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    private Timestamp startTime;

    @Column(name = "endTime", nullable = false)
    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    private Timestamp endTime;

    @Column(name = "duration")
    private Long duration;
}
