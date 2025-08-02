package com.coachera.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Request object for registering a device token for push notifications")
public class DeviceTokenRequest {

    @NotBlank(message = "Device token is required")
    @Schema(required = true, example = "fcm_device_token_abc123", description = "The device token used for push notifications")
    private String deviceToken;

    @Schema(example = "android", description = "Platform of the device (e.g., 'ios' or 'android')")
    private String platform;
}
