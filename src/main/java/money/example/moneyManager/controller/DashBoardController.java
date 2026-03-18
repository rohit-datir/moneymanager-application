package money.example.moneyManager.controller;

import lombok.RequiredArgsConstructor;
import money.example.moneyManager.service.DashBoardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class DashBoardController {

    private final DashBoardService dashBoardService;

    public ResponseEntity<Map<String ,Object>> getDashBoarData()
    {
        Map<String,Object> dashBoardData = dashBoardService.getDashBoardDate();
        return ResponseEntity.ok(dashBoardData);
    }


}
