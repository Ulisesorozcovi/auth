package com.crediticio.auth.users.ports.input;

import com.crediticio.auth.users.application.dto.ChangeRoleRequest;
import com.crediticio.auth.users.application.dto.UserResponse;

public interface UpdateUserRoleInputPort {

    UserResponse updateRole(Long id, ChangeRoleRequest request);
}
