package com.serch.server.domains.nearby.mappers;

import com.serch.server.core.file.responses.FileUploadResponse;
import com.serch.server.core.location.responses.Address;
import com.serch.server.core.payment.responses.PaymentAuthorization;
import com.serch.server.domains.nearby.bases.GoBaseUserResponse;
import com.serch.server.domains.nearby.models.go.GoBCap;
import com.serch.server.domains.nearby.models.go.GoLocation;
import com.serch.server.domains.nearby.models.go.GoMedia;
import com.serch.server.domains.nearby.models.go.GoPaymentAuthorization;
import com.serch.server.domains.nearby.models.go.activity.GoActivity;
import com.serch.server.domains.nearby.models.go.activity.GoActivityComment;
import com.serch.server.domains.nearby.models.go.activity.GoActivityRating;
import com.serch.server.domains.nearby.models.go.activity.GoAttendingUser;
import com.serch.server.domains.nearby.models.go.addon.GoAddon;
import com.serch.server.domains.nearby.models.go.addon.GoAddonPlan;
import com.serch.server.domains.nearby.models.go.interest.GoInterest;
import com.serch.server.domains.nearby.models.go.interest.GoInterestCategory;
import com.serch.server.domains.nearby.models.go.user.GoUser;
import com.serch.server.domains.nearby.models.go.user.GoUserAddon;
import com.serch.server.domains.nearby.services.account.requests.GoAccountUpdateRequest;
import com.serch.server.domains.nearby.services.account.responses.GoAccountResponse;
import com.serch.server.domains.nearby.services.account.responses.GoLocationResponse;
import com.serch.server.domains.nearby.services.activity.requests.GoCreateActivityRequest;
import com.serch.server.domains.nearby.services.activity.responses.GoActivityLifecycleResponse;
import com.serch.server.domains.nearby.services.activity.responses.GoActivityResponse;
import com.serch.server.domains.nearby.services.addon.responses.GoAddonPlanResponse;
import com.serch.server.domains.nearby.services.addon.responses.GoAddonResponse;
import com.serch.server.domains.nearby.services.addon.responses.GoAddonVerificationResponse;
import com.serch.server.domains.nearby.services.addon.responses.GoUserAddonResponse;
import com.serch.server.domains.nearby.services.auth.requests.GoAuthRequest;
import com.serch.server.domains.nearby.services.auth.responses.GoAuthResponse;
import com.serch.server.domains.nearby.services.bcap.responses.GoBCapLifecycleResponse;
import com.serch.server.domains.nearby.services.bcap.responses.GoBCapResponse;
import com.serch.server.domains.nearby.services.comment.responses.GoActivityCommentResponse;
import com.serch.server.domains.nearby.services.interest.responses.GoInterestCategoryResponse;
import com.serch.server.domains.nearby.services.interest.responses.GoInterestResponse;
import com.serch.server.domains.nearby.services.rating.responses.GoActivityRatingResponse;
import com.serch.server.utils.MoneyUtil;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface GoMapper {
    GoMapper instance = Mappers.getMapper(GoMapper.class);

    @Mapping(target = "id", source = "id")
    GoAuthResponse auth(GoUser user);

    @Mappings({
            @Mapping(target = "messagingToken", source = "fcmToken"),
            @Mapping(target = "device", source = "device.device"),
            @Mapping(target = "name", source = "device.name"),
            @Mapping(target = "host", source = "device.host"),
            @Mapping(target = "platform", source = "device.platform"),
            @Mapping(target = "operatingSystem", source = "device.operatingSystem"),
            @Mapping(target = "operatingSystemVersion", source = "device.operatingSystemVersion"),
            @Mapping(target = "localHost", source = "device.localHost"),
            @Mapping(target = "ipAddress", source = "device.ipAddress"),
            @Mapping(target = "contact", expression = "java(request.getContact() == null || request.getContact().isEmpty() ? \"\" : request.getContact())"),
    })
    GoUser user(GoAuthRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mappings({
            @Mapping(target = "emailAddress", source = "emailAddress", ignore = true),
            @Mapping(target = "messagingToken", source = "fcmToken")
    })
    void update(GoAccountUpdateRequest request, @MappingTarget GoUser user);

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "emailConfirmedAt", source = "emailConfirmedAt", ignore = true),
            @Mapping(target = "location", expression = "java(GoMapper.instance.location(user.getLocation()))"),
    })
    GoAccountResponse account(GoUser user);

    @Mappings({
            @Mapping(target = "category", source = "category.name"),
            @Mapping(target = "categoryId", source = "category.id"),
            @Mapping(target = "title", expression = "java(interest.getTitle())"),
            @Mapping(target = "popularity", source = "popularity", ignore = true),
    })
    GoInterestResponse interest(GoInterest interest);

    @Mapping(target = "interests", source = "interests", ignore = true)
    GoInterestCategoryResponse category(GoInterestCategory category);

    @Mapping(target = "id", source = "id", ignore = true)
    GoLocation location(Address address);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", source = "id", ignore = true)
    void update(Address address, @MappingTarget GoLocation location);

    @Mapping(target = "id", source = "id", ignore = true)
    GoLocationResponse location(GoLocation location);

    @Mapping(target = "id", source = "id", ignore = true)
    GoLocation location(GoLocationResponse address);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", source = "id", ignore = true)
    void update(GoLocationResponse response, @MappingTarget GoLocation location);

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "startTime", source = "startTime", ignore = true),
            @Mapping(target = "endTime", source = "endTime", ignore = true),
            @Mapping(target = "timestamp", source = "date", ignore = true),
            @Mapping(target = "interest", source = "interest", ignore = true),
            @Mapping(target = "location", expression = "java(GoMapper.instance.location(activity.getUser().getLocation()))"),
            @Mapping(target = "user", expression = "java(GoMapper.instance.base(activity.getUser()))"),
            @Mapping(target = "attendingUsers", expression = "java(GoMapper.getAttendingUsers(activity.getAttendingUsers()))"),
            @Mapping(target = "images", expression = "java(GoMapper.getFiles(activity))"),
    })
    GoActivityResponse activity(GoActivity activity);

    GoBaseUserResponse base(GoUser user);

    FileUploadResponse fileUpload(GoMedia media);

    GoMedia media(FileUploadResponse response);

    @Mappings({
            @Mapping(target = "images", source = "images", ignore = true),
            @Mapping(target = "description", source = "message"),
            @Mapping(target = "location", source = "location", ignore = true),
            @Mapping(target = "interest", source = "interest", ignore = true),
            @Mapping(target = "startTime", expression = "java(com.serch.server.domains.nearby.services.activity.requests.GoCreateActivityRequest.buildTime(request.getStartTime()))"),
            @Mapping(target = "endTime", expression = "java(com.serch.server.domains.nearby.services.activity.requests.GoCreateActivityRequest.buildTime(request.getEndTime()))"),
    })
    GoActivity activity(GoCreateActivityRequest request);

    @Mappings({
            @Mapping(target = "name", source = "user.fullName"),
            @Mapping(target = "avatar", source = "user.avatar")
    })
    GoActivityCommentResponse comment(GoActivityComment comment);

    @Mappings({
            @Mapping(target = "name", source = "user.fullName"),
            @Mapping(target = "avatar", source = "user.avatar")
    })
    GoActivityRatingResponse rating(GoActivityRating rating);

    @Mappings({
            @Mapping(target = "message", source = "description"),
            @Mapping(target = "username", source = "user.fullName"),
            @Mapping(target = "longitude", source = "location.longitude"),
            @Mapping(target = "latitude", source = "location.latitude"),
            @Mapping(target = "address", source = "location.place"),
            @Mapping(target = "interest", source = "interest.id"),
            @Mapping(
                    target = "contact",
                    expression = """
                        java(activity.getUser().getContact().isEmpty() ? activity.getUser().getEmailAddress() : activity.getUser().getContact())
                    """
            ),
    })
    GoActivityLifecycleResponse lifecycle(GoActivity activity);

    @Mappings({
            @Mapping(target = "activity", source = "activity.id"),
            @Mapping(target = "interest", expression = "java(GoMapper.instance.interest(cap.getActivity().getInterest()))"),
            @Mapping(target = "files", expression = "java(GoMapper.getFiles(cap))"),
    })
    GoBCapResponse cap(GoBCap cap);

    @Mappings({
            @Mapping(target = "activity", source = "activity.id"),
            @Mapping(target = "interest", source = "activity.interest.id"),
    })
    GoBCapLifecycleResponse lifecycle(GoBCap cap);

    static List<GoBaseUserResponse> getAttendingUsers(List<GoAttendingUser> users) {
        return users != null && !users.isEmpty()
                ? users.stream().map(user -> GoMapper.instance.base(user.getUser())).toList()
                : new ArrayList<>();
    }

    static List<FileUploadResponse> getFiles(GoActivity activity) {
        if(activity.getImages() != null && !activity.getImages().isEmpty()) {
            return activity.getImages().stream().map(GoMapper.instance::fileUpload).toList();
        } else {
            FileUploadResponse response = new FileUploadResponse();
            response.setFile(activity.getInterest().getImage());

            return new ArrayList<>(List.of(response));
        }
    }

    static List<FileUploadResponse> getFiles(GoBCap cap) {
        if(cap.getMedia() != null && !cap.getMedia().isEmpty()) {
            return cap.getMedia().stream().map(GoMapper.instance::fileUpload).toList();
        } else if(cap.getActivity().getImages() != null && !cap.getActivity().getImages().isEmpty()) {
            return cap.getActivity().getImages().stream().map(GoMapper.instance::fileUpload).toList();
        } else {
            FileUploadResponse response = new FileUploadResponse();
            response.setFile(cap.getActivity().getInterest().getImage());

            return new ArrayList<>(List.of(response));
        }
    }

    @Mapping(target = "plans", source = "plans", ignore = true)
    GoAddonResponse addon(GoAddon addon);

    @Mapping(target = "plans", source = "plans", ignore = true)
    GoAddonVerificationResponse verification(GoAddon addon);

    @Mapping(target = "amount", expression = "java(GoMapper.getAmount(addon))")
    GoAddonPlanResponse plan(GoAddonPlan addon);

    static String getAmount(GoAddonPlan plan) {
        return MoneyUtil.format(plan.getAmount(), plan.getCurrency());
    }

    GoPaymentAuthorization auth(PaymentAuthorization authorization);

    GoUserAddonResponse.Card card(GoPaymentAuthorization authorization);

    @Mapping(target = "amount", expression = "java(GoMapper.getAmount(plan))")
    GoUserAddonResponse.Switch switching(GoAddonPlan plan);

    @Mappings({
            @Mapping(target = "name", source = "plan.name"),
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "description", source = "plan.description"),
            @Mapping(target = "interval", source = "plan.interval"),
            @Mapping(target = "recurring", source = "isRecurring"),
            @Mapping(target = "amount", expression = "java(GoMapper.getAmount(addon.getPlan()))")
    })
    GoUserAddonResponse response(GoUserAddon addon);
}