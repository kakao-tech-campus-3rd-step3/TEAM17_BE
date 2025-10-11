package com.starterpack.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Setter
public class PlainTextCookieCsrfTokenRepository implements CsrfTokenRepository {

    private static final String DEFAULT_COOKIE_NAME = "XSRF-TOKEN";
    private static final String DEFAULT_HEADER_NAME = "X-XSRF-TOKEN";
    private static final String DEFAULT_PARAMETER_NAME = "_csrf";

    private String cookieName = DEFAULT_COOKIE_NAME;
    private String headerName = DEFAULT_HEADER_NAME;
    private String parameterName = DEFAULT_PARAMETER_NAME;
    private boolean secure = true;
    private String cookiePath = "/";
    private String sameSite = "None";

    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        return new DefaultCsrfToken(this.headerName, this.parameterName,
                createNewToken());
    }

    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request,
            HttpServletResponse response) {

        if (token == null) {
            return;
        }

        String tokenValue = token.getToken();

        Cookie cookie = new Cookie(this.cookieName, tokenValue);
        cookie.setPath(this.cookiePath);
        cookie.setSecure(this.secure);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(-1);

        // SameSite 속성 설정
        if (StringUtils.hasText(this.sameSite)) {
            cookie.setAttribute("SameSite", this.sameSite);
        }

        response.addCookie(cookie);
        response.setHeader(this.headerName, tokenValue);

    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (this.cookieName.equals(cookie.getName())) {
                    String token = cookie.getValue();
                    if (StringUtils.hasText(token)) {
                        return new DefaultCsrfToken(this.headerName,
                                this.parameterName, token);
                    }
                }
            }
        }
        return null;
    }

    private String createNewToken() {
        return UUID.randomUUID().toString();
    }
}
