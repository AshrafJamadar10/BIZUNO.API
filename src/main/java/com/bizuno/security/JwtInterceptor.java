package com.bizuno.security;

import com.bizuno.dtos.main.UserDO;
import com.bizuno.enums.ModelEnums;
import com.bizuno.models.business.BusinessUser;
import com.bizuno.models.main.Business;
import com.bizuno.models.main.Subscription;
import com.bizuno.models.main.User;
import com.bizuno.repositories.business.BusinessUserRepository;
import com.bizuno.repositories.main.BusinessRepository;
import com.bizuno.repositories.main.SubscriptionRepository;
import com.bizuno.repositories.main.UserRepository;
import com.bizuno.utils.JwtUtils;
import com.bizuno.utils.RoleFormatterForUI;
import com.bizuno.utils.TenantTransactionalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashSet;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final TenantTransactionalUtil tenantTransactionalUtil;
    private final RoleFormatterForUI roleFormatterForUI;
    private final BusinessRepository businessRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            //response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtils.validateToken(token)) {
                UserDO userDO = jwtUtils.getUserDataFromToken(token);

                if(userDO.getUserType().equals(ModelEnums.USER_TYPE.PLATFORM_USER.toString())){
                    Optional<User> userOpt = userRepository.findById(userDO.getUserId());
                    if (userOpt.isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("Invalid or expired token.");
                        return false;
                    }
                    User user = userOpt.get();
                    if(!userDO.getPhoneOrEmail().equals(user.getPhoneNumber()) &&  !userDO.getPhoneOrEmail().equals(user.getEmail())){
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("Invalid or expired token.");
                        return false;
                    }
                    userDO.setRole(roleFormatterForUI.formatRole(user.getRole() == null ? null : user.getRole()));
                }else if(userDO.getUserType().equals(ModelEnums.USER_TYPE.BUSINESS_USER.toString())) {
                    Optional<Business> businessOptional = businessRepository.findByBusinessCode(userDO.getBusinessCode());
                    if (businessOptional.isEmpty()) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("Invalid or expired token.");
                        return false;
                    }

                    Business business = businessOptional.get();

                    boolean response1 = tenantTransactionalUtil.excecuteInTenantContext(business.getTenantId(), business.getDbName(), em -> {
                        BusinessUserRepository businessUserRepository = tenantTransactionalUtil.getRepository(em, BusinessUserRepository.class);

                        Optional<BusinessUser> businessUserOpt = businessUserRepository.findById(userDO.getUserId());
                        if(businessUserOpt.isEmpty()) return false;

                        BusinessUser businessUser = businessUserOpt.get();
                        if(!userDO.getPhoneOrEmail().equals(businessUser.getPhoneNumber()) &&  !userDO.getPhoneOrEmail().equals(businessUser.getEmail())){
                            return false;
                        }

                        userDO.setRole(roleFormatterForUI.formatRole(businessUser.getRole() == null ? null : businessUser.getRole()));
                        return true;
                    });

                    if(userDO.getBusinessCode() != null){
                        Optional<Subscription> subscriptionOpt = subscriptionRepository.findCurrentSubscriptionByBusinessCode(userDO.getBusinessCode());
                        if(subscriptionOpt.isPresent()){
                            userDO.setPackageScopes(subscriptionOpt.get().getPack().getScopes());
                        } else {
                            userDO.setPackageScopes(new HashSet<>());
                        }
                    } else {
                        userDO.setPackageScopes(new HashSet<>());
                    }

                    if(!response1){
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("Invalid or expired token.");
                        return false;
                    }
                }

                // Set Spring Security Context
                UserPrincipal principal = new UserPrincipal(userDO);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);

                request.setAttribute("userDO", userDO);
                return true;
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or expired token.");
                return false;
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Authorization header missing or malformed.");
            return false;
        }
    }

}

