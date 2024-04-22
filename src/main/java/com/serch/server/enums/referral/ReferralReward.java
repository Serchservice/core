package com.serch.server.enums.referral;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReferralReward {
    REFER_TIERED(
            "This referral program rewards you for your successful referrals in tiers. As you refer more friends to our platform, you'll unlock increasing rewards. For every total of 5, 10, 15, etc. referrals made by your referrals, you'll earn credits. The more active your network becomes, the higher your rewards will be, providing ongoing incentives to grow your referral network and earn more.",
            500
    ),
    ME_TIERED(
            "This referral program rewards you based on your own referral activity. As you personally refer more friends to our platform, you'll unlock increasing rewards. For every total of 5, 10, 15, etc. referrals made by you, you'll earn credits. Your individual efforts in bringing new users to our platform are recognized and rewarded, encouraging you to continue spreading the word about our services and earning credits along the way.",
            500
    ),
    TIME_LIMITED(
            "This referral program offers time-limited rewards based on your referrals' referrals activity during specific periods of the month. From the 1st to the 15th of each month, if they refer 5-10 persons within 15 days, you'll earn credits. This time-limited promotion provides an extra incentive to ramp up your referrals' efforts during these periods, maximizing your earning potential within a defined timeframe.",
            800
    ),
    ME_TIME_LIMITED(
            "This referral program offers time-limited rewards based on your referral activity during specific periods of the month. From the 1st to the 15th of each month, if you refer 5-10 persons within 15 days, you'll earn credits. This time-limited promotion provides an extra incentive to ramp up your referral efforts during these periods, maximizing your earning potential within a defined timeframe.",
            800
    ),
    TRIP_MILESTONE(
            "This referral program rewards you for each trip taken by your referrals. Whenever your referrals go on trips using our platform, you'll earn credits for each trip made by them. Your efforts in encouraging your network to use our platform for their travel needs are recognized and rewarded, providing an additional incentive to promote our services and earn more rewards.",
            50
    ),
    ME_TRIP_MILESTONE(
            "This referral program rewards you for each trip you personally take on our platform. Whenever you go on trips using our platform, you'll earn credits for each trip made by you. By personally going on trips with our platform, you contribute directly to our platform's growth and success, earning rewards for each trip booked by you.",
            50
    ),
    SHARE_LOYALTY(
            "This referral program rewards you for each sharing link your referrals generate that brings new guests on our platform. For each sharing link provided, that leads to a new guest sign-up, you'll earn credits. Whether they're sharing with friends, family, or their social network, every new sign-up attributed to their shared link adds to your earnings, incentivizing you to share and promote our platform.",
            40
    ),
    ME_SHARE_LOYALTY(
            "This referral program rewards you for sharing links that generate new guests on our platform. For each sharing link you provide that leads to a new guest sign-up, you'll earn credits. Whether you're sharing with friends, family, or your social network, every new sign-up attributed to your shared link adds to your earnings, incentivizing you to share and promote our platform.",
            40
    );

    private final String description;
    private final Integer credit;
}