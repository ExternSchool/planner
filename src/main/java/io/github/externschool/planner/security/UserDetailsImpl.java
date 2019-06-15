package io.github.externschool.planner.security;

import io.github.externschool.planner.entity.User;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;

    private final User user;

    private String password;
    private final String username;
    private final Set<GrantedAuthority> authorities;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final boolean enabled;

    public UserDetailsImpl(final User user, final boolean accountNonExpired, final boolean accountNonLocked,
                           final boolean credentialsNonExpired, final boolean enabled) {
        this.user = user;
        this.password = user.getPassword();
        this.username = user.getEmail();
        this.authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = user.isEnabled();
    }

    public UserDetailsImpl(final User user) {
        this(user, true, true, true, true);
    }

    public User getUser() {
        return user;
    }

    @Override
    public Set<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof UserDetailsImpl)) return false;

        UserDetailsImpl that = (UserDetailsImpl) o;

        return new EqualsBuilder()
                .append(isAccountNonExpired(), that.isAccountNonExpired())
                .append(isAccountNonLocked(), that.isAccountNonLocked())
                .append(isCredentialsNonExpired(), that.isCredentialsNonExpired())
                .append(isEnabled(), that.isEnabled())
                .append(getUser(), that.getUser())
                .append(getPassword(), that.getPassword())
                .append(getUsername(), that.getUsername())
                .append(getAuthorities(), that.getAuthorities())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getPassword() != null ? getPassword() : 0)
                .append(getUsername() != null ? getUsername() : 0)
                .append(getAuthorities() != null ? getAuthorities() : 0)
                .append(isAccountNonExpired())
                .append(isAccountNonLocked())
                .append(isCredentialsNonExpired())
                .append(isEnabled())
                .toHashCode();
    }

    //taken from org.springframework.security.core.userdetails.User;
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append(": ");
        sb.append("Username: ").append(this.username).append("; ");
        sb.append("Password: [PROTECTED]; ");
        sb.append("Enabled: ").append(this.enabled).append("; ");
        sb.append("AccountNonExpired: ").append(this.accountNonExpired).append("; ");
        sb.append("credentialsNonExpired: ").append(this.credentialsNonExpired).append("; ");
        sb.append("AccountNonLocked: ").append(this.accountNonLocked).append("; ");
        if (!this.authorities.isEmpty()) {
            sb.append("Granted Authorities: ");
            boolean first = true;
            Iterator var3 = this.authorities.iterator();

            while(var3.hasNext()) {
                GrantedAuthority auth = (GrantedAuthority)var3.next();
                if (!first) {
                    sb.append(",");
                }

                first = false;
                sb.append(auth);
            }
        } else {
            sb.append("Not granted any authorities");
        }

        return sb.toString();
    }
}
