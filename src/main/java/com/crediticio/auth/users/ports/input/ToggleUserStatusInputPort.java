package com.crediticio.auth.users.ports.input;

import com.crediticio.auth.users.application.dto.ToggleStatusRequest;
import com.crediticio.auth.users.application.dto.UserResponse;

public interface ToggleUserStatusInputPort {

    UserResponse toggleStatus(Long id, ToggleStatusRequest request);
}
