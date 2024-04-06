package com.serch.server.models.account;

import com.serch.server.bases.BaseProfile;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.models.shop.Shop;
import com.serch.server.models.verified.Verification;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "account", name = "business_profiles")
public class BusinessProfile extends BaseProfile {
    @Column(name = "name", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Business name cannot be empty or null")
    private String businessName;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Business description cannot be empty or null")
    private String businessDescription;

    @Column(name = "address", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Business address cannot be empty or null")
    private String businessAddress;

    @Column(name = "default_password", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Default Password cannot be empty or null")
    private String defaultPassword;

    @Column(name = "category", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private SerchCategory category;

    @OneToOne(mappedBy = "business", cascade = CascadeType.ALL)
    private Verification verification;

    @OneToMany(mappedBy = "business", fetch = FetchType.LAZY)
    private List<Profile> associates;

    @OneToMany(mappedBy = "business", fetch = FetchType.LAZY)
    private List<Shop> shops;
}
