package com.iapppay.datax.task;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.iapppay.datax.service.CpcashDetailService;

/**
 * 
 * @className: CpCashDetailDataxTask
 * @classDescription:生成明细定时任务
 * @author lishiqiang
 * @create_date: 2019年7月18日 上午10:00:12
 * @update_date:
 */
@Component
public class CpCashDetailDataxTask {
	private Logger log = LoggerFactory.getLogger(CpCashDetailDataxTask.class);

	@Resource
	private CpcashDetailService cpcashDetailService;


	@Scheduled(cron = "${datax.generateCpCashDetailData}")
	public void executor() {
		try {
			cpcashDetailService.generatorCpCashDetailFile();
		} catch (Exception e) {
			log.error("生成账期明细失败:{}", e.getMessage(), e);
		}
	}
}
