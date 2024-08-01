package com.serch.server.services.shop.services;

import com.serch.server.bases.ApiResponse;
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
import com.serch.server.services.shop.requests.CreateShopRequest;
import com.serch.server.services.shop.requests.UpdateShopRequest;
import com.serch.server.services.shop.requests.ShopWeekdayRequest;
import com.serch.server.services.shop.responses.SearchShopResponse;
import com.serch.server.services.shop.responses.ShopResponse;
import com.serch.server.services.shop.responses.ShopServiceResponse;
import com.serch.server.services.shop.responses.ShopWeekdayResponse;
import com.serch.server.core.storage.core.StorageService;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Implementation of {@link ShopService} interface, providing methods for shop management operations.
 *
 * @see UserUtil
 * @see ShopRepository
 * @see ShopServiceRepository
 */
@Service
@RequiredArgsConstructor
public class ShopImplementation implements ShopService {
    private final StorageService storageService;
    private final UserUtil userUtil;
    private final ShopRepository shopRepository;
    private final ShopServiceRepository shopServiceRepository;
    private final ShopWeekdayRepository shopWeekdayRepository;

    @Value("${application.map.search-radius}")
    private String MAP_SEARCH_RADIUS;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mma");

    private static LocalTime toTime(String timeString) {
        return LocalTime.parse(timeString.toUpperCase(), TIME_FORMATTER);
    }

    private static String toString(LocalTime time) {
        return time.format(TIME_FORMATTER);
    }

    private ShopResponse response(Shop shop) {
        ShopResponse response = ShopMapper.INSTANCE.shop(shop);
        response.setOpen(shop.getStatus() == ShopStatus.OPEN);
        response.setCategory(shop.getCategory().getType());
        response.setImage(shop.getCategory().getImage());
        if(shop.getWeekdays() != null && !shop.getWeekdays().isEmpty()) {
            response.setWeekdays(shop.getWeekdays().stream().map(this::response).toList());
            DayOfWeek currentDay = LocalDateTime.now().getDayOfWeek();
            shopWeekdayRepository.findByDayAndShop_Id(Weekday.valueOf(currentDay.name()), shop.getId())
                    .ifPresent(weekday -> response.setCurrent(response(weekday)));
        }
        if(shop.getServices() != null && !shop.getServices().isEmpty()) {
            response.setServices(shop.getServices().stream().map(this::response).toList());
        }
        return response;
    }

    private ShopServiceResponse response(ShopSpecialty service) {
        return ShopServiceResponse.builder()
                .service(service.getService())
                .id(service.getId())
                .build();
    }

    private ShopWeekdayResponse response(ShopWeekday weekday) {
        return ShopWeekdayResponse.builder()
                .day(weekday.getDay().getDay())
                .closing(toString(weekday.getClosing()))
                .opening(toString(weekday.getOpening()))
                .id(weekday.getId())
                .open(weekday.getShop().getStatus() == ShopStatus.OPEN)
                .build();
    }

    private ApiResponse<ShopResponse> getUpdatedShopResponse(String shopId) {
        Shop updatedShop = shopRepository.findByIdAndUser_Id(shopId, userUtil.getUser().getId())
                .orElseThrow(() -> new ShopException("Shop not found"));
        return new ApiResponse<>(response(updatedShop));
    }

    @Override
    public ApiResponse<List<ShopResponse>> createShop(CreateShopRequest request) {
        if(HelperUtil.isUploadEmpty(request.getUpload())) {
            throw new ShopException("Shop logo or image is needed");
        } else {
            String logo = storageService.upload(request.getUpload(), "shops");
            Shop newShop = ShopMapper.INSTANCE.shop(request);
            newShop.setUser(userUtil.getUser());
            newShop.setLogo(logo);
            Shop shop = shopRepository.save(newShop);
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
                            weekday.setClosing(toTime(s.getClosing()));
                            weekday.setOpening(toTime(s.getOpening()));
                            weekday.setDay(s.getDay());
                            return weekday;
                        }).forEach(shopWeekdayRepository::save);
            }
        }

        return fetchShops();
    }

    @Override
    public ApiResponse<ShopResponse> createWeekday(String shopId, ShopWeekdayRequest request) {
        Shop shop = shopRepository.findByIdAndUser_Id(shopId, userUtil.getUser().getId())
                .orElseThrow(() -> new ShopException("Shop not found"));
        ShopWeekday weekday = new ShopWeekday();
        weekday.setShop(shop);
        weekday.setClosing(toTime(request.getClosing()));
        weekday.setOpening(toTime(request.getOpening()));
        weekday.setDay(request.getDay());
        shopWeekdayRepository.save(weekday);

        return getUpdatedShopResponse(shopId);
    }

    @Override
    public ApiResponse<ShopResponse> createService(String shopId, String service) {
        Shop shop = shopRepository.findByIdAndUser_Id(shopId, userUtil.getUser().getId())
                .orElseThrow(() -> new ShopException("Shop not found"));
        ShopSpecialty newService = new ShopSpecialty();
        newService.setShop(shop);
        newService.setService(service);
        shopServiceRepository.save(newService);

        return getUpdatedShopResponse(shopId);
    }

    @Override
    public ApiResponse<ShopResponse> updateShop(UpdateShopRequest request) {
        Shop shop = shopRepository.findByIdAndUser_Id(request.getShop(), userUtil.getUser().getId())
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
        shop.setUpdatedAt(LocalDateTime.now());
        shopRepository.save(shop);
        return getUpdatedShopResponse(request.getShop());
    }

    @Override
    public ApiResponse<ShopResponse> updateWeekday(Long id, String shopId, ShopWeekdayRequest request) {
        ShopWeekday weekday = shopWeekdayRepository.findByIdAndShop_Id(id, shopId)
                .orElseThrow(() -> new ShopException("Weekday not found"));
        if(!weekday.getClosing().equals(toTime(request.getClosing()))) {
            weekday.setClosing(toTime(request.getClosing()));
        }
        if(!weekday.getOpening().equals(toTime(request.getOpening()))) {
            weekday.setOpening(toTime(request.getOpening()));
        }
        weekday.setUpdatedAt(LocalDateTime.now());
        shopWeekdayRepository.save(weekday);
        return getUpdatedShopResponse(shopId);
    }

    @Override
    public ApiResponse<ShopResponse> updateService(Long id, String shopId, String service) {
        ShopSpecialty shopSpecialty = shopServiceRepository.findByIdAndShop_Id(id, shopId)
                .orElseThrow(() -> new ShopException("Service not found"));
        if(!shopSpecialty.getService().equalsIgnoreCase(service)) {
            shopSpecialty.setService(service);
            shopSpecialty.setUpdatedAt(LocalDateTime.now());
            shopServiceRepository.save(shopSpecialty);
        }
        return getUpdatedShopResponse(shopId);
    }

    @Override
    public ApiResponse<List<ShopResponse>> fetchShops() {
        List<Shop> shops = shopRepository.findByUser_Id(userUtil.getUser().getId());
        List<ShopResponse> list = shops == null || shops.isEmpty() ? List.of() : shops
                .stream()
                .map(this::response)
                .toList();
        return new ApiResponse<>(list);
    }

    @Override
    public ApiResponse<ShopResponse> removeService(Long id, String shopId) {
        shopServiceRepository.findByIdAndShop_Id(id, shopId).ifPresent(shopServiceRepository::delete);
        return getUpdatedShopResponse(shopId);
    }

    @Override
    public ApiResponse<List<ShopResponse>> removeShop(String shopId) {
        shopRepository.findByIdAndUser_Id(shopId, userUtil.getUser().getId()).ifPresent(shopRepository::delete);
        return fetchShops();
    }

    @Override
    public ApiResponse<ShopResponse> removeWeekday(Long id, String shopId) {
        shopWeekdayRepository.findByIdAndShop_Id(id, shopId).ifPresent(shopWeekdayRepository::delete);
        return getUpdatedShopResponse(shopId);
    }

    @Override
    public ApiResponse<ShopResponse> changeStatus(String shopId, ShopStatus status) {
        Shop shop = shopRepository.findByIdAndUser_Id(shopId, userUtil.getUser().getId())
                .orElseThrow(() -> new ShopException("Shop not found"));
        shop.setStatus(status);
        shop.setUpdatedAt(LocalDateTime.now());
        shopRepository.save(shop);
        return getUpdatedShopResponse(shopId);
    }

    @Override
    public ApiResponse<List<ShopResponse>> changeAllStatus() {
        List<Shop> shops = shopRepository.findByUser_Id(userUtil.getUser().getId());
        if(shops != null && !shops.isEmpty()) {
            shops.stream().filter(shop -> shop.getStatus() != ShopStatus.SUSPENDED).peek(shop -> {
                if(shop.getStatus() == ShopStatus.OPEN) {
                    shop.setStatus(ShopStatus.CLOSED);
                } else if(shop.getStatus() == ShopStatus.CLOSED) {
                    shop.setStatus(ShopStatus.OPEN);
                }
                shop.setUpdatedAt(LocalDateTime.now());
            }).forEach(shopRepository::save);
            return fetchShops();
        } else {
            throw new ShopException("You have no shops");
        }
    }

    @Override
    public ApiResponse<List<SearchShopResponse>> drive(String query, String category, Double longitude, Double latitude, Double radius) {
        double searchRadius = radius == null ? Double.parseDouble(MAP_SEARCH_RADIUS) : radius;
        List<SearchShopResponse> list = list(query, category, longitude, latitude, searchRadius);
        return new ApiResponse<>(list);
    }

    @Override
    public List<SearchShopResponse> list(String query, String category, Double longitude, Double latitude, Double radius) {
        if((query == null || query.isEmpty()) && (category == null || category.isEmpty()) ) {
            throw new ShopException("Query and category cannot be null or empty");
        }

        List<Shop> listOfShops;
        if(query != null && !query.isEmpty()) {
            listOfShops = shopRepository.findByServiceAndLocation(latitude, longitude, query.toLowerCase(), radius);
        } else {
            listOfShops = shopRepository.findByServiceAndLocation(latitude, longitude, category.toLowerCase(), radius);
        }

        if(listOfShops != null && !listOfShops.isEmpty()) {
            return listOfShops.stream()
                    .map(shop -> {
                        SearchShopResponse response = new SearchShopResponse();
                        double distance = HelperUtil.getDistance(latitude, longitude, shop.getLatitude(), shop.getLongitude());
                        response.setDistanceInKm(distance + " km");
                        response.setDistance(distance);
                        response.setShop(response(shop));
                        response.setUser(shop.getUser().getId());
                        return response;
                    })
                    .toList();
        }
        return List.of();
    }

    @Override
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
