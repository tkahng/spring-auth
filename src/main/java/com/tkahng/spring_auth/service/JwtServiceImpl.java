package com.tkahng.spring_auth.service;

import com.tkahng.spring_auth.dto.JwtDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    private final JwtEncoder jwtEncoder;

    public String generateToken(JwtDto dto) {
        Instant now = Instant.now();
        var roles = dto.getRoles();
        var permissions = dto.getPermissions();
        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
                .issuer("your-app")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .subject(dto.getEmail());
        if (roles != null && !roles.isEmpty()) {
            claims.claim("roles", roles);
        }
        if (permissions != null && !permissions.isEmpty()) {
            claims.claim("permissions", permissions);
        }


        return jwtEncoder.encode(
                        JwtEncoderParameters.from(
                                JwsHeader.with(MacAlgorithm.HS256)
                                        .build(), // Explicitly tell Nimbus to use HS256
                                claims.build()
                        )
                )
                .getTokenValue();
    }
}
