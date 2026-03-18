package money.example.moneyManager.controller;

import lombok.RequiredArgsConstructor;
import money.example.moneyManager.dto.AuthDto;
import money.example.moneyManager.dto.ProfileDto;
import money.example.moneyManager.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class ProfileControler {

    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileDto> registerProfile(@RequestBody ProfileDto profileDto)
    {
        ProfileDto registerProfile = profileService.registerProfile(profileDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(registerProfile);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token)
    {
        boolean isActivated = profileService.activateProfile(token);
        if(isActivated)
        {
            return ResponseEntity.ok("Profile activated successfully");
        }
        else
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activation token not found or already used");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,Object>> login(@RequestBody AuthDto authDto)
    {
        try{
          if(!profileService.isAccountActive(authDto.getEmail()))
          {
              return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message","Account is not activated"));
          }
          Map<String,Object> response = profileService.authencateAndGenerateTokenL(authDto);
            return ResponseEntity.ok(response);
        }catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message",e.getMessage()));

        }

    }



}
