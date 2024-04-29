package com.serch.server.services.shop.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.shop.ShopStatus;
import com.serch.server.enums.subscription.PlanStatus;
import com.serch.server.exceptions.others.ShopException;
import com.serch.server.mappers.ShopMapper;
import com.serch.server.models.auth.User;
import com.serch.server.models.shop.Shop;
import com.serch.server.models.shop.ShopService;
import com.serch.server.repositories.shop.ShopRepository;
import com.serch.server.repositories.shop.ShopServiceRepository;
import com.serch.server.services.shop.requests.AddShopServiceRequest;
import com.serch.server.services.shop.requests.CreateShopRequest;
import com.serch.server.services.shop.requests.RemoveShopServiceRequest;
import com.serch.server.services.shop.requests.UpdateShopRequest;
import com.serch.server.services.shop.responses.SearchShopResponse;
import com.serch.server.services.shop.responses.ShopResponse;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Implementation of {@link ShopServices} interface, providing methods for shop management operations.
 *
 * @see UserUtil
 * @see ShopRepository
 * @see ShopServiceRepository
 */
@Service
@RequiredArgsConstructor
public class ShopImplementation implements ShopServices {
    private final UserUtil userUtil;
    private final ShopRepository shopRepository;
    private final ShopServiceRepository shopServiceRepository;

    @Override
    public ApiResponse<List<ShopResponse>> createShop(CreateShopRequest request) {
        User user = userUtil.getUser();

        Shop shop = ShopMapper.INSTANCE.shop(request);
        shop.setUser(user);
        Shop savedShop = shopRepository.save(shop);

        if(!request.getServices().isEmpty()) {
            request.getServices()
                    .stream()
                    .map(s -> {
                        ShopService service = new ShopService();
                        service.setShop(savedShop);
                        service.setService(s);
                        return service;
                    }).forEach(shopServiceRepository::save);
        }

        return getShopResponse(user);
    }

    private ApiResponse<List<ShopResponse>> getShopResponse(User user) {
        return new ApiResponse<>(
                "Success",
                shopRepository.findByUser_Id(user.getId()).isEmpty() ? List.of() : shopRepository.findByUser_Id(user.getId())
                        .stream()
                        .sorted(Comparator.comparing(Shop::getCreatedAt))
                        .map(s -> {
                            ShopResponse response = ShopMapper.INSTANCE.shop(s);
                            response.setServices(s.getServices()
                                    .stream()
                                    .map(ShopMapper.INSTANCE::service)
                                    .toList()
                            );
                            return response;
                        })
                        .toList(),
                HttpStatus.OK
        );
    }

    @Override
    public ApiResponse<List<ShopResponse>> updateShop(UpdateShopRequest request) {
        Shop shop = shopRepository.findById(request.getShopId())
                .orElseThrow(() -> new ShopException("Shop not found"));

        if(shop.isUser(userUtil.getUser().getId())) {
            if(shop.getStatus() == ShopStatus.SUSPENDED) {
                throw new ShopException("Cannot update a suspended shop. Contact support");
            } else {
                if(!shop.getName().equalsIgnoreCase(request.getName())) {
                    shop.setName(request.getName());
                }
                if(!shop.getAddress().equalsIgnoreCase(request.getAddress())) {
                    shop.setAddress(request.getAddress());
                }
                if(!shop.getPlace().equalsIgnoreCase(request.getPlace())) {
                    shop.setPlace(request.getPlace());
                }
                if(!shop.getPhoneNumber().equalsIgnoreCase(request.getPhoneNumber())) {
                    shop.setPhoneNumber(request.getPhoneNumber());
                }
                if(!shop.getLatitude().equals(request.getLatitude())) {
                    shop.setLatitude(request.getLatitude());
                }
                if(!shop.getLongitude().equals(request.getLongitude())) {
                    shop.setLongitude(request.getLongitude());
                }
                if(shop.getCategory() != request.getCategory()) {
                    shop.setCategory(request.getCategory());
                }
                shop.setUpdatedAt(LocalDateTime.now());
                shopRepository.save(shop);
                return fetchShops();
            }
        } else {
            throw new ShopException("Shop does not belong to user");
        }
    }

    @Override
    public ApiResponse<List<ShopResponse>> fetchShops() {
        return getShopResponse(userUtil.getUser());
    }

    @Override
    public ApiResponse<String> removeService(RemoveShopServiceRequest request) {
        ShopService shop = shopServiceRepository.findByIdAndShop_Id(request.getId(), request.getShop())
                .orElseThrow(() -> new ShopException("Shop not found"));

        if(shop.getShop().isUser(userUtil.getUser().getId())) {
            if(shop.getShop().getStatus() == ShopStatus.SUSPENDED) {
                throw new ShopException("Cannot update a suspended shop. Contact support");
            }

            shopServiceRepository.delete(shop);
            return new ApiResponse<>("Service removed successfully", HttpStatus.OK);
        } else {
            throw new ShopException("Shop does not belong to user");
        }
    }

    @Override
    public ApiResponse<String> addService(AddShopServiceRequest request) {
        Shop shop = shopRepository.findById(request.getShop())
                .orElseThrow(() -> new ShopException("Shop not found"));
        if(shop.isUser(userUtil.getUser().getId())) {
            if(shop.getStatus() == ShopStatus.SUSPENDED) {
                throw new ShopException("Cannot update a suspended shop. Contact support");
            }

            ShopService service = new ShopService();
            service.setShop(shop);
            service.setService(request.getService());
            shopServiceRepository.save(service);

            return new ApiResponse<>("Service successfully added", HttpStatus.OK);
        } else {
            throw new ShopException("Shop does not belong to you");
        }
    }

    @Override
    public ApiResponse<String> changeStatus(String shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopException("Shop not found"));
        if(shop.isUser(userUtil.getUser().getId())) {
            if(shop.getStatus() == ShopStatus.SUSPENDED) {
                throw new ShopException("Cannot update a suspended shop. Contact support");
            }

            if(shop.getStatus() == ShopStatus.OPEN) {
                shop.setStatus(ShopStatus.CLOSED);
                shop.setUpdatedAt(LocalDateTime.now());
                shopRepository.save(shop);

                return new ApiResponse<>(
                        "%s is now closed for business".formatted(shop.getName()),
                        HttpStatus.OK
                );
            } else {
                shop.setStatus(ShopStatus.OPEN);
                shop.setUpdatedAt(LocalDateTime.now());
                shopRepository.save(shop);

                return new ApiResponse<>(
                        "%s is now open for business".formatted(shop.getName()),
                        HttpStatus.OK
                );
            }
        } else {
            throw new ShopException("Shop does not belong to user");
        }
    }

    @Override
    public ApiResponse<String> markAllOpen() {
        List<Shop> shops = shopRepository.findByUser_Id(userUtil.getUser().getId());
        if(shops.isEmpty()) {
            throw new ShopException("You have no shops");
        } else {
            shops.stream().filter(shop -> shop.getStatus() != ShopStatus.SUSPENDED).peek(shop -> {
                shop.setStatus(ShopStatus.OPEN);
                shop.setUpdatedAt(LocalDateTime.now());
            }).forEach(shopRepository::save);
            return new ApiResponse<>("Shops are now open for business", HttpStatus.OK);
        }
    }

    @Override
    public ApiResponse<String> markAllClosed() {
        List<Shop> shops = shopRepository.findByUser_Id(userUtil.getUser().getId());
        if(shops.isEmpty()) {
            throw new ShopException("You have no shops");
        } else {
            shops.stream().filter(shop -> shop.getStatus() != ShopStatus.SUSPENDED).peek(shop -> {
                shop.setStatus(ShopStatus.CLOSED);
                shop.setUpdatedAt(LocalDateTime.now());
            }).forEach(shopRepository::save);
            return new ApiResponse<>("Shops are now open for business", HttpStatus.OK);
        }
    }

    @Override
    public ApiResponse<List<SearchShopResponse>> drive(String query, String category, Double longitude, Double latitude, Double radius) {
        List<Shop> listOfShops = new ArrayList<>();
        if(query != null && !query.isEmpty()) {
            listOfShops = shopRepository.findByServiceAndLocation(
                    latitude, longitude, query, radius, PlanStatus.ACTIVE.name()
            );
        } else {
            listOfShops = shopRepository.findByCategoryAndLocation(
                    latitude, longitude, category, radius, PlanStatus.ACTIVE.name()
            );
        }

        return new ApiResponse<>(
                listOfShops.stream()
                        .map(shop -> {
                            SearchShopResponse response = ShopMapper.INSTANCE.search(shop);

                            double distance = HelperUtil.getDistance(latitude, longitude, shop.getLatitude(), shop.getLongitude());
                            response.setDistance(distance + " km");
                            return response;
                        })
                        .toList()
        );
    }
}
