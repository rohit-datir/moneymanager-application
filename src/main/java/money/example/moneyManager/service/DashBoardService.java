package money.example.moneyManager.service;

import lombok.RequiredArgsConstructor;
import money.example.moneyManager.dto.ExpenseDto;
import money.example.moneyManager.dto.IncomeDto;
import money.example.moneyManager.dto.RecentTransactionDto;
import money.example.moneyManager.entity.ProfileEntity;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Stream.concat;

@Service
@RequiredArgsConstructor
public class DashBoardService {

    private final ExpenceService expenceService;
    private final IncomService incomService;
    private final ProfileService profileService;

    public Map<String,Object> getDashBoardDate(){
        ProfileEntity profile = profileService.getCurrentProfile();
        Map<String,Object> returnValue = new LinkedHashMap<>();
        List<IncomeDto> latestIncomes = incomService.getLatest5IncomesForCurrentUser();
        List<ExpenseDto> latestExpenses = expenceService.getLatest5ExpensesForCurrentUser();
      List<RecentTransactionDto> recentTransactions = concat(latestIncomes.stream().map(income -> RecentTransactionDto.builder()
                .id(income.getId())
                .amount(income.getAmount())
                .profileId(income.getId())
                .icon(income.getIcon())
                .date(income.getDate())
                .name(income.getName())
                .createdAt(income.getCreatedAt())
                .updatedAt(income.getUpdatedAt())
                .type("income")
                .build()),

                latestExpenses.stream().map(expense ->
                        RecentTransactionDto.builder()
                                .id(expense.getId())
                                .profileId(profile.getId())
                                .icon(expense.getIcon())
                                .name(expense.getName())
                                .amount(expense.getAmount())
                                .date(expense.getDate())
                                .createdAt(expense.getCreatedAt())
                                .updatedAt(expense.getUpdatedAt())
                                .type("expense")
                                .build()))
              .sorted((a,b) ->{
                  int cmp = b.getDate().compareTo(a.getDate());
                  if(cmp == 0 && a.getCreatedAt() != null && b.getCreatedAt() != null){
                      return b.getCreatedAt().compareTo(a.getCreatedAt());
                  }
                  return cmp;
              }).collect(Collectors.toList());

      returnValue.put("totalBalance",incomService.getTotalIncomesForCurrentUser().subtract(expenceService.getTotalExpensesForCurrentUser()));
      returnValue.put("totalIncome",incomService.getTotalIncomesForCurrentUser());
      returnValue.put("totalExpense",expenceService.getTotalExpensesForCurrentUser());
      returnValue.put("recent5Expenses",latestExpenses);
      returnValue.put("recent5Incomes",latestIncomes);
      returnValue.put("recentTransactions",recentTransactions);
      return returnValue;


    }

}
