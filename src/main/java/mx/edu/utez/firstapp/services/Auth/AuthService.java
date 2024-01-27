package mx.edu.utez.firstapp.services.Auth;

import mx.edu.utez.firstapp.config.ApiResponse;
import mx.edu.utez.firstapp.models.user.User;
import mx.edu.utez.firstapp.security.jwt.JwtProvider;
import mx.edu.utez.firstapp.services.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AuthService {
    private final UserService userService;
    private final AuthenticationManager manager;
    private final JwtProvider provider;

    private final String PREFIX = "Bearer";


    public AuthService(UserService userService, AuthenticationManager manager, JwtProvider provider) {
        this.userService = userService;
        this.manager = manager;
        this.provider = provider;
    }

    // este metdo
    @Transactional
    public ResponseEntity<ApiResponse> singIn(String username, String password) {
        try {
            Optional<User> foundUser = userService.findUserByUsername(username);
            if (foundUser.isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST, true, "UserNotFound"), HttpStatus.BAD_REQUEST);
            }
            User user = foundUser.get();
            if (!user.getStatus()) {
                return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST, true, "UserNotActive"), HttpStatus.BAD_REQUEST);
            }
            if (!user.getBlocked()) {
                return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST, true, "UserBlocked"), HttpStatus.BAD_REQUEST);
            }
            Authentication auth = manager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            SecurityContextHolder.getContext().setAuthentication(auth);
            String token = provider.generateToken(auth);
            //Payload - DTO (token, attrs)
            return new ResponseEntity<>(new ApiResponse(token, HttpStatus.OK), HttpStatus.OK);

        } catch (Exception e) {
            String message = "CredtionalsMismatch";
            if (e instanceof DisabledException) {
                message = "UserDisabled";
            }
            return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST, true, message), HttpStatus.BAD_REQUEST);
        }
    }

}
