package com.bizuno.models.main;

import com.bizuno.enums.ModelEnums;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "audit_logs")
public class AuditLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID auditLogId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "role is required")
    @Enumerated(EnumType.STRING)
    private ModelEnums.AuditAction action;

    @NotNull(message = "module is required")
    @Enumerated(EnumType.STRING)
    private ModelEnums.PlatformAuditModule module;

    @NotNull(message = "role is required")
    private String role;

    private Long entityId;

    @NotBlank(message = "description is required")
    private String description;

    private String ipAddress;
}

