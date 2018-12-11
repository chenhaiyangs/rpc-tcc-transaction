package com.chenhaiyang.tcc.transaction.dashboard.page;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据分页
 * @author chenhaiyang
 */
@Data
@AllArgsConstructor
public class DataGrid {
    private long total=0;
    private List<OperateVo> rows = new ArrayList<>();
}