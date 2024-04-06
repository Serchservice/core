package com.serch.server.models.auth.incomplete;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(schema = "identity", name = "incomplete_phone_information")
public class IncompletePhoneInformation extends BaseModel {
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
            name = "incomplete_email",
            referencedColumnName = "email_address",
            nullable = false,
            foreignKey = @ForeignKey(name = "additional_email_fkey")
    )
    private Incomplete incomplete;
}
