package com.tkahng.spring_auth.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
@Builder
public class CreateTokenDto {
    private String identifier;
    private String type;
    private String value;
    private int ttl;
}
