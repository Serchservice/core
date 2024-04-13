package com.serch.server.models.company;

import com.serch.server.annotations.SerchEnum;
import com.serch.server.bases.BaseDateTime;
import com.serch.server.enums.account.AccountStatus;
import com.serch.server.enums.company.ProductType;
import com.serch.server.generators.ProductID;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "company", name = "products")
public class Product extends BaseDateTime {
    @Id
    @Column(name = "id", nullable = false, columnDefinition = "TEXT")
    @GenericGenerator(name = "product_seq", type = ProductID.class)
    @GeneratedValue(generator = "product_seq")
    private String id;

    @Column(nullable = false)
    private Double rating = 5.0;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    @SerchEnum(message = "Product must be of ProductType enum")
    private ProductType type;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<Issue> issues;
}
