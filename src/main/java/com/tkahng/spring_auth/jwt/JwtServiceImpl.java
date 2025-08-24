package com.tkahng.spring_auth.jwt;

import com.tkahng.spring_auth.dto.JwtDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    private final JwtEncoder jwtEncoder;

    public String generateToken(JwtDto dto) {
        Instant now = Instant.now();
        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
                .issuer("your-app")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .subject(dto.getUserId()
                        .toString())
                .claim("user_id", dto.getUserId())
                .claim(StandardClaimNames.EMAIL, dto.getEmail());

        ArrayList<String> authorities = new ArrayList<>();
        var roles = dto.getRoles();
        if (roles != null && !roles.isEmpty()) {
            authorities.addAll(roles.stream()
                    .map(s -> "ROLE_" + s)
                    .toList());
            claims.claim("roles", roles);
        }
        var permissions = dto.getPermissions();
        if (permissions != null && !permissions.isEmpty()) {
            authorities.addAll(permissions);
            claims.claim("permissions", permissions);
        }
        if (dto.getEmailVerifiedAt() != null) {
            authorities.add("email_verified");
            claims.claim(
                    "email_verified_at", dto.getEmailVerifiedAt()
                            .toString()
            );
        }
        if (!authorities.isEmpty()) {
            claims.claim("authorities", authorities);
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
