package ru.aziattsev.pdm_system.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import ru.aziattsev.pdm_system.entity.UserRole;
import ru.aziattsev.pdm_system.repository.PdmUserRepository;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PdmUserService implements UserDetailsService {

    private final PdmUserRepository pdmUserRepository;
    private final PasswordEncoder passwordEncoder;

    public PdmUserService(PdmUserRepository pdmUserRepository, PasswordEncoder passwordEncoder) {
        this.pdmUserRepository = pdmUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return pdmUserRepository.findByUsername(username)
                .map(user -> User.builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .authorities(user.getRoles().stream()
                                .map(UserRole::getAuthority)
                                .toArray(String[]::new))
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

//    public void registerNewUser(UserRegistrationDto registrationDto) {
//        if (pdmUserRepository.existsByUsername(registrationDto.getUsername())) {
//            throw new IllegalArgumentException("Username already exists");
//        }
//
//        PdmUser user = new PdmUser();
//        user.setUsername(registrationDto.getUsername());
//        user.setDisplayName(registrationDto.getDisplayName());
//        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
//        user.setActive(true);
//        user.setRoles(Set.of(UserRole.USER));
//
//        pdmUserRepository.save(user);
//    }
}