package com.crediticio.auth.users.infrastructure.input;

import com.crediticio.auth.users.application.dto.CreateUserRequest;
import com.crediticio.auth.users.application.dto.ToggleStatusRequest;
import com.crediticio.auth.users.application.dto.UserResponse;
import com.crediticio.auth.users.ports.input.CreateUserInputPort;
import com.crediticio.auth.users.ports.input.GetUserInputPort;
import com.crediticio.auth.users.ports.input.ToggleUserStatusInputPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final CreateUserInputPort createUserInputPort;
    private final GetUserInputPort getUserInputPort;
    private final ToggleUserStatusInputPort toggleUserStatusInputPort;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = createUserInputPort.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(getUserInputPort.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(getUserInputPort.getUserById(id));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<UserResponse> toggleStatus(
            @PathVariable Long id,
            @Valid @RequestBody ToggleStatusRequest request) {
        return ResponseEntity.ok(toggleUserStatusInputPort.toggleStatus(id, request));
    }
}
