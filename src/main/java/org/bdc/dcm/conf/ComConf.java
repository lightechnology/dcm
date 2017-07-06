package org.bdc.dcm.conf;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

import org.bdc.dcm.vo.NettyChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateNotFoundException;
import io.netty.util.AttributeKey;

public class ComConf {
	
	public static final String DEVICE_KEY_LIST = "device_key_list";
	
	final static Logger logger = LoggerFactory.getLogger(ComConf.class);
	
	private Properties pps;
	private Configuration freemarkeConf;

	private static final ComConf config = new ComConf();
    
    public static final AttributeKey<NettyChannel> NETTY_CHANNEL_KEY = AttributeKey.valueOf("netty.channel");
	
	public final String CHARSET;
	public final int DATAPACK_MAXLENGTH;
	public final int DEVICE_OUTLINE_TIME;
	public final int THREAD_POOL_SIZE; 

	private ComConf() {
		pps = new Properties();
		try {
			pps.load(ComConf.class.getClassLoader().getResourceAsStream("netty.properties"));
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {
				logger.error(e.getMessage(), e);
			} else {
				e.printStackTrace();
			}
		}
		CHARSET = pps.getProperty("charset", "UTF-8");
		int dmt = Public.objToInt(pps.getProperty("datapack_maxlength", "128"));
		if (0 > dmt) DATAPACK_MAXLENGTH = 128;
		else DATAPACK_MAXLENGTH = dmt;
		dmt = Public.objToInt(pps.getProperty("device_outline_time", "60"));
		if (0 > dmt) DEVICE_OUTLINE_TIME = 60;
		else DEVICE_OUTLINE_TIME = dmt;
		dmt = Public.objToInt(pps.getProperty("thread_pool_size", "8"));
		if (0 > dmt) THREAD_POOL_SIZE = 8;
		else THREAD_POOL_SIZE = dmt;
		freemarkeConf = new Configuration(Configuration.VERSION_2_3_23);
		try {
			freemarkeConf.setDirectoryForTemplateLoading(new File(ComConf.class
					.getClassLoader()
					.getResource("org/bdc/dcm/data/fmktemp/json").toURI()));
			freemarkeConf.setObjectWrapper(new DefaultObjectWrapper(
					Configuration.VERSION_2_3_23));
			freemarkeConf.setDefaultEncoding(CHARSET);
		} catch (IOException | URISyntaxException e) {
			if (logger.isErrorEnabled()) {
				logger.error(e.getMessage(), e);
			} else
				e.printStackTrace();
		}
	}

	public static ComConf getInstance() {
		return config;
	}
	
	public String getProperty(String key) {
		if (null == pps) return null;
		return pps.getProperty(key);
	}
	
	public String getProperty(String key, String def) {
		if (null == pps) return null;
		return pps.getProperty(key, def);
	}
	
	public void setProperty(String key, String value) {
		pps.setProperty(key, value);
	}
	
	public Template getFmkTemp(String name) throws TemplateNotFoundException,
			MalformedTemplateNameException, ParseException, IOException {
		return freemarkeConf.getTemplate(name);
	}
	
}
