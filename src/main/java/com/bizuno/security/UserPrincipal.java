package com.bizuno.security;

import com.bizuno.dtos.main.RoleCrudePermissionResponseDTO;
import com.bizuno.dtos.main.UserDO;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
public class UserPrincipal implements UserDetails {

    private final UserDO userDO;

    public UserPrincipal(UserDO userDO) {
        this.userDO = userDO;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<GrantedAuthority> authorities = new ArrayList<>();

        // role
        if (userDO != null && userDO.getRole() != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + userDO.getRole().getName()));

            if (userDO.getRole().getCrudPermissions() != null) {
                for (RoleCrudePermissionResponseDTO permission : userDO.getRole().getCrudPermissions()) {
                    if (permission == null) {
                        continue;
                    }

                    String scope = permission.getScope();
                    if (permission.getOperations() == null) {
                        continue;
                    }

                    for (String operation : permission.getOperations()) {
                        authorities.add(new SimpleGrantedAuthority(scope + "_" + operation));
                    }
                }
            }
        }

        // Add package scope authorities
        if(userDO.getPackageScopes() != null){
            for(String scope : userDO.getPackageScopes()){
                authorities.add(new SimpleGrantedAuthority("PACKAGE_SCOPE_" + scope));
            }
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return userDO.getPhoneOrEmail();
    }

    @Override
    public String getUsername() {
        return userDO.getPhoneOrEmail();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

}

