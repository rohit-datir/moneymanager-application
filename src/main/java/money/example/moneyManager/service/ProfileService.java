package money.example.moneyManager.service;

import lombok.RequiredArgsConstructor;
import money.example.moneyManager.dto.AuthDto;
import money.example.moneyManager.dto.ProfileDto;
import money.example.moneyManager.entity.ProfileEntity;
import money.example.moneyManager.repository.ProfileRepo;
import money.example.moneyManager.utils.JwtUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepo profileRepo;
    private final ModelMapper modelMapper;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Value("${app.activation.url}")
    private String activationUrl;

    public ProfileDto registerProfile(ProfileDto profileDto)
    {

        ProfileEntity newprofile = modelMapper.map(profileDto, ProfileEntity.class);
        newprofile.setActivationToken(UUID.randomUUID().toString());
        newprofile.setPassword(passwordEncoder.encode(profileDto.getPassword()));
        newprofile = profileRepo.save(newprofile);
        String activationLink = activationUrl+"/api/v1.0/activate?token=" + newprofile.getActivationToken();
        String subject = "Activate your account";
        String body = "Click on the Following link to activate your account: " + activationLink;
        emailService.sendEmail(newprofile.getEmail(), subject, body);
        return modelMapper.map(newprofile, ProfileDto.class);
    }


    public boolean activateProfile(String activationToken) {
       return profileRepo.findByActivationToken(activationToken).map(profile -> {
            profile.setIsActive(true);
            profileRepo.save(profile);
            return true;
        }).orElse(false);
    }

    public boolean isAccountActive(String email) {
        return profileRepo.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }

    public ProfileEntity getCurrentProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return profileRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + authentication.getName() + " not found"));
    }

    public ProfileDto getPublicProfile(String email)
    {
        ProfileEntity currentUser = null;
        if (email == null)
        {
            currentUser = getCurrentProfile();
        }else{
          currentUser = profileRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " not found"));
        }
        return modelMapper.map(currentUser,ProfileDto.class);
    }


    //AuthenticationManager is responsible for validating user credentials and deciding whether authentication is successful or not.
    public Map<String, Object> authencateAndGenerateTokenL(AuthDto authDto) {
       try{
           authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDto.getEmail(),authDto.getPassword()));
           String token = jwtUtil.generateToken(authDto.getEmail());

           return Map.of(
                     "token",token,
                   "user",getPublicProfile(authDto.getEmail()
           ));
       }catch (Exception e)
       {
           throw new RuntimeException("Invalid email or password");
       }

    }
}
