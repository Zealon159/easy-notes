package cn.zealon.notes.controller;

import cn.zealon.notes.common.result.Result;
import cn.zealon.notes.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 仪表盘
 * @author: zealon
 * @since: 2021/1/7
 */
@RequestMapping("dashboard")
@RestController
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * 获取仪表盘数据
     * @return
     */
    @GetMapping("/summary")
    public Result getDashboardSummary(){
        return this.dashboardService.getDashboardSummary();
    }
}