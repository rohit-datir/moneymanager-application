package money.example.moneyManager.service;

import lombok.RequiredArgsConstructor;
import money.example.moneyManager.entity.ProfileEntity;
import money.example.moneyManager.repository.ProfileRepo;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

//Spring Security does not know your DB, so you tell it.
//Your user entity ≠ Spring Security user
//
//Spring Security needs a bridge
//
//👉 That bridge = CustomUserDetailsService

//Spring Security uses UserDetailsService to load user-specific data. Since our user information is stored in the database and uses email instead of username, we implement a CustomUserDetailsService to fetch user details from the DB and convert them into Spring Security’s UserDetails format.
@Service
@RequiredArgsConstructor
public class AppuserDetailService implements UserDetailsService {
    private final ProfileRepo profileRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       ProfileEntity existingProfile =  profileRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " not found"));
       return User.builder()
               .username(existingProfile.getEmail())
               .password(existingProfile.getPassword())
               .authorities(Collections.emptyList())
               .build();
    }
}
