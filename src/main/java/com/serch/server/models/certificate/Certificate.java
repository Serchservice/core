package com.serch.server.models.certificate;

import com.serch.server.bases.BaseDateTime;
import com.serch.server.generators.CertificateID;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "platform", name = "certificates")
public class Certificate extends BaseDateTime {
    @Id
    @Column(nullable = false, columnDefinition = "TEXT")
    @GenericGenerator(type = CertificateID.class, name = "cert_seq_key")
    @GeneratedValue(generator = "cert_seq_key")
    private String id;

    @Column(columnDefinition = "TEXT")
    private String document;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String secret;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String code;

    @Column(nullable = false, name = "user_id", unique = true)
    private UUID user;
}