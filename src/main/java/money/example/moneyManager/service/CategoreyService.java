package money.example.moneyManager.service;

import lombok.RequiredArgsConstructor;
import money.example.moneyManager.dto.CategoryDto;
import money.example.moneyManager.entity.CatogoreyEntity;
import money.example.moneyManager.entity.ProfileEntity;
import money.example.moneyManager.repository.CategoryRepo;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoreyService {

    private final ProfileService profileService;
    private final CategoryRepo categoryRepo;
    private final ModelMapper modelMapper;

    public CategoryDto saveCategory(CategoryDto categoryDto) {

        // 1️⃣ Get logged-in user profile
        ProfileEntity profile = profileService.getCurrentProfile();

        // 2️⃣ Check duplicate category for same profile
        if (categoryRepo.existsByNameAndProfileId(categoryDto.getName(), profile.getId())) {
           throw new RuntimeException("Category name already exists for this profile");
        }

        // 3️⃣ DTO -> Entity (basic fields only)
        CatogoreyEntity categoryEntity =
                modelMapper.map(categoryDto, CatogoreyEntity.class);

        // 4️⃣ Manually set relationship
        categoryEntity.setProfile(profile);

        // 5️⃣ Save entity
        CatogoreyEntity savedCategory = categoryRepo.save(categoryEntity);

        // 6️⃣ Entity -> DTO
        CategoryDto responseDto =
                modelMapper.map(savedCategory, CategoryDto.class);

        // 7️⃣ Manually set nested value
        responseDto.setProfilId(profile.getId());

        return responseDto;
    }

    public List<CategoryDto> getCategoreyCurrentaUser() {

        ProfileEntity profile = profileService.getCurrentProfile();

        List<CatogoreyEntity> categories =
                categoryRepo.findByProfileId(profile.getId());

        return categories.stream()
                .map(cat -> modelMapper.map(cat, CategoryDto.class))
                .collect(Collectors.toList());
    }

    public List<CategoryDto> getCategoriesByTypeForCurrentUser(String type) {
        ProfileEntity profile = profileService.getCurrentProfile();

        List<CatogoreyEntity> entities =
                categoryRepo.findByTypeAndProfileId(type, profile.getId());

        return entities.stream()
                .map(cat -> modelMapper.map(cat, CategoryDto.class))
                .collect(Collectors.toList());
    }

    public CategoryDto updateCategory(Long categoryId,CategoryDto dto)
    {
        ProfileEntity profile = profileService.getCurrentProfile();
        CatogoreyEntity existingCategory = categoryRepo.findByIdAndProfileId(categoryId, profile.getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        existingCategory.setName(dto.getName());
        existingCategory.setIcon(dto.getIcon());
        existingCategory.setType(dto.getType());
        categoryRepo.save(existingCategory);
        return modelMapper.map(existingCategory, CategoryDto.class);
    }
}
