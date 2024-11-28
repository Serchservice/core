package com.serch.server.nearby.models;

import com.serch.server.bases.BaseDevice;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "devices", schema = "nearby")
public class Nearby extends BaseDevice {
    @Column(name = "fcm_token", columnDefinition = "TEXT", unique = true, nullable = false)
    private String fcmToken;
}