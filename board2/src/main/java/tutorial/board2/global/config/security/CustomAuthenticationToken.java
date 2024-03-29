package tutorial.board2.global.config.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomAuthenticationToken extends AbstractAuthenticationToken {

    private CustomUserDetails principal;

    public CustomAuthenticationToken(CustomUserDetails principal, Collection<? extends GrantedAuthority> authorities){
        super(authorities);
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return new UnsupportedOperationException();
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

}
