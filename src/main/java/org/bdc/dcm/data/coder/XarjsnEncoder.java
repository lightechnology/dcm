package org.bdc.dcm.data.coder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.bdc.dcm.conf.ComConf;
import org.bdc.dcm.data.coder.intf.DataEncoder;
import org.bdc.dcm.netty.NettyBoot;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.netty.channel.ChannelHandlerContext;

public class XarjsnEncoder implements DataEncoder<String> {

	final static Logger logger = LoggerFactory.getLogger(XarjsnEncoder.class);

	private NettyBoot nettyBoot;

	public XarjsnEncoder(NettyBoot nettyBoot) {
		this.nettyBoot = nettyBoot;
	}

	@Override
	public String package2Data(ChannelHandlerContext ctx, DataPack msg) {
		Server server = nettyBoot.getServer();
		Map<String, String> sdkInfo = server.getSdkUserInfo();
		try {
			Template json1 = ComConf.getInstance().getFmkTemp("json0.ftl");
			StringWriter stringWriter = new StringWriter();
			BufferedWriter writer = new BufferedWriter(stringWriter);
			json1.process(null, writer);
			String reader = new String(stringWriter.toString());
			writer.flush();
			writer.close();
		} catch (TemplateException | IOException e) {
			if (logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
			} else
				e.printStackTrace();
		}
		return null;
	}

}
