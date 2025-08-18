package com.tkahng.spring_auth.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
@Builder
public class EmailDto {
    private String subject;
    private String recipient;
    private String body;
}
