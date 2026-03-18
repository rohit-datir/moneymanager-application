package money.example.moneyManager.controller;

import lombok.RequiredArgsConstructor;
import money.example.moneyManager.dto.ExpenseDto;
import money.example.moneyManager.dto.FilterDto;
import money.example.moneyManager.dto.IncomeDto;
import money.example.moneyManager.service.ExpenceService;
import money.example.moneyManager.service.IncomService;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/filter")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class FilterController {
    private final ExpenceService expenceService;
    private final IncomService incomService;

    @PostMapping
    public ResponseEntity<?> filterTransaction(@RequestBody FilterDto filter)
    {
        LocalDate startDate = filter.getStartDate() != null ? filter.getStartDate() : LocalDate.MIN;
        LocalDate endDate = filter.getEndDate() != null ? filter.getEndDate() : LocalDate.now();
        String keyword = filter.getKeyword() != null ? filter.getKeyword() : "";
        String sortField = filter.getSortField() != null ? filter.getSortField() : "date";
        Sort.Direction direction = "desc".equalsIgnoreCase(filter.getSortOrder()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortField);
        if("income".equalsIgnoreCase(filter.getType()))
        {
            List<IncomeDto> incomes = incomService.filterIncomes(startDate,endDate,keyword,sort);
            return ResponseEntity.ok(incomes);
        }
        else if("expense".equalsIgnoreCase(filter.getType()))
        {
            List<ExpenseDto> expenses = expenceService.filterExpense(startDate,endDate,keyword,sort);
            return ResponseEntity.ok(expenses);
        }
        else
        {
            return ResponseEntity.badRequest().body("Invalid type. Must be 'income' or 'expense'.");
        }
    }
}
