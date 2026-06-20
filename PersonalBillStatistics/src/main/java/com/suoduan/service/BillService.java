package com.suoduan.service;

import com.suoduan.entity.Bill;
import com.suoduan.entity.PageResult;
import com.suoduan.mapper.BillMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BillService {

    private final BillMapper ledgerMapper;

    @Autowired
    public BillService(BillMapper ledgerMapper) {
        this.ledgerMapper = ledgerMapper;
    }

    public PageResult<Bill> findByUserId(Integer userId, LocalDate start, LocalDate end,
                                         Integer categoryId, String type, int page, int pageSize) {
        int rowOffset = offsetOf(page, pageSize);
        List<Bill> records = ledgerMapper.findByUserId(
                userId, start, end, categoryId, type, rowOffset, pageSize);
        long recordCount = ledgerMapper.countByUserId(userId, start, end, categoryId, type);
        return new PageResult<>(records, recordCount, page, pageSize);
    }

    public void delete(Integer id, Integer userId) {
        ledgerMapper.delete(id, userId);
    }

    public Bill create(Bill bill) {
        ledgerMapper.insert(bill);
        return reload(bill);
    }

    public Bill update(Bill bill) {
        ledgerMapper.update(bill);
        return reload(bill);
    }

    public Bill findById(Integer id) {
        return ledgerMapper.findById(id);
    }

    private int offsetOf(int page, int pageSize) {
        return (page - 1) * pageSize;
    }

    private Bill reload(Bill bill) {
        return ledgerMapper.findById(bill.getId());
    }
}
