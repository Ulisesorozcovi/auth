package com.crediticio.auth.users.ports.input;

import com.crediticio.auth.users.application.dto.UserResponse;

import java.util.List;

public interface GetUserInputPort {

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);
}
