package com.kushui.kuapicommon.util;

import org.springframework.util.StringUtils;
import io.jsonwebtoken.*;

import java.util.Date;

public class JwtUtil {
    private static long tokenExpiration = 24*60*60*1000;
    private static String tokenSignKey = "kushui";

    public static String createToken(Long userId, String userName) {
        String token = Jwts.builder()
                .setSubject("API-USER")
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .claim("userId", userId)
                .claim("userName", userName)
                .signWith(SignatureAlgorithm.HS512, tokenSignKey)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        return token;
    }

    //这里我自己的理解是，token进行了tokenSignKey的jwt化
    //需要输入SigningKey即tokenSignKey进行解码，然后才能得到userid
    public static Long getUserId(String token) {
        if(StringUtils.isEmpty(token)) return null;
        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        Integer userId = (Integer)claims.get("userId");
        return userId.longValue();
    }
    public static String getUserName(String token) {
        if(StringUtils.isEmpty(token)) return "";
        Jws<Claims> claimsJws
                = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        return (String)claims.get("userName");
    }


}
