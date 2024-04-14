package com.serch.server.bases;

import com.serch.server.models.auth.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

/**
 * The BaseUser class serves as a base entity for user-related entities in the system,
 * extending the functionality of BaseDateTime with user-specific attributes.
 * <p></p>
 * It includes fields such as serchId and a reference to the User entity.
 * This class is annotated with JPA annotations for entity mapping and auditing behavior.
 * It also provides constructors for creating instances with and without arguments.
 *
 * @see BaseDateTime
 * @see MappedSuperclass
 * @see org.springframework.data.jpa.domain.support.AuditingEntityListener
 */
@Getter
@Setter
@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BaseUser extends BaseDateTime {
    /**
     * The unique identifier for the user entity.
     */
    @Id
    @Column(name = "serch_id", nullable = false)
    private UUID serchId;

    /**
     * The reference to the User entity.
     */
    @OneToOne
    @MapsId
    @JoinColumn(
            name = "serch_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "profile_user_id_fkey")
    )
    private User user;
}