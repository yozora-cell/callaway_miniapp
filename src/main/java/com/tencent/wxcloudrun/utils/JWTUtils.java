package com.tencent.wxcloudrun.utils;

import com.alibaba.fastjson2.JSON;
import com.tencent.wxcloudrun.entity.dto.UserInfo;
import com.tencent.wxcloudrun.exception.ServiceException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author yozora
 * Description JSON Web Token
 **/
@Slf4j
public class JWTUtils {

    private static final String SECRET_KEY = "MHcCAQEEIKTna+5lOYrjhhp3xth58Ef3WosBb/haSpjj/VvQszNaoAoGCCqGSM49AwEHoUQDQgAE2DhX3HIW9uv8IOX40PpKDw2945ZVfUMif5Q6/Sj9doreq2kLcqVwomz8nKFKXxRtEUCR6zdbyl9xvEuBRmO4TQ==";

    private JWTUtils() throws ClassNotFoundException {
        throw new ClassNotFoundException("can not instantiate JWTUtils.");
    }

    public static String createToken(Map<String, Object> claims, long ttlMillis) {
        long tokenExpireSeconds = 900L;
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        JwtBuilder builder = Jwts.builder().setIssuedAt(now).setClaims(claims).signWith(signatureAlgorithm, SECRET_KEY);
        long expMillis;
        if (ttlMillis > 0L) {
            expMillis = nowMillis + ttlMillis;
        } else {
            expMillis = nowMillis + tokenExpireSeconds * 1000L;
        }

        Date exp = new Date(expMillis);
        builder.setExpiration(exp);
        String token = builder.compact();

        return Base64Utils.encode(token.getBytes(StandardCharsets.UTF_8)).replaceAll("[\r\n]", "");
    }

    /**
     * create user token
     *
     * @param userInfo  user info
     * @param ttlMillis expire time
     * @return java.lang.String
     * @author yozora
     */
    public static String createToken(UserInfo userInfo, long ttlMillis) {
        String token = JWTUtils.createToken(ttlMillis * 1000);
        CaffeineCacheUtil.put(token, userInfo);
        return token;
    }

    public static UserInfo getUser() throws ServiceException {
        HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
        String token = request != null ? request.getHeader(HttpHeaders.AUTHORIZATION) : null;
        assert token != null;
        token = token.replace("Bearer ", "");
        Object o = CaffeineCacheUtil.get(token);
        if (o == null) {
            throw new ServiceException(HttpStatus.UNAUTHORIZED.getReasonPhrase(), HttpStatus.UNAUTHORIZED.value());
        }
        return (UserInfo) o;
    }

    /**
     * create timestamp token
     *
     * @return java.lang.String
     * @author yozora
     */
    public static String createToken(long ttlMillis) {
        Map<String, Object> claims = new HashMap<>(2);
        claims.put("currentTimestamp", System.currentTimeMillis());
        return createToken(claims, ttlMillis);
    }

    /**
     * get token from request header
     *
     * @return java.lang.String
     * @author yozora
     */
    public static String getToken() {
        HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
        return request != null ? request.getHeader(HttpHeaders.AUTHORIZATION) : null;
    }

    public static Claims getClaimsFromToken(String token) throws AuthenticationException {
        try {
            return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(Arrays.toString(Base64.getDecoder().decode(token.getBytes(StandardCharsets.UTF_8)))).getBody();
        } catch (Exception e) {
            throw new AuthenticationException();
        }
    }

    /**
     * validate token
     *
     * @param token token
     * @return java.lang.Boolean
     * @author yozora
     */
    @Deprecated
    public static Boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            Date now = new Date();
            return expiration.before(now);
        } catch (Exception e) {
            log.error("Token expired!\n{}", e.getMessage());
            return true;
        }
    }

    /**
     * refresh token
     *
     * @param token     token
     * @param ttlMillis expire time
     * @return java.lang.String
     * @author yozora
     */
    @Deprecated
    public static String refreshToken(String token, long ttlMillis) {
        String refreshedToken;
        try {
            Claims claims = getClaimsFromToken(token);
            claims.put("refresh", new Date());
            refreshedToken = createToken(claims, ttlMillis);
        } catch (Exception var5) {
            refreshedToken = null;
        }

        return refreshedToken;
    }

    /**
     * description: generate UUID
     *
     * @return java.lang.String
     * @author yozora
     **/
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

}
