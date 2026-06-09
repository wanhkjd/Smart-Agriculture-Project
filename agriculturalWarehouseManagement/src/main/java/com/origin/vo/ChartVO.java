package com.origin.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartVO {
    private List<String> productNames;
    private List<Double> productQuantities;
    private List<Map<String, Object>> categoryPie;
}
