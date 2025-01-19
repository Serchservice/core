package com.serch.server.nearby.services.drive.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.nearby.mappers.NearbyMapper;
import com.serch.server.nearby.models.NearbyCategory;
import com.serch.server.nearby.models.NearbyShop;
import com.serch.server.nearby.models.NearbyTimeline;
import com.serch.server.nearby.repositories.NearbyCategoryRepository;
import com.serch.server.nearby.repositories.NearbyShopRepository;
import com.serch.server.nearby.repositories.NearbyTimelineRepository;
import com.serch.server.nearby.services.drive.requests.NearbyDriveRequest;
import com.serch.server.nearby.services.drive.services.NearbyDriveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NearbyDriveImplementation implements NearbyDriveService {
    private final NearbyCategoryRepository nearbyCategoryRepository;
    private final NearbyTimelineRepository nearbyTimelineRepository;
    private final NearbyShopRepository nearbyShopRepository;

    @Override
    public ApiResponse<String> search(String type) {
        Optional<NearbyCategory> optional = nearbyCategoryRepository.findByTypeIgnoreCase(type);

        NearbyCategory category;
        if (optional.isPresent()) {
            category = optional.get();
            category.increment();
            nearbyCategoryRepository.save(category);
        } else {
            category = getNearbyCategory(type);
        }

        NearbyTimeline timeline = new NearbyTimeline();
        timeline.setCategory(category);
        timeline.setOption("DRIVE SEARCH");
        nearbyTimelineRepository.save(timeline);

        return new ApiResponse<>("Success", HttpStatus.OK);
    }

    private NearbyCategory getNearbyCategory(String type) {
        NearbyCategory data = new NearbyCategory();
        data.setType(type);

        return nearbyCategoryRepository.save(data);
    }

    @Override
    public ApiResponse<String> drive(NearbyDriveRequest request) {
        Optional<NearbyShop> optional = nearbyShopRepository.findById(request.getId());

        NearbyShop shop;
        if (optional.isPresent()) {
            shop = optional.get();
            shop.increment();
            nearbyShopRepository.save(shop);
        } else {
            shop = getNearbyShop(request);
        }

        NearbyTimeline timeline = new NearbyTimeline();
        timeline.setShop(shop);
        timeline.setOption(request.getOption());
        nearbyTimelineRepository.save(timeline);

        return new ApiResponse<>("Success", HttpStatus.OK);
    }

    private NearbyShop getNearbyShop(NearbyDriveRequest request) {
        NearbyShop data = NearbyMapper.instance.shop(request);

        return nearbyShopRepository.save(data);
    }
}