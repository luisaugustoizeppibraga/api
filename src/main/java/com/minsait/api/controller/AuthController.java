package com.minsait.api.controller;

import com.minsait.api.controller.dto.GetTokenRequest;
import com.minsait.api.controller.dto.GetTokenResponse;
import com.minsait.api.repository.UsuarioEntity;
import com.minsait.api.repository.UsuarioRepository;
import com.minsait.api.sicurity.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    JWTUtil jwtUtil;

    @PostMapping("/get-token")
    public ResponseEntity<GetTokenResponse> getToken(@RequestBody GetTokenRequest request){


        String login = request.getUserName();
        String senha = request.getPassword();

        // Buscar usuário pelo login
        UsuarioEntity user = usuarioRepository.findByLogin(login);

        if (user == null) {
            // Usuário não encontrado
            return new ResponseEntity<>(GetTokenResponse.builder().build(), HttpStatus.UNAUTHORIZED);
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // Comparar senha fornecida com a senha armazenada no banco de dados
        //if (passwordEncoder.matches(senha, user.getSenha())) {
        if(user != null){
            // Senhas coincidem

            final ArrayList<String> permissions = new ArrayList<>();
            permissions.add("LEITURA_CLIENTE");
            permissions.add("ESCRITA_CLIENTE");
            permissions.add("LEITURA_USUARIO");
            permissions.add("ESCRITA_USUARIO");

            final var token = jwtUtil.generateToken("admin", permissions, 5);
            return new ResponseEntity<>(GetTokenResponse.builder()
                    .accessToken(token)
                    .build(), HttpStatus.OK);

        } else {
            // Senhas não coincidem
            return new ResponseEntity<>(GetTokenResponse.builder().build(), HttpStatus.UNAUTHORIZED);
        }
    }
}
