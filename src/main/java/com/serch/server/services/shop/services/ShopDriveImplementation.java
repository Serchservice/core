package com.serch.server.services.shop.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.others.ShopException;
import com.serch.server.mappers.ShopMapper;
import com.serch.server.models.shop.Shop;
import com.serch.server.models.shop.ShopDrive;
import com.serch.server.repositories.shop.ShopDriveRepository;
import com.serch.server.repositories.shop.ShopRepository;
import com.serch.server.services.rating.requests.RatingCalculation;
import com.serch.server.services.rating.services.RatingCalculationService;
import com.serch.server.services.shop.requests.ShopDriveRequest;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopDriveImplementation implements ShopDriveService {
    private final ShopRepository shopRepository;
    private final UserUtil userUtil;
    private final RatingCalculationService calculationService;
    private final ShopDriveRepository shopDriveRepository;

    @Override
    public ApiResponse<String> drive(ShopDriveRequest request) {
        Shop shop = shopRepository.findById(request.getShopId()).orElseThrow(() -> new ShopException("Shop not found"));

        ShopDrive drive = ShopMapper.INSTANCE.drive(request);
        drive.setShop(shop);
        drive.setUser(userUtil.getUser());
        shopDriveRepository.save(drive);

        return new ApiResponse<>("Success", HttpStatus.OK);
    }

    @Override
    public ApiResponse<String> rate(Long id, Double rating) {
        ShopDrive drive = shopDriveRepository.findById(id).orElseThrow(() -> new ShopException("Drive not found"));

        if(drive.getUser().isUser(userUtil.getUser().getId())) {
            drive.setRating(rating);
            drive.setUpdatedAt(TimeUtil.now());
            shopDriveRepository.save(drive);

            List<RatingCalculation> ratings = shopDriveRepository.findByShop_Id(drive.getShop().getId())
                    .stream()
                    .filter(shop -> shop.getRating() != null)
                    .map(ShopMapper.INSTANCE::calculation).toList();
            drive.getShop().setRating(calculationService.getUpdatedRating(ratings, drive.getShop().getRating()));
            drive.getShop().setUpdatedAt(TimeUtil.now());
            shopRepository.save(drive.getShop());

            return new ApiResponse<>("Rating saved", HttpStatus.OK);
        } else {
            throw new ShopException("Access denied");
        }
    }
}
