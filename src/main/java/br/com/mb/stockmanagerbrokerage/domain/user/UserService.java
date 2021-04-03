package br.com.mb.stockmanagerbrokerage.domain.user;

import java.security.Principal;

import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
	
	public String getUsername() {
		KeycloakAuthenticationToken authentication = (KeycloakAuthenticationToken) SecurityContextHolder.getContext()
				.getAuthentication();

		Principal principal = (Principal) authentication.getPrincipal();
		return principal.getName();
	}
}
