package money.example.moneyManager.controller;

import lombok.RequiredArgsConstructor;
import money.example.moneyManager.dto.CategoryDto;
import money.example.moneyManager.service.CategoreyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class CategoreyConreoller{

    private final CategoreyService categoreyService;

    @PostMapping
    public ResponseEntity<CategoryDto> saveCategory(@RequestBody CategoryDto categoryDto) {
        CategoryDto savedCategory = categoreyService.saveCategory(categoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories()
    {
        List<CategoryDto> categories = categoreyService.getCategoreyCurrentaUser();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{type}")
    public ResponseEntity<List<CategoryDto>> getCategoriesByTypeForCurrentUser(@PathVariable String type){
        List<CategoryDto> list = categoreyService.getCategoriesByTypeForCurrentUser(type);
        return ResponseEntity.ok(list);

    }

    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long id,@RequestBody CategoryDto categoryDto)
    {
        CategoryDto updateCategory = categoreyService.updateCategory(id,categoryDto);
        return ResponseEntity.ok(updateCategory);
    }




}
