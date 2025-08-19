package com.tkahng.spring_auth.config;


import com.tkahng.spring_auth.domain.User;
import com.tkahng.spring_auth.service.AuthService;
import com.tkahng.spring_auth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
@Slf4j
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthService authService;
    private final UserService userService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        var type = parameter.getParameterType()
                .equals(User.class);
        log.info("type: {}", type);
        return type;
    }

    @Override
    public Object resolveArgument(
            @NotNull MethodParameter parameter, ModelAndViewContainer mavContainer,
            @NotNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory
    ) {
        var authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            String subject = jwt.getSubject();
            return userService.findUserByEmail(subject)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
        return null;
    }
}
