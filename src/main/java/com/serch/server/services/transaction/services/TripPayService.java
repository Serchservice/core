//package com.serch.server.services.transaction.services;
//
//import com.serch.server.bases.ApiResponse;
//import com.serch.server.services.transaction.requests.PayRequest;
//
///**
// * This is the wrapper class for trip payment and transaction view
// */
//public interface TripPayService {
//    /**
//     * Initiates a payment based on the provided request.
//     *
//     * @param request The payment request details.
//     * @return An ApiResponse containing the result of the payment initiation.
//     */
//    ApiResponse<String> pay(PayRequest request);
//
////    /**
////     * Processes a payment for a trip using the wallet balance.
////     *
////     * @param request The payment request for the trip.
////     * @return An ApiResponse containing the result of the payment.
////     */
////    ApiResponse<String> paySubscription(PayRequest request);
//
//    /**
//     * Processes a payment for a subscription using the wallet balance.
//     *
//     * @param request The payment request for the subscription.
//     * @return An ApiResponse containing the result of the payment.
//     */
//    ApiResponse<String> payTrip(PayRequest request);
//
//    /**
//     * Checks if a user can pay for a trip using the wallet balance.
//     *
//     * @param trip The trip for which the payment is being checked.
//     * @return An ApiResponse indicating if the user can pay for the trip with the wallet balance.
//     */
//    ApiResponse<String> checkIfUserCanPayForTripWithWallet(String trip);
//}
