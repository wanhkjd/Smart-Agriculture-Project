package com.origin.pattern.factory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 工厂方法模式 — 单据工厂
 * 根据类型创建不同的仓库操作单据
 */
public class DocumentFactory {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static int seq = 0;

    public static WarehouseDocument createDocument(String type, Long productId, Double quantity) {
        String docNo = generateDocNo(type);
        return switch (type) {
            case "INBOUND" -> new InboundDocument(docNo, productId, quantity);
            case "OUTBOUND" -> new OutboundDocument(docNo, productId, quantity);
            case "PICKING" -> new PickingDocument(docNo, productId, quantity);
            case "REPLENISH" -> new ReplenishDocument(docNo, productId, quantity);
            default -> throw new IllegalArgumentException("未知单据类型: " + type);
        };
    }

    public static synchronized String generateDocNo(String type) {
        String prefix = switch (type) {
            case "INBOUND" -> "RK";
            case "OUTBOUND" -> "CK";
            case "PICKING" -> "JH";
            case "REPLENISH" -> "BH";
            default -> "XX";
        };
        seq = (seq + 1) % 10000;
        return prefix + LocalDateTime.now().format(FMT) + String.format("%04d", seq);
    }
}
