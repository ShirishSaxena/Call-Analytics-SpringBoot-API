package com.Adjetter.analytics.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "callLogger")
public class CallData {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernnateCallData")
    @SequenceGenerator(name = "hibernnateCallData", sequenceName = "hibernnateCallData", initialValue = 1, allocationSize = 1)
    @Column(name = "Id")
    private Long id;

    @NotNull
    @Column(name = "number")
    private Long number;

    // choosing pattern as dd-MM-YYYY instead of dd/MM/YYYY to make it more readable and doesn't conflict with 'GET' requests
    @NotNull
    @Column(name = "startTime")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Timestamp startTime;

    @NotNull
    @Column(name = "endTime")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Timestamp endTime;

    @Column(name = "duration")
    private Long duration;
}
