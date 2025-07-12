package com.pqb.motor_rental.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "violation_report")
public class ViolationReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reportId;

    @ManyToOne
    @JoinColumn(name = "reported_user_id")
    private User reportedUser;

    @ManyToOne
    @JoinColumn(name = "reporter_user_id")
    private User reporterUser;

    @ManyToOne
    @JoinColumn(name = "rental_id")
    private Rental rental;

    private String evidenceUrl;
    private String description;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private ReportStatus status;
}
