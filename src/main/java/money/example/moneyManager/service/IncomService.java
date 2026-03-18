package money.example.moneyManager.service;

import lombok.RequiredArgsConstructor;
import money.example.moneyManager.dto.ExpenseDto;
import money.example.moneyManager.dto.IncomeDto;
import money.example.moneyManager.entity.CatogoreyEntity;
import money.example.moneyManager.entity.ExpenseEntity;
import money.example.moneyManager.entity.IncomeEntity;
import money.example.moneyManager.entity.ProfileEntity;
import money.example.moneyManager.repository.CategoryRepo;
import money.example.moneyManager.repository.IncomeRepo;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomService {
   private final CategoryRepo categoryRepo;
    private final IncomeRepo incomeRepo;
    private final ModelMapper modelMapper;
    private final ProfileService profileService;

    public IncomeDto addIncome(IncomeDto dto)
    {
        ProfileEntity profile = profileService.getCurrentProfile();
        CatogoreyEntity category = categoryRepo.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: "));

        IncomeEntity inCome =  modelMapper.map(dto, IncomeEntity.class);

        inCome.setProfile(profile);
        inCome.setCategory(category);

        inCome = incomeRepo.save(inCome);

        return modelMapper.map(inCome,IncomeDto.class);
    }

    public List<IncomeDto> getCurrentMonthIncomeForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<IncomeEntity> list =incomeRepo.findByProfileIdAndDateBetween(profile.getId(),startDate,endDate);
        return list.stream().map(expense -> modelMapper.map(expense,IncomeDto.class)).toList();
    }

    public void deleteIncome(Long incomeId){
        ProfileEntity profile = profileService.getCurrentProfile();
        IncomeEntity entity =  incomeRepo.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Expense not found with id: " + incomeId));

        if(!entity.getProfile().getId().equals(profile.getId())){
            throw new RuntimeException("You are not authorized to delete this income");
        }
        incomeRepo.delete(entity);
    }

    public List<IncomeDto> getLatest5IncomesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();

        List<IncomeEntity> list = incomeRepo.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(expense -> modelMapper.map(expense,IncomeDto.class)).toList();
    }


    public BigDecimal getTotalIncomesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal total = incomeRepo.findTotalExpenseByProfileId(profile.getId());
        return total != null ? total : BigDecimal.ZERO;
    }

    public List<IncomeDto> filterIncomes(LocalDate startDate, LocalDate endDate, String keyword, Sort sort)
    {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> list=incomeRepo.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate,endDate,keyword,sort);
        return list.stream().map(expense -> modelMapper.map(expense,IncomeDto.class)).toList();
    }


}
