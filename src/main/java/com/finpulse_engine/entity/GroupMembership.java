package com.finpulse_engine.entity;


import com.finpulse_engine.entity.enum_converter.GroupRoleConverter;
import com.finpulse_engine.enums.GroupRole;
import com.finpulse_engine.psql_enum.GroupRoleArrayType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.jdbc.ArrayJdbcType;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "group_membership")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMembership {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "group_id", nullable = false)
    private UUID groupId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Type(GroupRoleArrayType.class)
    @Column(name = "group_roles", columnDefinition = "group_roles[]")
    @Builder.Default
    private List<GroupRole> groupRoles = new ArrayList<>();

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
