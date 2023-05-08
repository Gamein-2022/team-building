package gamein2.team.kernel.util;

import gamein2.team.kernel.exceptions.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.function.Function;


public class JwtUtils {
    private static final String secret = "some-very-very-ultra-top-secret";

    public static String generateToken(Long userId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + 86400000))
                .signWith(
                        SignatureAlgorithm.HS512,
                        secret
                ).compact();
    }

    public static Boolean isTokenExpired(String token) throws InvalidTokenException {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private static Date getExpirationDateFromToken(String token) throws InvalidTokenException {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private static <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) throws InvalidTokenException {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private static Claims getAllClaimsFromToken(String token) throws InvalidTokenException {
        try {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();

        } catch (MalformedJwtException e) {
            throw new InvalidTokenException("Invalid token!");
        }
    }

    public static String getIdFromToken(String token) throws InvalidTokenException {
        return getClaimFromToken(token, Claims::getSubject);
    }
}
