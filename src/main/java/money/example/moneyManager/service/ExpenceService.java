package money.example.moneyManager.service;

import lombok.RequiredArgsConstructor;
import money.example.moneyManager.dto.ExpenseDto;
import money.example.moneyManager.entity.CatogoreyEntity;
import money.example.moneyManager.entity.ExpenseEntity;
import money.example.moneyManager.entity.ProfileEntity;
import money.example.moneyManager.repository.CategoryRepo;
import money.example.moneyManager.repository.ExpensRepo;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenceService {
   private final CategoryRepo categoryRepo;
    private final ExpensRepo expensRepo;
    private final ProfileService profileService;
    private final ModelMapper modelMapper;

    //add expense to the database
    public ExpenseDto addExpense(ExpenseDto dto)
    {
        ProfileEntity profile = profileService.getCurrentProfile();
        CatogoreyEntity category = categoryRepo.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: "));

        ExpenseEntity newExpense =  modelMapper.map(dto, ExpenseEntity.class);

        newExpense.setProfile(profile);
        newExpense.setCategory(category);

        newExpense = expensRepo.save(newExpense);

        return modelMapper.map(newExpense,ExpenseDto.class);

    }
    public List<ExpenseDto> getCurrentMonthExpensesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<ExpenseEntity> list =expensRepo.findByProfileIdAndDateBetween(profile.getId(),startDate,endDate);
        return list.stream().map(expense -> modelMapper.map(expense,ExpenseDto.class)).toList();
    }

    public void deleteExpense(Long expenseId){
        ProfileEntity profile = profileService.getCurrentProfile();
       ExpenseEntity entity =  expensRepo.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found with id: " + expenseId));

       if(!entity.getProfile().getId().equals(profile.getId())){
           throw new RuntimeException("You are not authorized to delete this expense");
       }
       expensRepo.delete(entity);
    }

    public List<ExpenseDto> getLatest5ExpensesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();

        List<ExpenseEntity> list = expensRepo.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(expense -> modelMapper.map(expense,ExpenseDto.class)).toList();
    }


    public BigDecimal getTotalExpensesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
       BigDecimal total = expensRepo.findTotalExpenseByProfileId(profile.getId());
         return total != null ? total : BigDecimal.ZERO;
    }

    public List<ExpenseDto> filterExpense(LocalDate startDate, LocalDate endDate, String keyword, Sort sort)
    {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> list=expensRepo.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate,endDate,keyword,sort);
        return list.stream().map(expense -> modelMapper.map(expense,ExpenseDto.class)).toList();
    }

    public List<ExpenseDto> getExpensesForUserOnDate(Long profileId,LocalDate date)
    {
          List<ExpenseEntity> list =expensRepo.findByProfileIdAndDate(profileId,date);
        return list.stream().map(expense -> modelMapper.map(expense,ExpenseDto.class)).toList();
    }



}
