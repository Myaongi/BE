package Myaong.Gangajikimi.auth.userDetails;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import Myaong.Gangajikimi.member.entity.Member;

public class CustomUserDetails implements UserDetails {

	private final Member member;

	public CustomUserDetails(Member member) {
		this.member = member;
	}

	public Long getId() {
		return member.getId();
	}

	public String getEmail() {
		return member.getEmail();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(
				(GrantedAuthority) () -> "ROLE_" + member.getRole().name()
		);
	}

	@Override
	public String getPassword() {
		return member.getPassword();
	}

	@Override
	public String getUsername() {
		return member.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}