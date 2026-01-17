package com.finpulse_engine.entity;


import com.finpulse_engine.enums.SplitType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "group_expenses")
@Data                       // Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupExpense {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer month;

    @Column(name = "paid_by", nullable = false)
    private UUID paidBy;

    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    @Column(name = "tag_id")
    private UUID tagId;

    @Column(name = "group_id", nullable = false)
    private UUID groupId;

    @Column(nullable = false)
    private Integer amount;

    @Column()
    private String description;

    @Column(name = "split_type",
            columnDefinition = "split_type",
            nullable = false)
    private SplitType splitType;

    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, Integer> splits;

    @Column(name = "due_amounts",
            columnDefinition = "jsonb",
            nullable = false)
    private Map<String, Integer> dueAmounts;

    @Column(name = "created_at", updatable = false)
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

