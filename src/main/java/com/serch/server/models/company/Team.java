package com.serch.server.models.company;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseModel;
import com.serch.server.enums.company.TeamType;
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
@Table(schema = "company", name = "teams")
public class Team extends BaseModel {
    @Column(columnDefinition = "TEXT", nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String position;

    @Column(columnDefinition = "TEXT")
    private String image;

    @Column(columnDefinition = "TEXT")
    private String about;

    @Column(columnDefinition = "TEXT")
    private String link;

    @Column(nullable = false)
    private Integer hierarchy = 0;

    @Column(name = "team", nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "TeamType must be an enum")
    private TeamType team;
}