package com.serch.server.models.account;

import com.serch.server.bases.BaseDateTime;
import com.serch.server.generators.account.AccountRequestID;
import com.serch.server.models.auth.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(schema = "account", name = "information_requests")
public class AccountRequest extends BaseDateTime {
    @Id
    @GenericGenerator(type = AccountRequestID.class, name = "account_request_seq")
    @GeneratedValue(generator = "account_request_seq")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String id;

    @Column(name = "file", columnDefinition = "TEXT")
    private String fileUrl = null;

    @Column(name = "collect_time")
    @Future(message = "Collect time must either be future or present")
    private LocalDateTime collectTime = null;

    @Column(name = "expires_at")
    @Future(message = "Expiration time must either be future or present")
    private LocalDateTime expireTime = null;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "serch_id",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(name = "user_id_fkey")
    )
    private User user;
}
