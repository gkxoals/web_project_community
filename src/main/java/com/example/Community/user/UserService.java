package com.example.Community.user;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public void createSiteUser(SiteUser siteuser) {
        if (siteuser.getRole() == null) {
            siteuser.setRole(Role.USER);
        }
        userRepository.save(siteuser);
    }

    public boolean isUsernameTaken(String username) {
        return userRepository.findByUsername(username) != null;
    }

    public SiteUser validateUser(String username, String password) {
        SiteUser user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}
