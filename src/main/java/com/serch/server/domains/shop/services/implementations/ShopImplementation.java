package com.serch.server.domains.shop.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.file.services.FileService;
import com.serch.server.core.location.services.LocationService;
import com.serch.server.domains.shop.requests.*;
import com.serch.server.domains.shop.responses.SearchShopResponse;
import com.serch.server.domains.shop.responses.ShopResponse;
import com.serch.server.domains.shop.responses.ShopWeekdayResponse;
import com.serch.server.domains.shop.services.ShopService;
import com.serch.server.enums.shop.DriveScope;
import com.serch.server.enums.shop.ShopStatus;
import com.serch.server.enums.shop.Weekday;
import com.serch.server.exceptions.others.ShopException;
import com.serch.server.mappers.ShopMapper;
import com.serch.server.models.shop.Shop;
import com.serch.server.models.shop.ShopSpecialty;
import com.serch.server.models.shop.ShopWeekday;
import com.serch.server.repositories.shop.ShopRepository;
import com.serch.server.repositories.shop.ShopServiceRepository;
import com.serch.server.repositories.shop.ShopWeekdayRepository;
import com.serch.server.utils.AuthUtil;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Implementation of {@link ShopService} interface, providing methods for shop management operations.
 *
 * @see AuthUtil
 * @see ShopRepository
 * @see ShopServiceRepository
 */
@Service
@RequiredArgsConstructor
public class ShopImplementation implements ShopService {
    private final FileService uploadService;
    private final LocationService locationService;
    private final AuthUtil authUtil;
    private final ShopRepository shopRepository;
    private final ShopServiceRepository shopServiceRepository;
    private final ShopWeekdayRepository shopWeekdayRepository;

    @Value("${application.map.search-radius}")
    private String MAP_SEARCH_RADIUS;

    @Override
    public ShopResponse response(Shop shop) {
        ShopResponse response = ShopMapper.INSTANCE.shop(shop);
        response.setOpen(shop.isOpen());

        if(shop.getWeekdays() != null && !shop.getWeekdays().isEmpty()) {
            response.setWeekdays(shop.getWeekdays().stream().map(this::response).toList());
            DayOfWeek currentDay = LocalDateTime.now().getDayOfWeek();
            shopWeekdayRepository.findByDayAndShop_Id(Weekday.valueOf(currentDay.name()), shop.getId())
                    .ifPresent(weekday -> response.setCurrent(response(weekday)));
        }

        if(shop.getServices() != null && !shop.getServices().isEmpty()) {
            response.setServices(shop.getServices().stream().map(ShopMapper.INSTANCE::service).toList());
        }

        return response;
    }

    private ShopWeekdayResponse response(ShopWeekday weekday) {
        ShopWeekdayResponse response = ShopMapper.INSTANCE.weekday(weekday);
        response.setDay(weekday.getDay().getDay());
        response.setClosing(TimeUtil.toString(weekday.getClosing()));
        response.setOpening(TimeUtil.toString(weekday.getOpening()));
        response.setOpen(weekday.getShop().isOpen());

        return response;
    }

    protected ApiResponse<ShopResponse> getUpdatedShopResponse(String shopId) {
        Shop updatedShop = shopRepository.findByIdAndUser_Id(shopId, authUtil.getUser().getId())
                .orElseThrow(() -> new ShopException("Shop not found"));

        return new ApiResponse<>(response(updatedShop));
    }

    @Override
    public ApiResponse<List<ShopResponse>> create(CreateShopRequest request) {
        if(HelperUtil.isUploadEmpty(request.getUpload())) {
            throw new ShopException("Shop logo or image is needed");
        } else {
            Shop shop = getShop(request);

            if(request.getServices() != null && !request.getServices().isEmpty()) {
                request.getServices()
                        .stream()
                        .map(s -> {
                            ShopSpecialty service = new ShopSpecialty();
                            service.setShop(shop);
                            service.setService(s);
                            return service;
                        }).forEach(shopServiceRepository::save);
            }
            if(request.getWeekdays() != null && !request.getWeekdays().isEmpty()) {
                request.getWeekdays()
                        .stream()
                        .map(s -> {
                            ShopWeekday weekday = new ShopWeekday();
                            weekday.setShop(shop);
                            weekday.setClosing(TimeUtil.toTime(s.getClosing()));
                            weekday.setOpening(TimeUtil.toTime(s.getOpening()));
                            weekday.setDay(s.getDay());
                            return weekday;
                        }).forEach(shopWeekdayRepository::save);
            }
        }

        return fetch(null, null);
    }

    private Shop getShop(CreateShopRequest request) {
        Shop shop = ShopMapper.INSTANCE.shop(request);
        shop.setUser(authUtil.getUser());
        shop = shopRepository.save(shop);

        String logo = uploadService.uploadShop(request.getUpload(), shop.getId()).getFile();
        shop.setLogo(logo);

        return shopRepository.save(shop);
    }

    @Override
    public ApiResponse<ShopResponse> create(String shopId, ShopWeekdayRequest request) {
        Shop shop = shopRepository.findByIdAndUser_Id(shopId, authUtil.getUser().getId())
                .orElseThrow(() -> new ShopException("Shop not found"));

        ShopWeekday weekday = new ShopWeekday();
        weekday.setShop(shop);
        weekday.setClosing(TimeUtil.toTime(request.getClosing()));
        weekday.setOpening(TimeUtil.toTime(request.getOpening()));
        weekday.setDay(request.getDay());
        shopWeekdayRepository.save(weekday);

        return getUpdatedShopResponse(shopId);
    }

    @Override
    public ApiResponse<ShopResponse> create(CreateShopServiceRequest request) {
        Shop shop = shopRepository.findByIdAndUser_Id(request.getId(), authUtil.getUser().getId())
                .orElseThrow(() -> new ShopException("Shop not found"));

        ShopSpecialty newService = new ShopSpecialty();
        newService.setShop(shop);
        newService.setService(request.getService());
        shopServiceRepository.save(newService);

        return getUpdatedShopResponse(request.getId());
    }

    @Override
    public ApiResponse<ShopResponse> update(UpdateShopRequest request) {
        Shop shop = shopRepository.findByIdAndUser_Id(request.getShop(), authUtil.getUser().getId())
                .orElseThrow(() -> new ShopException("Shop not found"));

        if(!shop.getName().equalsIgnoreCase(request.getName())) {
            shop.setName(request.getName());
        }
        if(!shop.getAddress().equalsIgnoreCase(request.getAddress())) {
            shop.setAddress(request.getAddress());
        }
        if(!shop.getPhoneNumber().equalsIgnoreCase(request.getPhoneNumber())) {
            shop.setPhoneNumber(request.getPhoneNumber());
        }
        if(shop.getCategory() != request.getCategory()) {
            shop.setCategory(request.getCategory());
        }
        if(!shop.getLatitude().equals(request.getLatitude())) {
            shop.setLatitude(request.getLatitude());
        }
        if(!shop.getLongitude().equals(request.getLongitude())) {
            shop.setLongitude(request.getLongitude());
        }
        shop.setUpdatedAt(TimeUtil.now());
        shopRepository.save(shop);
        return getUpdatedShopResponse(request.getShop());
    }

    @Override
    public ApiResponse<ShopResponse> update(Long id, String shopId, ShopWeekdayRequest request) {
        ShopWeekday weekday = shopWeekdayRepository.findByIdAndShop_Id(id, shopId)
                .orElseThrow(() -> new ShopException("Weekday not found"));

        if(!weekday.getClosing().equals(TimeUtil.toTime(request.getClosing()))) {
            weekday.setClosing(TimeUtil.toTime(request.getClosing()));
        }
        if(!weekday.getOpening().equals(TimeUtil.toTime(request.getOpening()))) {
            weekday.setOpening(TimeUtil.toTime(request.getOpening()));
        }
        weekday.setUpdatedAt(TimeUtil.now());
        shopWeekdayRepository.save(weekday);

        return getUpdatedShopResponse(shopId);
    }

    @Override
    public ApiResponse<ShopResponse> update(UpdateShopServiceRequest request) {
        ShopSpecialty shopSpecialty = shopServiceRepository.findByIdAndShop_Id(request.getId(), request.getShop())
                .orElseThrow(() -> new ShopException("Service not found"));

        if(!shopSpecialty.getService().equalsIgnoreCase(request.getService())) {
            shopSpecialty.setService(request.getService());
            shopSpecialty.setUpdatedAt(TimeUtil.now());
            shopServiceRepository.save(shopSpecialty);
        }

        return getUpdatedShopResponse(request.getShop());
    }

    @Override
    public ApiResponse<List<ShopResponse>> fetch(Integer page, Integer size) {
        Page<Shop> shops = shopRepository.findByUser_Id(authUtil.getUser().getId(), HelperUtil.getPageable(page, size));

        List<ShopResponse> list = new ArrayList<>();

        if(shops != null && !shops.getContent().isEmpty()) {
            list = shops.getContent()
                    .stream()
                    .map(this::response)
                    .toList();
        }

        return new ApiResponse<>(list);
    }

    @Override
    public ApiResponse<ShopResponse> removeService(Long id, String shopId) {
        shopServiceRepository.findByIdAndShop_Id(id, shopId).ifPresent(shopServiceRepository::delete);
        return getUpdatedShopResponse(shopId);
    }

    @Override
    public ApiResponse<List<ShopResponse>> remove(String shopId) {
        shopRepository.findByIdAndUser_Id(shopId, authUtil.getUser().getId()).ifPresent(shopRepository::delete);
        return fetch(null, null);
    }

    @Override
    public ApiResponse<ShopResponse> removeWeekday(Long id, String shopId) {
        shopWeekdayRepository.findByIdAndShop_Id(id, shopId).ifPresent(shopWeekdayRepository::delete);
        return getUpdatedShopResponse(shopId);
    }

    @Override
    public ApiResponse<ShopResponse> changeStatus(String shopId, ShopStatus status) {
        Shop shop = shopRepository.findByIdAndUser_Id(shopId, authUtil.getUser().getId())
                .orElseThrow(() -> new ShopException("Shop not found"));
        shop.setStatus(status);
        shop.setUpdatedAt(TimeUtil.now());
        shopRepository.save(shop);

        return getUpdatedShopResponse(shopId);
    }

    @Override
    public ApiResponse<List<ShopResponse>> changeAllStatus() {
        List<Shop> shops = shopRepository.findByUser_Id(authUtil.getUser().getId());
        if(shops != null && !shops.isEmpty()) {
            shops.stream().filter(shop -> shop.getStatus() != ShopStatus.SUSPENDED).peek(shop -> {
                if(shop.getStatus() == ShopStatus.OPEN) {
                    shop.setStatus(ShopStatus.CLOSED);
                } else if(shop.getStatus() == ShopStatus.CLOSED) {
                    shop.setStatus(ShopStatus.OPEN);
                }
                shop.setUpdatedAt(TimeUtil.now());
            }).forEach(shopRepository::save);

            return fetch(null, null);
        } else {
            throw new ShopException("You have no shops");
        }
    }

    @Override
    public ApiResponse<List<SearchShopResponse>> drive(String query, String category, Double longitude, Double latitude, Double radius, DriveScope scope, Integer page, Integer size) {
        return new ApiResponse<>(list(
                query,
                category,
                longitude,
                latitude,
                radius == null ? Double.parseDouble(MAP_SEARCH_RADIUS) : radius,
                scope == null ? DriveScope.ALL : scope,
                page,
                size
        ));
    }

    @Override
    public List<SearchShopResponse> list(String query, String category, Double longitude, Double latitude, Double radius, DriveScope scope, Integer page, Integer size) {
        if((query == null || query.isEmpty()) && (category == null || category.isEmpty()) ) {
            throw new ShopException("Query and category cannot be null or empty");
        }

        List<SearchShopResponse> list = new ArrayList<>();

        if(scope == DriveScope.ALL) {
            addSerchShops(list, query, category, longitude, latitude, radius, page, size);
            if(page == null || page == 0) {
                addGoogleShops(list, query, category, longitude, latitude, radius);
            }
        } else if(scope == DriveScope.GOOGLE) {
            if(page == null || page == 0) {
                addGoogleShops(list, query, category, longitude, latitude, radius);
            }
        } else {
            addSerchShops(list, query, category, longitude, latitude, radius, page, size);
        }

        // Sort the list first by isGoogle (false first), then by distance, treating null as max value
        list.sort(Comparator.comparing(SearchShopResponse::getIsGoogle)
                .thenComparing(searchShopResponse -> {
                    Double distance = searchShopResponse.getDistance();
                    return distance != null ? distance : Double.MAX_VALUE; // Treat null as max value
                }));

        return list;
    }

    private void addGoogleShops(List<SearchShopResponse> list, String query, String category, Double longitude, Double latitude, Double radius) {
        List<SearchShopResponse> shops = locationService.nearbySearch(query, category, longitude, latitude, radius);

        if (shops != null && !shops.isEmpty()) {
            list.addAll(shops);
        }
    }

    private void addSerchShops(List<SearchShopResponse> list, String query, String category, Double longitude, Double latitude, Double radius, Integer page, Integer size) {
        Page<Shop> shops = getShops(query, category, longitude, latitude, radius, page, size);

        if(shops != null && !shops.getContent().isEmpty()) {
            shops.forEach(shop -> {
                SearchShopResponse response = new SearchShopResponse();
                double distance = HelperUtil.getDistance(latitude, longitude, shop.getLatitude(), shop.getLongitude());

                response.setDistanceInKm(distance + " km");
                response.setDistance(distance);
                response.setShop(response(shop));
                response.setUser(shop.getUser().getId());
                response.setIsGoogle(false);

                list.add(response);
            });
        }
    }

    private Page<Shop> getShops(String query, String category, Double longitude, Double latitude, Double radius, Integer page, Integer size) {
        if(query != null && !query.isEmpty()) {
            return shopRepository.findByServiceAndLocation(
                    latitude,
                    longitude,
                    query.equalsIgnoreCase("car_repair") ? "mechanic" : query.toLowerCase(),
                    radius,
                    HelperUtil.getPageable(page, size)
            );
        } else {
            return shopRepository.findByServiceAndLocation(latitude, longitude, category.toLowerCase(), radius, HelperUtil.getPageable(page, size));
        }
    }

    @Override
    @Transactional
    public void openOrCloseShops() {
        List<Shop> openingShops = shopRepository.findShopsWithCurrentOpeningTimeAndDay(Weekday.valueOf(LocalDateTime.now().getDayOfWeek().name()));
        if(openingShops != null && !openingShops.isEmpty()) {
            openingShops.forEach(shop -> {
                shop.setStatus(ShopStatus.OPEN);
                shopRepository.save(shop);
            });
        }

        List<Shop> closingShops = shopRepository.findShopsWithCurrentClosingTimeAndDay(Weekday.valueOf(LocalDateTime.now().getDayOfWeek().name()));
        if(closingShops != null && !closingShops.isEmpty()) {
            closingShops.forEach(shop -> {
                shop.setStatus(ShopStatus.CLOSED);
                shopRepository.save(shop);
            });
        }
    }
}
