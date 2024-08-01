package com.serch.server.models.company;

import com.serch.server.bases.BaseModel;
import com.serch.server.enums.company.NewsletterStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "company", name = "newsletter")
public class Newsletter extends BaseModel {
    @Column(nullable = false, updatable = false, name = "email_address", unique = true)
    private String emailAddress;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private NewsletterStatus status = NewsletterStatus.UNCOLLECTED;
}