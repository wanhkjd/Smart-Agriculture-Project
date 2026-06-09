package com.origin.controller;

import com.origin.common.Result;
import com.origin.entity.WarehouseLocation;
import com.origin.mapper.WarehouseLocationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class WarehouseLocationController {

    private final WarehouseLocationMapper locationMapper;

    private static final Map<String, String> CONDITION_ZONE_MAP = Map.of(
            "常温", "常温区",
            "冷藏", "冷藏区",
            "冷冻", "冷冻区"
    );

    @GetMapping("/available")
    public Result<?> getAvailableLocations(@RequestParam String storageCondition) {
        String zone = CONDITION_ZONE_MAP.get(storageCondition);
        if (zone == null) return Result.success(List.of());
        List<WarehouseLocation> locations = locationMapper.findAvailableByZone(zone);
        return Result.success(locations);
    }

    @GetMapping
    public Result<?> listAll() {
        return Result.success(locationMapper.findAll());
    }
}