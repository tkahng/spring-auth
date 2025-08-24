package com.tkahng.spring_auth.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTokenDto {
    private String identifier;
    private String type;
    private String value;
    private int ttl;
}
