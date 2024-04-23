package com.serch.server.models.account;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseModel;
import com.serch.server.enums.account.Gender;
import com.serch.server.models.auth.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an account settings entity, storing information about the user preferences.
 * <p></p>
 * Relationships:
 * <ol>
 *     <li>{@link com.serch.server.models.auth.User} - One to one</li>
 * </ol>
 *
 * @see com.serch.server.bases.BaseDateTime
 * @see com.serch.server.annotations.SerchEnum
 */
@Getter
@Setter
@Entity
@Table(schema = "account", name = "settings")
public class AccountSetting extends BaseModel {
    @Column(name = "gender_for_trip", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "Gender must be an enum")
    private Gender gender = Gender.ANY;

    /**
     * The user associated with the deletion request.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "serch_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "user_id_fkey")
    )
    private User user;
}