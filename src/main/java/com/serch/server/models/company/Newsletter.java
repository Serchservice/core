package com.serch.server.models.company;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
}