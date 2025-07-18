package de.ait_tr.g_40_1_shop.security.sec_service;

import de.ait_tr.g_40_1_shop.domain.entity.User;
import de.ait_tr.g_40_1_shop.security.sec_dto.TokenResponseDto;
import de.ait_tr.g_40_1_shop.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.security.auth.message.AuthException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    private UserService userService;
    private TokenService tokenService;
    private Map<String,String> refreshStorage;
    private BCryptPasswordEncoder passwordEncoder;

    public AuthService(BCryptPasswordEncoder passwordEncoder, UserService userService, TokenService tokenService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.tokenService = tokenService;
        this.refreshStorage = new HashMap<>();
    }


    public TokenResponseDto login(User inboundUser) throws AuthException{
        String username = inboundUser.getUsername();
        User foundUser = (User) userService.loadUserByUsername(username);

        if (passwordEncoder.matches(inboundUser.getPassword(),foundUser.getPassword())){

            String accessToken = tokenService.generateAccessToken(foundUser);
            String refreshToken = tokenService.generateRefreshToken(foundUser);
            refreshStorage.put(username,refreshToken);
            return new TokenResponseDto(accessToken,refreshToken);
        }else {
            throw new AuthException("password is incorrect");
        }
    }
        public TokenResponseDto getNewAccessToken(String inboundRefreshToken){
            Claims refrashClaims = tokenService.getRefreshClaims(inboundRefreshToken);
            String username =refrashClaims.getSubject();
            String saveRefreshToken = refreshStorage.get(username);
            if (saveRefreshToken!=null&& saveRefreshToken.equals(inboundRefreshToken)){
             User user   = (User) userService.loadUserByUsername(username);
             String accesToken = tokenService.generateAccessToken(user);
             return new TokenResponseDto(accesToken,null);
            }else {
                return new TokenResponseDto(null,null);
            }

        }

}
