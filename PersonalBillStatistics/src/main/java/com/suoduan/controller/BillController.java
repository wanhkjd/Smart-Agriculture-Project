package com.suoduan.controller;

import com.suoduan.entity.Bill;
import com.suoduan.entity.PageResult;
import com.suoduan.entity.Result;
import com.suoduan.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    private final BillService ledgerService;

    @Autowired
    public BillController(BillService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id, HttpSession session) {
        ledgerService.delete(id, sessionUserId(session));
        return Result.ok();
    }

    @PostMapping
    public Result<Bill> create(@RequestBody Bill payload, HttpSession session) {
        payload.setUserId(sessionUserId(session));
        Bill savedBill = ledgerService.create(payload);
        return Result.ok(savedBill);
    }

    @PutMapping("/{id}")
    public Result<Bill> update(@PathVariable Integer id, @RequestBody Bill payload) {
        payload.setId(id);
        Bill changedBill = ledgerService.update(payload);
        return Result.ok(changedBill);
    }

    @GetMapping
    public Result<PageResult<Bill>> list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpSession session) {
        PageResult<Bill> pageData = ledgerService.findByUserId(
                sessionUserId(session), start, end, categoryId, type, page, pageSize);
        return Result.ok(pageData);
    }

    private Integer sessionUserId(HttpSession session) {
        return (Integer) session.getAttribute("userId");
    }
}
