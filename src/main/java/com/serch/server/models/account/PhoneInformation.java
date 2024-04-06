package com.serch.server.models.account;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "account", name = "phone_information")
public class PhoneInformation extends BaseModel {
    @Column(name = "number", nullable = false, columnDefinition = "TEXT")
    private String phoneNumber;

    @Column(name = "country_code", nullable = false, columnDefinition = "TEXT")
    private String countryCode;

    @Column(name = "iso_code", nullable = false, columnDefinition = "TEXT")
    private String isoCode;

    @Column(name = "country", nullable = false, columnDefinition = "TEXT")
    private String country;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "serch_id",
            foreignKey = @ForeignKey(name = "user_phone_fkey")
    )
    private Profile profile;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "business_id",
            referencedColumnName = "serch_id",
            foreignKey = @ForeignKey(name = "business_phone_fkey")
    )
    private BusinessProfile business;
}
