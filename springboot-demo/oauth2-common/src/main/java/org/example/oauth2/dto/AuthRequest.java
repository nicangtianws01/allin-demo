package org.example.oauth2.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {
    private String clientId;
    private String clientSecret;
    private String code;
}
