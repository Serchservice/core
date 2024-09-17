package com.serch.server.models.auth.incomplete;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseModel;
import com.serch.server.enums.account.SerchCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The IncompleteCategory class represents incomplete category information in the system.
 * It stores information about incomplete categories, including the category and the associated incomplete profile.
 * <p></p>
 * Relationships:
 * <ul>
 *     <li>{@link Incomplete} - The incomplete object associated with the incomplete referral.</li>
 * </ul>
 * Enums:
 * <ul>
 *     <li>{@link SerchCategory}</li>
 * </ul>
 * @see BaseModel
 * @see SerchEnum
 */
@ToString
@Getter
@Setter
@Entity
@Table(schema = "identity", name = "incomplete_category")
public class IncompleteCategory extends BaseModel {
    @Column(name = "category", nullable = false)
    @SerchEnum(message = "SerchCategory must be an enum")
    @Enumerated(value = EnumType.STRING)
    private SerchCategory category;

    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "incomplete_email",
            referencedColumnName = "email_address",
            nullable = false,
            foreignKey = @ForeignKey(name = "category_email_fkey")
    )
    @ToString.Exclude
    private Incomplete incomplete;
}
