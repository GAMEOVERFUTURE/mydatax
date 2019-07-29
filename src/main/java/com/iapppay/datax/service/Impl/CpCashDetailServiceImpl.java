package com.iapppay.datax.service.Impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.datax.core.Engine;
import com.iapppay.datax.service.CpcashConfiguration;
import com.iapppay.datax.service.CpcashDetailService;
import com.iapppay.operating.common.util.ZipUtils;
import com.iapppay.operating.constant.EnumBdSettleFileCode;
import com.iapppay.operating.constant.EnumCpidCode;
import com.iapppay.operating.model.sp.dao.IBalaNewBillinfoDao;
import com.iapppay.operating.model.sp.dao.IBalaNewCpamtDao;
import com.iapppay.operating.model.sp.dao.IBdCpcashDistDao;
import com.iapppay.operating.model.sp.dao.IBdSettleFileDao;
import com.iapppay.operating.model.sp.domain.BalaNewBillinfo;
import com.iapppay.operating.model.sp.domain.BalaNewCpamt;
import com.iapppay.operating.model.sp.domain.BdCpcashDist;
import com.iapppay.operating.model.sp.domain.BdSettleFile;
import com.iapppay.operating.model.sp.domain.query.BalaNewBillinfoQuery;
import com.iapppay.operating.model.sp.domain.query.BalaNewCpamtQuery;
import com.iapppay.operating.model.sp.domain.query.BdCpcashDistQuery;

/**
 * 
 * @className: CpCashDetailServiceImpl
 * @classDescription:
 * @author lishiqiang
 * @create_date: 2019年7月5日 下午3:49:30
 * @update_date:
 */
@Component
public class CpCashDetailServiceImpl extends CpcashConfiguration implements CpcashDetailService {
	private Logger log = LoggerFactory.getLogger(CpCashDetailServiceImpl.class);

	@Resource
	private IBdSettleFileDao bdSettleFileDao;

	@Resource
	private IBdCpcashDistDao bdCpcashDistDao;

	/**
	 * 账期商户付款表
	 */
	@Resource
	private IBalaNewCpamtDao balaNewCpamtDao;

	/**
	 * 账期信息表
	 */
	@Resource
	private IBalaNewBillinfoDao balaNewBillinfoDao;

	private static final String TOPICPARAM_SPLIT = "#";

	private static final String FILENAME_SPLIT = "_";

	private static String dataxPath = currentProjectPath();


	/**
	 * 初始化数据
	 */
	public void init() {
		System.setProperty("datax.home", dataxPath);
		System.setProperty("mongoDBAddress", getMongoDbAddress());
		System.setProperty("userName", this.userName);
		System.setProperty("userPassword", this.password);
		System.setProperty("dbName", this.dbName);
		System.setProperty("query", this.query);
		System.setProperty("month", this.month);
		System.setProperty("filepath", this.filepath);
		System.setProperty("fileName", this.filename);
		System.setProperty("fileHeader", this.fileHeader);
		System.setProperty("billno", this.billno);
		System.setProperty("isCp", this.isCp);
	}


	@Override
	public void generatorCpCashDetailFile() throws Exception {
		List<BdSettleFile> bdSettleFileList = bdSettleFileDao.findSettleFile();
		// 账期编号，商户ID,下载类型
		if (bdSettleFileList != null && bdSettleFileList.size() > 0) {
			bdSettleFileList.stream().forEach((bdSettleFile) -> {
				if (bdSettleFile.getTopicparam() != null) {
					try {
						handlerCpCashDetail(bdSettleFile);
					} catch (Exception e) {
						log.error("topicParam[%s],topictype[%s]生成账期明细文件失败:{}", bdSettleFile.getTopicparam(),
								bdSettleFile.getTopictype(), e.getMessage(), e);
					}
				}
			});
		}
	}

	private void handlerCpCashDetail(BdSettleFile bdSettleFile) throws Exception {
		int filestate = 0;
		boolean result = true;
		String[] topicParam = bdSettleFile.getTopicparam().split(TOPICPARAM_SPLIT);
		String billno = topicParam[0];
		String cpid = topicParam[1];
		String cptype = topicParam[2];
		this.perpare(bdSettleFile, billno, cpid, cptype);

		// 初始化
		this.init();

		// 调用之前更新状态
		Map<String, Object> map = new HashMap<>();
		filestate = EnumBdSettleFileCode.PROCESS_FILE.getCode();
		makeSettleFileMap(map, bdSettleFile.getId(), filestate, null);
		bdSettleFileDao.updatePartById(map);

		// 调用datax引擎
		String[] datxArgs = { "-job", dataxPath + "/job/cpcashdetail.json", "-mode", "standalone", "-jobid", "-1" };
		try {
			Engine.entry(datxArgs);
		} catch (Throwable e) {
			result = false;
			filestate = EnumBdSettleFileCode.FAIL_FILE.getCode();
			log.error("账期[%s],商户ID[%s],商户类型[{}],datax生成账期明细文件失败:{}", billno, cpid, cptype, e.getMessage(), e);
		}

		// 更新生成文件路径及状态
		String filepathzip = null;
		if (result) {
			filestate = EnumBdSettleFileCode.SUCCESS_FILE.getCode();
			filepathzip = generateZip(filepath,
					filename + (isCpExportData(bdSettleFile.getTopicparam()) == true ? "_cp" : ""));
		}
		makeSettleFileMap(map, bdSettleFile.getId(), filestate, filepathzip);
		bdSettleFileDao.updatePartById(map);
	}

	/**
	 * 准备数据
	 * 
	 * @param bdSettleFile
	 * @param billno
	 * @param cpid
	 * @param cptype
	 */
	private void perpare(BdSettleFile bdSettleFile, String billno, String cpid, String cptype) {
		this.month = billno.substring(1, 7);
		this.fileHeader = getFileHeader(isCpExportData(bdSettleFile.getTopicparam()));
		this.filename = billno.concat(FILENAME_SPLIT).concat(cpid).concat(FILENAME_SPLIT).concat(cptype);
		this.filepath = getServerFilePath(bdSettleFile.getTopictype(), this.filename);
		try {
			this.query = getBdCpcashDetailQuery(billno, Integer.parseInt(cpid), Integer.parseInt(cptype));
		} catch (Exception e) {
			log.error("本次生成的账期[%s],商户ID[%s],商户类型[{}]组装查询条件失败：[%s]", billno, cpid, cptype, e.getMessage(), e);
		}
		this.billno = billno;
		this.isCp = String.valueOf(isCpExportData(bdSettleFile.getTopicparam()));
		log.info("本次生成的账期[%s],商户ID[%s],商户类型[{}]", billno, cpid, cptype);
	}

	/**
	 * 更新bdSettleFileMap
	 * 
	 * @param map
	 * @param id
	 * @param filestate
	 */
	private void makeSettleFileMap(Map<String, Object> map, Integer id, int filestate, String filepath) {
		map.clear();
		map.put("id", id);
		map.put("filestate", filestate);
		map.put("filepath", filepath);
		map.put("updatetime", new Date());
	}

	/**
	 * 封装查询账单明细数据对象
	 * 
	 * @param billno
	 * @param cpid
	 * @param cpType
	 * @return
	 * @throws Exception
	 */
	private String getBdCpcashDetailQuery(String billno, Integer cpid, Integer cpType) throws Exception {
		StringBuffer query = new StringBuffer();
		// 添加日结商户月虚账单
		if (billno.startsWith(MONTH_SETTLEMENT)) {
			List<String> billnoList = queryDaySettleCpMonthBill(billno, cpid);
			query.append(this.dealAssembleList("billno", billnoList));
		} else {
			query.append("'billno': '" + billno + "',");
		}

		String appidacid = "";
		if (Integer.valueOf(EnumCpidCode.APP_CPID.getCode()).equals(cpType)) { // 应用所属商户
			query.append("'cpid': " + cpid + ",");
		} else if (Integer.valueOf(EnumCpidCode.MAIN_SUB_CPID.getCode()).equals(cpType)) { // 主子商户
			query.append("'maincpid': " + cpid + ",");
		} else if (Integer.valueOf(EnumCpidCode.DIST_CPID.getCode()).equals(cpType)) { // 分润商户
			BdCpcashDistQuery cpDistQuery = new BdCpcashDistQuery();
			cpDistQuery.setBillno(billno);
			cpDistQuery.setDistcpid(cpid);
			List<BdCpcashDist> distDataList = bdCpcashDistDao.queryList(cpDistQuery);
			if (CollectionUtils.isEmpty(distDataList)) {
				return null;
			}

			// 2.1 组装应用和渠道
			List<String> appidList = new ArrayList<String>();
			List<String> acidList = new ArrayList<String>();
			Set<String> appidacidList = new HashSet<String>();
			for (BdCpcashDist dist : distDataList) {
				appidList.add(dist.getAppid());
				acidList.add(dist.getAcid());
				appidacidList.add(dist.getBillno() + "#" + dist.getAppid() + "#" + dist.getAcid());
			}
			query.append(this.dealAssembleList("appid", appidList));
			query.append(this.dealAssembleList("acid", acidList));
			for (String bean : appidacidList) {
				appidacid += bean + ",";
			}
		}
		if (appidacid.length() > 1) {
			appidacid = appidacid.substring(0, appidacid.length() - 1);
		}
		System.setProperty("array", appidacid);

		if (query.length() > 1) {
			query = query.deleteCharAt(query.length() - 1);
		}

		return query.toString();
	}

	/**
	 * 组装Mongodb语法
	 */
	private StringBuffer dealAssembleList(String type, List<String> lists) {
		StringBuffer result = new StringBuffer();
		if (lists.isEmpty()) {
			return result;
		}
		result.append("'" + type + "': {$in:[");
		for (String bean : lists) {
			result.append("'" + bean + "',");
		}
		result.deleteCharAt(result.length() - 1);
		result.append("]},");

		return result;
	}

	/**
	 * 添加日结商户月虚账单
	 * 
	 * @param queryObj
	 * @param bean
	 * @param index
	 */
	private List<String> queryDaySettleCpMonthBill(String billno, Integer cpid) {
		if (billno == null || cpid == null) {
			return Collections.emptyList();
		}
		// 查月账单的虚拟列表
		BalaNewCpamtQuery cpamtQuery = new BalaNewCpamtQuery();
		cpamtQuery.setBillno(billno);
		cpamtQuery.setCpid(cpid);
		cpamtQuery.setBilltype(0);
		cpamtQuery.setPage(null);
		List<BalaNewCpamt> cpamtList = balaNewCpamtDao.queryList(cpamtQuery);
		if (CollectionUtils.isEmpty(cpamtList)) {
			return Collections.emptyList();
		}

		// 查虚拟列表时间范围
		return this.qryDayBillnos(billno);
	}

	/**
	 * 根据月账期查询出所有日账期的账单数据
	 * 
	 * @param billno
	 * @return
	 */
	public List<String> qryDayBillnos(String billno) {
		BalaNewBillinfo billinfo = balaNewBillinfoDao.queryBillInfo(billno);
		if (null == billinfo) {
			log.error("账期交易明细：根据billno = (" + billno + ")在balaNewBillinfo表中查不到相关数据！");
			return Collections.emptyList();
		}

		// 根据天列表组装账期列表
		BalaNewBillinfoQuery billinfoQuery = new BalaNewBillinfoQuery();
		billinfoQuery.setStartDate(billinfo.getBegisettdate());
		billinfoQuery.setEndDate(billinfo.getEndsettdate());
		billinfoQuery.setIsMonth(Integer.parseInt(this.BALA_BILL_TYPE_DAY));
		List<String> resultList = balaNewBillinfoDao.getBillno(billinfoQuery);
		if (CollectionUtils.isEmpty(resultList)) {
			log.error("账期交易明细：根据begisettdate >= " + billinfo.getBegisettdate() + ", endsettdate <= "
					+ billinfo.getEndsettdate() + ",在balaNewBillinfo表中查不到相关数据！");
			return Collections.emptyList();
		}
		return resultList;
	}

	/**
	 * 压缩成zip文件
	 * 
	 * @param fileList
	 */
	private String generateZip(String filePath, String fileName) throws Exception {
		if (StringUtils.isEmpty(filePath)) {
			return null;
		}
		// 判断文件夹
		File file = new File(filePath);
		if (!(file.exists() && file.isDirectory())) {
			return null;
		}
		// 获取文件夹下所有文件
		File[] files = file.listFiles();
		List<File> fileList = new ArrayList<File>();
		for (File f : files) {
			if (f.getName().endsWith(".zip")) {
				continue;
			}
			fileList.add(f);
		}
		File zipFile = new File(this.fileBasePath + "/" + fileName + ".zip");
		ZipUtils.zipFiles(fileList, zipFile);
		zipFile.createNewFile();
		// 判断压缩后的文件大小
		if (zipFile.exists() && zipFile.length() < 1) {
			return null;
		}
		// 删除文件
		for (File f : fileList) {
			if (f.getName().endsWith(".zip")) {
				continue;
			}
			f.delete();
		}
		file.delete();
		return fileName + ".zip";
	}
	
}
