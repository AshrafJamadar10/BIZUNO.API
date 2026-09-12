package com.bizuno.services.main;

import com.bizuno.constants.AppConstants;
import com.bizuno.dtos.main.CommonResponse;
import com.bizuno.dtos.main.CreateBusinessRequestDTO;
import com.bizuno.dtos.main.LoginRequestDTO;
import com.bizuno.enums.ModelEnums;
import com.bizuno.models.main.Role;
import com.bizuno.models.main.User;
import com.bizuno.repositories.main.RoleRepository;
import com.bizuno.repositories.main.UserRepository;
import com.bizuno.utils.JwtUtils;
import com.bizuno.utils.RoleFormatterForUI;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtUtils jwtUtils;
    private final BusinessService businessService;
    private final RoleFormatterForUI roleFormatterForUI;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public CommonResponse loginSuperAdmin(@Valid LoginRequestDTO loginRequestDTO) {
        User user = findUserByCredential(loginRequestDTO.getPhoneOrEmail());

        if (user == null) {
            return new CommonResponse(AppConstants.STATUS_UNAUTHORIZED, AppConstants.MESSAGE_INVALID_CREDENTIALS);
        }

        if (user.getRole() == null || !ModelEnums.Titles.SUPER_ADMIN.name().equalsIgnoreCase(user.getRole().getName())) {
            return new CommonResponse(AppConstants.STATUS_UNAUTHORIZED, AppConstants.MESSAGE_INVALID_CREDENTIALS);
        }

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            return new CommonResponse(AppConstants.STATUS_UNAUTHORIZED, AppConstants.MESSAGE_INVALID_CREDENTIALS);
        }

        return buildLoginResponse(user);
    }

    @Transactional
    public CommonResponse register(@Valid CreateBusinessRequestDTO createBusinessRequestDTO) {
        return businessService.createBusiness(createBusinessRequestDTO);
    }

    public CommonResponse login(@Valid LoginRequestDTO loginRequestDTO) {
        User user = findUserByCredential(loginRequestDTO.getPhoneOrEmail());

        if (user == null) {
            return new CommonResponse(AppConstants.STATUS_UNAUTHORIZED, AppConstants.MESSAGE_INVALID_CREDENTIALS);
        }

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            return new CommonResponse(AppConstants.STATUS_UNAUTHORIZED, AppConstants.MESSAGE_INVALID_CREDENTIALS);
        }

        return buildLoginResponse(user);
    }

    private CommonResponse buildLoginResponse(User user) {
        String accessToken = jwtUtils.generateAccessToken(
                user.getEmail() != null ? user.getEmail() : user.getPhoneNumber(),
                user.getUserId().toString(),
                user.getRole().getType(),
                null
        );

        String refreshToken = jwtUtils.generateRefreshToken(
                user.getEmail() != null ? user.getEmail() : user.getPhoneNumber(),
                user.getUserId().toString()
        );

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("userId", user.getUserId());
        responseData.put("email", user.getEmail());
        responseData.put("phoneNumber", user.getPhoneNumber());
        responseData.put("role", user.getRole() != null ? roleFormatterForUI.formatRole(user.getRole()) : null);
        responseData.put("roleType", user.getRole() != null ? user.getRole().getType() : null);
        responseData.put("accessToken", accessToken);
        responseData.put("refreshToken", refreshToken);
        responseData.put("tokenType", "Bearer");

        return new CommonResponse(AppConstants.STATUS_SUCCESS, AppConstants.MESSAGE_LOGIN_SUCCESS, responseData);
    }

    private User findUserByCredential(String phoneOrEmail) {
        if (phoneOrEmail == null || phoneOrEmail.isBlank()) {
            return null;
        }

        String value = phoneOrEmail.trim();
        Optional<User> byEmail = userRepository.findByEmailIgnoreCase(value);
        return byEmail.orElseGet(() -> userRepository.findByPhoneNumber(value).orElse(null));

    }
}
