package com.serch.server.domains.nearby.listeners;

import com.serch.server.domains.nearby.services.interest.services.GoInterestTrendService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class InterestGrowthListener {
    private static final Logger log = LoggerFactory.getLogger(InterestGrowthListener.class);

    private final JdbcTemplate jdbcTemplate;
    private final GoInterestTrendService trendService;

    @PostConstruct
    public void listenForInterestGrowth() {
//        new Thread(() -> {
//            try (Connection conn = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
//                Statement stmt = conn.createStatement()) {
//
//                stmt.execute("LISTEN interest_growth");
//                PGConnection pgConn = conn.unwrap(PGConnection.class);
//
//                while (true) {
//                    PGNotification[] notifications = pgConn.getNotifications();
//                    if (notifications != null) {
//                        for (PGNotification notification : notifications) {
//                            String payload = notification.getParameter(); // Extract JSON string
//                            System.out.println("Received Notification: " + payload);
//
//                            JSONObject json = new JSONObject(payload);
//                            long userInterestId = json.getLong("user_interest_id");
//
//                            trendService.onTrending(userInterestId);
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                log.error(e.getMessage());
//            }
//        }).start();
    }
}
