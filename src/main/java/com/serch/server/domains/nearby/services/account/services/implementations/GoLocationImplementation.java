package com.serch.server.domains.nearby.services.account.services.implementations;

import com.serch.server.core.location.responses.Address;
import com.serch.server.domains.nearby.services.account.responses.GoLocationResponse;
import com.serch.server.domains.nearby.services.account.services.GoLocationService;
import com.serch.server.exceptions.others.SerchException;
import com.serch.server.domains.nearby.mappers.GoMapper;
import com.serch.server.domains.nearby.models.go.activity.GoActivity;
import com.serch.server.domains.nearby.models.go.user.GoUser;
import com.serch.server.domains.nearby.models.go.GoLocation;
import com.serch.server.domains.nearby.repositories.go.GoLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoLocationImplementation implements GoLocationService {
    private final GoLocationRepository goLocationRepository;

    @Override
    public void put(GoUser user, Address address) {
        if(address != null) {
            goLocationRepository.findByUser_Id(user.getId()).ifPresentOrElse(location -> {
                GoMapper.instance.update(address, location);
                location = goLocationRepository.save(location);

                user.setLocation(location);
            }, () -> {
                GoLocation location = GoMapper.instance.location(address);
                location.setUser(user);
                location = goLocationRepository.save(location);

                user.setLocation(location);
            });
        }
    }

    @Override
    public void put(GoActivity activity, GoLocationResponse response) {
        if(response == null) {
            throw new SerchException("Event location must be provided");
        } else {
            goLocationRepository.findByActivity_Id(activity.getId()).ifPresentOrElse(location -> {
                GoMapper.instance.update(response, location);
                location = goLocationRepository.save(location);

                activity.setLocation(location);
            }, () -> {
                GoLocation location = GoMapper.instance.location(response);
                location.setActivity(activity);
                location = goLocationRepository.save(location);

                activity.setLocation(location);
            });

        }
    }
}