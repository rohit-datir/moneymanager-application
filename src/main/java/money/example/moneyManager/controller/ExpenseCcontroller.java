package money.example.moneyManager.controller;

import lombok.RequiredArgsConstructor;
import money.example.moneyManager.dto.ExpenseDto;
import money.example.moneyManager.service.ExpenceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expenses")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class ExpenseCcontroller {
  private final ExpenceService expenceService;
    @PostMapping
    public ResponseEntity<ExpenseDto> addExpense(@RequestBody ExpenseDto dto) {
        ExpenseDto saved = expenceService.addExpense(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseDto>> getExpenses(){
       List<ExpenseDto> expenses =  expenceService.getCurrentMonthExpensesForCurrentUser();
       return ResponseEntity.ok(expenses);
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long expenseId){
        expenceService.deleteExpense(expenseId);
        return ResponseEntity.noContent().build();
    }

}

