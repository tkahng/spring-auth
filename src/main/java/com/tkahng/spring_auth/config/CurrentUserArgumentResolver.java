package com.tkahng.spring_auth.config;


import com.tkahng.spring_auth.annotation.CurrentUser;
import com.tkahng.spring_auth.domain.CustomUserDetail;
import org.springframework.core.MethodParameter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(CurrentUser.class) != null
                && CustomUserDetail.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetail user) {
            return user;
        }
        throw new AccessDeniedException("Not authenticated");
    }
}
