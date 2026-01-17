package com.finpulse_engine.repository;

import com.finpulse_engine.entity.GroupMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface GroupMembershipRepository extends JpaRepository<GroupMembership, UUID> {
    boolean existsByGroupIdAndUserId(UUID groupId, UUID userId);
    GroupMembership findByGroupIdAndUserId(UUID groupId, UUID userId);

}