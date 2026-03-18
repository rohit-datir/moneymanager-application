package money.example.moneyManager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import money.example.moneyManager.dto.ExpenseDto;
import money.example.moneyManager.entity.ProfileEntity;
import money.example.moneyManager.repository.ProfileRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final ProfileRepo profileRepo;
    private final EmailService emailService;
    private final ExpenceService expenseService;


    @Value("${money.manager.frontend.url}")
    private String frontendUrl;

    @Scheduled(cron = "0 0 22 * * *",zone="IST")
    public void sendDailyIncomeExpenseReminder(){
        log.info("Job started:sendDailyIncomeExpenseReminder()");
        List<ProfileEntity> profiles = profileRepo.findAll();
        for(ProfileEntity profile:profiles){
            String email = profile.getEmail();
            String subject = "Daily Reminder: Log Your Income and Expenses";
            String body =
                    "Hi " + profile.getFullName() + ",<br><br>" +

                            "This is a friendly reminder to add your income and expenses for today " +
                            "in Money Manager.<br><br>" +

                            "<a href='" + frontendUrl + "' " +
                            "style='display:inline-block;padding:10px 20px;" +
                            "background-color:#4CAF50;color:white;text-decoration:none;" +
                            "border-radius:5px;'>Log Your Transactions</a>" +

                            "<br><br>Best Regards,<br>" +
                            "Money Manager Team";
            emailService.sendEmail(email, subject, body);
        }
        log.info("Job finished:sendDailyIncomeExpenseReminder()");
    }

    @Scheduled(cron = "0 0 23 * * *",zone="IST")
    public void sendDailyExpenseSummary(){
        log.info("Job started:sendDailyExpenseSummary()");
        List<ProfileEntity> profiles = profileRepo.findAll();
        for(ProfileEntity profile:profiles)
        {
          List<ExpenseDto> todaysExpense = expenseService.getExpensesForUserOnDate(profile.getId(), LocalDate.now());
          if(!todaysExpense.isEmpty()){
              StringBuilder table = new StringBuilder();
              table.append("<table style='border-collapse:collapse;width:100%;'>");
              table.append("<tr style='background-color:#f2f2f2;'><th style='border:1px solid #ddd;padding:8px;'> S.No</th><th style='border:1px solid #ddd;padding:8px;'>Name</th> <th style='border:1px solid #ddd;padding:8px;'>Amount</th> <th style='border:1px solid #ddd;padding:8px;'>Category</th> </tr>");
              int i = 1;
              for (ExpenseDto expense : todaysExpense) {
                  table.append("<tr>");
                  table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(i++).append("</td>");
                  table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(expense.getName()).append("</td>");
                  table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(expense.getCategoryId() != null ? expense.getCategoryName():"N/A").append("</td>");
                  table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(expense.getAmount()).append("</td>");
                  table.append("</tr>");
              }
              table.append("</table>");
              String body = "Hi "+profile.getFullName()+",<br></br> Here is a summary your expense for today:<br/><br/>"+table+"<br/><br/> Best regards,<br/> Money Manager Team";
              emailService.sendEmail(profile.getEmail(),"Your Daily Expense summary",body);
          }
        }

        log.info("job completed:sendDailyExpenseSummary()");

    }
}
