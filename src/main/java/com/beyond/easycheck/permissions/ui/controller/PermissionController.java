package com.beyond.easycheck.permissions.ui.controller;

import com.beyond.easycheck.permissions.application.service.PermissionOperationUseCase;
import com.beyond.easycheck.permissions.application.service.PermissionReadUseCase;
import com.beyond.easycheck.permissions.ui.requestbody.PermissionCreateRequest;
import com.beyond.easycheck.permissions.ui.requestbody.PermissionGrantRequest;
import com.beyond.easycheck.permissions.ui.requestbody.PermissionRevokeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.beyond.easycheck.permissions.application.service.PermissionOperationUseCase.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/permissions")
@Tag(name = "Permission", description = "권한 관리 API")
public class PermissionController {

    private final PermissionOperationUseCase permissionOperationUseCase;

    private final PermissionReadUseCase permissionReadUseCase;

    @PostMapping("")
    @Operation(summary = "권한 생성 API")
    public ResponseEntity<Void> createPermission(
            @RequestBody @Validated PermissionCreateRequest request
    ) {
        PermissionCreateCommand command = new PermissionCreateCommand(request.name(), request.description());

        permissionOperationUseCase.createPermission(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @PatchMapping("/grant")
    @Operation(summary = "권한 부여 API")
    public ResponseEntity<Void> grantPermission(
            @AuthenticationPrincipal Long grantorUserId,
            @RequestBody @Validated PermissionGrantRequest request
    ) {

        PermissionGrantCommand command = new PermissionGrantCommand(
                request.granteeUserId(),
                grantorUserId,
                request.permissionId()
        );

        permissionOperationUseCase.grantPermission(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @PatchMapping("/revoke")
    @Operation(summary = "권한 회수 API")
    public ResponseEntity<Void> revokePermission(
            @RequestBody @Validated PermissionRevokeRequest request
    ) {

        PermissionRevokeCommand command = new PermissionRevokeCommand(
                request.targetUserId(),
                request.permissionId()
        );

        permissionOperationUseCase.revokePermission(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @DeleteMapping("/{permissionId}")
    @Operation(summary = "권한 삭제 API")
    public ResponseEntity<Void> revokePermission(@PathVariable Long permissionId) {

        permissionOperationUseCase.deletePermission(permissionId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }
}
