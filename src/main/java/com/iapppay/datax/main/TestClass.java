package com.iapppay.datax.main;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.alibaba.datax.core.Engine;
import com.iapppay.datax.utils.ClassLoaderUtil;

/**
 * 
 * @className: TestClass
 * @classDescription:
 * @author lishiqiang
 * @create_date: 2019年7月4日 下午2:49:33
 * @update_date:
 */
public class TestClass {

	public static void main(String[] args) throws Throwable {
		ClassLoader classLoader = ClassLoaderUtil.getClassLoader();
		System.out.println(">>>>" + classLoader.getResource("").getPath());
		System.out.println(getCurrentClasspath());
		String dataxPath = System.getProperty("user.dir");
		System.out.println("===========" + dataxPath);
		System.setProperty("datax.home", dataxPath);
		System.setProperty("now", new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(new Date()));// 替换job中的占位符
		String[] datxArgs = { "-job", dataxPath + "/job/job.json", "-mode", "standalone", "-jobid",
				"-1" };
		Engine.entry(datxArgs);
	}

	public static String getCurrentClasspath() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		String currentClasspath = classLoader.getResource("").getPath(); // 当前操作系统
		String osName = System.getProperty("os.name");
		if (osName.startsWith("Windows")) { // 删除path中最前面的/
			currentClasspath = currentClasspath.substring(1);
		}
		return currentClasspath;
	}

	public static void testOptions(String[] args) {
		Options options = new Options();
		Option opt = new Option("xx", "help1", true, "Print help");
		opt.setRequired(false);
		options.addOption(opt);

		opt = new Option("c", "configFile", true, "Name server config properties file");
		opt.setRequired(false);
		options.addOption(opt);

		opt = new Option("p", "printConfigItem", false, "Print all config item");
		opt.setRequired(false);
		options.addOption(opt);

		HelpFormatter hf = new HelpFormatter();
		hf.setWidth(110);
		CommandLine commandLine = null;
		CommandLineParser parser = new PosixParser();
		try {
			commandLine = parser.parse(options, args);
			if (commandLine.hasOption('h')) {
				// 打印使用帮助
				hf.printHelp("TESTAPP", options, true);
			}

			// 打印opts的名称和值
			System.out.println("--------------------------------------");
			Option[] opts = commandLine.getOptions();
			if (opts != null) {
				for (Option opt1 : opts) {
					String name = opt1.getLongOpt();
					String value = commandLine.getOptionValue(name);
					System.out.println(name + "=>" + value);
				}
			}
		} catch (ParseException e) {
			hf.printHelp("testApp", options, true);
		}
	}

}
