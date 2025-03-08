package com.serch.server.domains.nearby.models.go.interest;

import com.serch.server.bases.BaseModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@Table(schema = "nearby", name = "go_interests")
public class GoInterest extends BaseModel {
    @Column(nullable = false, columnDefinition = "TEXT DEFAULT ''")
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT DEFAULT ''")
    private String emoji;

    @Column(nullable = false, columnDefinition = "TEXT DEFAULT ''")
    private String verb;

    @Column(nullable = false, columnDefinition = "TEXT DEFAULT ''")
    private String image;

    @Column(nullable = false)
    private Long popularity = 0L;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(
            name = "category_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "go_interest_category_id_fkey")
    )
    private GoInterestCategory category;

    public String getTitle() {
        if(getEmoji().isEmpty()) {
            return getName();
        } else {
            return "%s %s".formatted(getEmoji(), getName());
        }
    }
}