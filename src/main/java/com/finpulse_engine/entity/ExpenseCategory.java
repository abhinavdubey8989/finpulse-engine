package com.finpulse_engine.entity;


import com.finpulse_engine.enums.DifferentiatorType;
import com.finpulse_engine.psql_enum.DifferentiatorTypeHandler;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.util.UUID;


@Entity
@Table(name = "expense_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseCategory {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 40)
    private String category;

    @Column(
            name = "monthly_upper_limit",
            nullable = false
    )
    private Integer monthlyUpperLimit;

    @Column()
    private String description;

    @Type(DifferentiatorTypeHandler.class)
    @Column(name = "type",
            columnDefinition = "differentiator_type",
            nullable = false)
    private DifferentiatorType type;

    @Column(name = "group_id")
    private UUID groupId;

    // Date fields
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
