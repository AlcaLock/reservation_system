package reservation_system.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import reservation_system.dto.AdminDashboardResponse;
import reservation_system.dto.ResourceStatisticsResponse;
import reservation_system.dto.UpdateUserRoleRequest;
import reservation_system.dto.UpdateUserStatusRequest;
import reservation_system.dto.UserResponse;
import reservation_system.service.AdminDashboardService;
import reservation_system.service.ResourceStatisticsService;
import reservation_system.service.UserService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminDashboardService adminDashboardService;
    private final ResourceStatisticsService resourceStatisticsService;
    private final UserService userService;

    public AdminController(
            AdminDashboardService adminDashboardService,
            ResourceStatisticsService resourceStatisticsService,
            UserService userService) {
        this.adminDashboardService = adminDashboardService;
        this.resourceStatisticsService = resourceStatisticsService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardResponse> dashboard() {
        return ResponseEntity.ok(adminDashboardService.getDashboard());
    }

    @GetMapping("/resources/statistics")
    public ResourceStatisticsResponse resourceStatistics() {
        return resourceStatisticsService.getStatistics();
    }

    @GetMapping("/users")
    public List<UserResponse> findUsers(@RequestParam(required = false) String search) {
        return userService.findAll(search);
    }

    @GetMapping("/users/{id}")
    public UserResponse findUserById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PatchMapping("/users/{id}/role")
    public UserResponse updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRoleRequest request) {
        return userService.updateRole(id, request);
    }

    @PatchMapping("/users/{id}/status")
    public UserResponse updateUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        return userService.updateStatus(id, request);
    }
}
