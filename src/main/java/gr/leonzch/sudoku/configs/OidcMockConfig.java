package gr.leonzch.sudoku.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.time.Instant;
import java.util.Map;

@Configuration
@Profile("dev")
public class OidcMockConfig {
    @Bean
    public OidcUser mockOidcUser() {
        Map<String, Object> claims = Map.of(
                StandardClaimNames.SUB, "testuser",
                StandardClaimNames.PREFERRED_USERNAME, "localuser",
                StandardClaimNames.EMAIL, "localuser@local.com"
        );

        OidcIdToken fakeToken = new OidcIdToken(
                "fake-token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                claims
        );

        return new DefaultOidcUser(null, fakeToken);
    }
}
