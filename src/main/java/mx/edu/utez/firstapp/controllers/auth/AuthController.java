package mx.edu.utez.firstapp.controllers.auth;


import mx.edu.utez.firstapp.config.ApiResponse;
import mx.edu.utez.firstapp.services.Auth.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"*"})
public class AuthController {
    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse> signIn(@RequestBody SignDto dto) {
        return service.singIn(dto.getUsername(), dto.getPassword());
    }

}
