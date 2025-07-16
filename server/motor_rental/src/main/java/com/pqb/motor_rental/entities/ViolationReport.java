package com.pqb.motor_rental.entities;

import com.pqb.motor_rental.enums.ReportStatus;
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
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;

    @ManyToOne
    @JoinColumn(name = "rental_id")
    private Rental rental;

    private String evidenceUrl;
    private String description;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    private LocalDateTime resolvedAt;
    private String resolutionNote;
}
