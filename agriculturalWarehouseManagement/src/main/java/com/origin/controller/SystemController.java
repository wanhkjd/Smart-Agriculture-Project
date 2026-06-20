package com.origin.controller;

import com.origin.common.Result;
import com.origin.pattern.proxy.PermissionProxy;
import com.origin.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
public class SystemController {
    private final ReportService reportService;
    private final PermissionProxy permissionProxy;

    @GetMapping("/logs")
    public Result<?> logs() {
        return Result.success(reportService.getSystemLogs());
    }

    @GetMapping("/config")
    public Result<?> config() {
        return Result.success(reportService.getConfig());
    }

    @PutMapping("/config")
    public Result<?> updateConfig(@RequestParam String key, @RequestParam String value) {
        permissionProxy.check("配置管理");
        reportService.updateConfig(key, value);
        return Result.success(Map.of(key, value));
    }

    @GetMapping("/check-permission")
    public Result<?> checkPermission(@RequestParam String operation, @RequestParam String role) {
        return Result.success(Map.of("result", reportService.checkPermission(operation, role)));
    }
}
