package com.serch.server.models.account;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseProfile;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.auth.Role;
import com.serch.server.models.certificate.Certificate;
import com.serch.server.models.verified.Verification;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "account", name = "profiles")
public class Profile extends BaseProfile {
    @Column(name = "serch_category", nullable = false)
    @SerchEnum(message = "SerchCategory must be an enum")
    @Enumerated(value = EnumType.STRING)
    private SerchCategory category;

    @OneToOne(mappedBy = "profile")
    private Certificate certificate;

    @OneToOne(mappedBy = "profile", cascade = CascadeType.ALL)
    private Verification verification;

    @OneToMany(mappedBy = "profile", fetch = FetchType.LAZY)
    private List<Specialty> specializations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "business_id",
            referencedColumnName = "serch_id",
            foreignKey = @ForeignKey(name = "profile_business_id_fkey")
    )
    private BusinessProfile business = null;

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    public boolean belongsToBusiness(UUID serchId) {
        return getBusiness() != null && getBusiness().getId() == serchId
                && getUser().getRole() == Role.ASSOCIATE_PROVIDER;
    }

    public boolean isAssociate() {
        return getBusiness() != null && getUser().getRole() == Role.ASSOCIATE_PROVIDER;
    }

    public boolean isSameAs(UUID user) {
        return getId() != null && getId() == user;
    }
}
