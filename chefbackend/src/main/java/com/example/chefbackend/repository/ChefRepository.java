package com.example.chefbackend.repository;

import com.example.chefbackend.dto.ChefLeaderboardDto;
import com.example.chefbackend.dto.ChefSummaryDto;
import com.example.chefbackend.model.Chef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChefRepository extends JpaRepository<Chef, Long> {

    @Query("SELECT new com.example.chefbackend.dto.ChefSummaryDto(c.id, c.name, c.profilePicture, c.experience, c.foodOrigin, c.expertise, c.basePrice) " +
            "FROM Chef c WHERE " +
            "(:foodOrigin IS NULL OR c.foodOrigin = :foodOrigin) AND " +
            "(:expertise IS NULL OR c.expertise = :expertise) AND " +
            "(:minExperience IS NULL OR c.experience >= :minExperience) AND " +
            "(:maxExperience IS NULL OR c.experience <= :maxExperience) AND " +
            "(:minBasePrice IS NULL OR c.basePrice >= :minBasePrice) AND " +
            "(:maxBasePrice IS NULL OR c.basePrice <= :maxBasePrice)")
    List<ChefSummaryDto> findChefsByFilters(
            @Param("foodOrigin") String foodOrigin,
            @Param("expertise") String expertise,
            @Param("minExperience") Integer minExperience,
            @Param("maxExperience") Integer maxExperience,
            @Param("minBasePrice") Integer minBasePrice,
            @Param("maxBasePrice") Integer maxBasePrice
    );

    @Query("SELECT new com.example.chefbackend.dto.ChefSummaryDto(c.id, c.name, c.profilePicture, c.experience, c.foodOrigin, c.expertise, c.basePrice) " +
            "FROM Chef c ORDER BY c.viewCount DESC")
    List<ChefSummaryDto> findAllByOrderByViewCountDesc();

    @Query("SELECT new com.example.chefbackend.dto.ChefLeaderboardDto(c.id, c.name, c.profilePicture, c.viewCount) " +
            "FROM Chef c ORDER BY c.viewCount DESC LIMIT 5")
    List<ChefLeaderboardDto> findTop5ByViewCount();
}
