package carrot.market.common.security.provider;

import carrot.market.common.security.auth.PrincipalDetails;
import carrot.market.common.security.auth.PrincipalDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class CustomAuthProvider implements AuthenticationProvider {

    private final PrincipalDetailsService principalDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();
        PrincipalDetails userDetails = (PrincipalDetails) principalDetailsService.loadUserByUsername(username);

        if(userDetails == null)
            throw new BadCredentialsException("아이디, 비밀번호를 다시 확인해주세요");

        // PW 검사
        if (!passwordEncoder.matches(password, userDetails.getPassword()))
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");

        // 인증 완료 시 완료된 Authentication 객체를 리턴
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}