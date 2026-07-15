package com.alnoor.backend.dto;

import com.alnoor.backend.security.AppUserDetails;

public record UserDto(String id, String name, String email) {
    public static UserDto from(AppUserDetails principal) {
        return new UserDto(principal.getId(), principal.getName(), principal.getEmail());
    }
}
