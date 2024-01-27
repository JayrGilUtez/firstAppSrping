package mx.edu.utez.firstapp.security.jwt;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.security.SignatureException;
import java.util.Date;

@Service
public class JwtProvider {
    private final Logger LOGGER = LoggerFactory.getLogger(JwtProvider.class);
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private long expiration;
    private final String HEADER = "Authentication";
    private final String TOKEN_TYPE = "Bearer ";

    public String generateToken(Authentication auth){
        UserDetails user = (UserDetails) auth.getPrincipal();
        // claims -> Payload -> Pueden poner cualquier propiedad con su respectivo valor
        Claims claims = Jwts.claims().setSubject(user.getUsername());
        claims.put("roles", user.getAuthorities());
        Date tokenCreateTime = new Date();
        Date tokenValidity = new Date(
                tokenCreateTime.getTime() + expiration * 1000L
        );
        return Jwts.builder().setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(tokenValidity)
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    private Key getSignKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    private Claims parseClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build().parseClaimsJws(token)
                .getBody();
    }
    public Claims resolveClaims(HttpServletRequest req){
        try{
            String token = resolveToken(req);
            if (token!=null)
                return parseClaims(token);
            return null;
        } catch (ExpiredJwtException e){
            req.setAttribute("expired", e.getMessage());
            throw e;
        } catch (Exception e) {
            req.setAttribute("invalid", e.getMessage());
            throw e;
        }
    }

    public String resolveToken(HttpServletRequest req){
        String bearerToken = req.getHeader(HEADER);
        if (bearerToken!=null && bearerToken.startsWith(TOKEN_TYPE))
            //.substring(TOKEN_TYPE.length());
            return bearerToken.replace(TOKEN_TYPE, "");
        return null;
    }

    public boolean validateClaims(Claims claims, String token){
        try {
            parseClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (MalformedJwtException e) {
            LOGGER.error("MalformedToken");
        } catch (UnsupportedJwtException e) {
            LOGGER.error("UnsupportedToken");
        } catch (ExpiredJwtException e) {
            LOGGER.error("TokenExpired");
        } catch (IllegalArgumentException e){
            LOGGER.error("BlankToken");
        } catch (Exception e){
            LOGGER.error(e.getMessage());
        }
        return false;
    }
}