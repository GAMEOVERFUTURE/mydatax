package com.iapppay.datax.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.iapppay.datax.redis.RedisUtils;
import com.iapppay.operating.constant.EnumBdSettleFileCode;
import com.iapppay.operating.constant.EnumTopicType;

/**
 * 
 * @className: CpcashConfiguration
 * @classDescription:配置信息
 * @author lishiqiang
 * @create_date: 2019年7月22日 下午5:56:52
 * @update_date:
 */
public class CpcashConfiguration {

	@Autowired
	private RedisUtils redisUtils;

	@Value("${spring.data.mongodb.host}")
	public String mongoDbHost;

	@Value("${spring.data.mongodb.port}")
	public String mongoDbProt;

	@Value("${spring.data.mongodb.database}")
	public String dbName;

	@Value("${spring.data.mongodb.username}")
	public String userName;

	@Value("${spring.data.mongodb.password}")
	public String password;

	/**
	 * 根据billno判断账期
	 */
	public String MONTH_SETTLEMENT = "3";

	// v2.0.9 账期类型 0 日账单 1 月账单
	public String BALA_BILL_TYPE_DAY = "0";
	public String BALA_BILL_TYPE_MONTH = "1";

	// v2.0.9 账期类型 2 补结 类型账期
	public String BALA_BILL_TYPE_FILL = "2";

	// 微信服务商通道大类
	long BALA_WX_BUS_CHANNO = 703;

	// 报表数据生成状态 0 未生成 1 已生成
	String BALA_BILL_RPT_STAT_INIT = "0";
	String BALA_BILL_RPT_STAT_GENERATED = "1";

	/**
	 * 文件地址
	 */
	public String filepath;

	/**
	 * 文件名
	 */
	public String filename;

	/**
	 * 查询具体哪个数据库
	 */
	public String month;

	public String billno;

	public String array;

	public String isCp;

	protected String fileBasePath;

	/**
	 * 查询条件
	 */
	public String query;

	public String fileHeader;

	public String getMongoDbAddress() {
		return mongoDbHost.concat(":").concat(mongoDbProt);
	}

	public static String currentProjectPath() {
		return System.getProperty("user.dir");
	}

	/**
	 * 获取文件存储的地址
	 * 
	 * @param topictype
	 * @return
	 */
	public String getServerFilePath(Integer topictype, String filename) {
		String filePath = null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		if (EnumTopicType.EXEC_BILL_DETAIL.getCode().equals(topictype)) {
			filePath = (String) redisUtils.getSystemConf("sigle.cpcashDetail.export");
			this.fileBasePath = filePath;
			filePath = filePath + "/" + formatter.format(new Date()) + "/" + filename;
		} else if (EnumTopicType.EXEC_BATCH_BILL_DETAIL.getCode().equals(topictype)) {
			filePath = (String) redisUtils.getSystemConf("batch.cpcashDetail.export");
			this.fileBasePath = filePath;
			filePath = filePath + "/" + formatter.format(new Date()) + "/" + filename;
		}
		return filePath;
	}
	
	public static String getFileHeader(boolean iscp) {
		String headers;
		if (iscp) {
			//商户数据
			headers =  "'交易时间', '应用名称', '商户名称', '支付通道', '订单号', '流水号', '交易额', '退款金额', '通道手续费率', '通道手续费', '平台服务费率', '平台服务费', '账单金额','平台应付','通道应付'";
		}else{
			//财务数据
			headers = "'交易时间', '应用名称', '商户名称', '签约主体', '支付通道', '订单号', '流水号', '交易额', '退款金额', '通道手续费率', '通道手续费', '通道差额', '平台服务费率', '平台服务费', '账单金额', '通道返款', '平台应收款', '进件费 '";
		}

		return headers;
	}
	
	protected static boolean isCpExportData(String topicparam) {
		String[] topicParamArray = topicparam.split("#");
		return (topicParamArray.length >= 4 && EnumBdSettleFileCode.CP_TYPE.getCode().equals(Integer.parseInt(topicParamArray[3])));
	}

}
