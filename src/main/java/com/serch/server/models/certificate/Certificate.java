package com.serch.server.models.certificate;

import com.serch.server.annotations.CoreID;
import com.serch.server.bases.BaseDateTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "platform", name = "certificates")
public class Certificate extends BaseDateTime {
    @Id
    @CoreID(name = "cert_generator", prefix = "SCERT", replaceSymbols = true, end = 10)
    @Column(nullable = false, columnDefinition = "TEXT")
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