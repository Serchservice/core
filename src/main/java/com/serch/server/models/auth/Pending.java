package com.serch.server.models.auth;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
@Table(schema = "identity", name = "pending")
public class Pending extends BaseModel {
    @Column(nullable = false, columnDefinition = "TEXT")
    private String secret;

    @Column(columnDefinition = "TEXT")
    private String scope;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "user_session_fkey")
    )
    @ToString.Exclude
    private User user;
}
