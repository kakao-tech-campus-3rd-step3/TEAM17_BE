package com.starterpack.auth.login;

import com.starterpack.auth.CustomMemberDetails;
import com.starterpack.exception.BusinessException;
import com.starterpack.exception.ErrorCode;
import com.starterpack.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @Login 어노테이션을 처리하는 Argument Resolver.
 * SecurityContext에서 인증된 사용자 정보를 찾아 Member 객체를 파라미터에 주입합니다.
 */
@Component
@RequiredArgsConstructor
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * 이 Resolver가 지원하는 파라미터인지를 검사합니다.
     * @return 파라미터에 @Login 어노테이션이 있고, 타입이 Member.class이면 true
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class);
        boolean hasMemberType = Member.class.isAssignableFrom(parameter.getParameterType());
        return hasLoginAnnotation && hasMemberType;
    }

    /**
     * supportsParameter가 true를 반환하면 실행됩니다.
     * 실제 파라미터에 주입할 객체를 반환합니다.
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Login loginAnnotation = parameter.getParameterAnnotation(Login.class);

        if (isUnauthenticated(authentication)) {
            if (loginAnnotation == null || loginAnnotation.required()) {
                throw new BusinessException(ErrorCode.INVALID_AUTH_PRINCIPAL);
            } else {
                return null;
            }
        }

        CustomMemberDetails principal = (CustomMemberDetails) authentication.getPrincipal();

        return principal.getMember();
    }

    private boolean isUnauthenticated(Authentication authentication) {
        return authentication == null || !(authentication.getPrincipal() instanceof CustomMemberDetails);
    }

}
