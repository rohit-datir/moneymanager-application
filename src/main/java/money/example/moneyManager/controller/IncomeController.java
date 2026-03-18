package money.example.moneyManager.controller;

import lombok.RequiredArgsConstructor;
import money.example.moneyManager.dto.ExpenseDto;
import money.example.moneyManager.dto.IncomeDto;
import money.example.moneyManager.service.ExpenceService;
import money.example.moneyManager.service.IncomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/incomes")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class IncomeController {
private final ExpenceService expenceService;

private final IncomService incomService;

    @PostMapping
    public ResponseEntity<IncomeDto> addExpense(@RequestBody IncomeDto dto) {
        IncomeDto saved = incomService.addIncome(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<IncomeDto>> getExpenses(){
        List<IncomeDto> expenses =  incomService.getCurrentMonthIncomeForCurrentUser();
        return ResponseEntity.ok(expenses);

    }

    @DeleteMapping("/{incomeId}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long incomeId){
        incomService.deleteIncome(incomeId);
        return ResponseEntity.noContent().build();
    }

}

