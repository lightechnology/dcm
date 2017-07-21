package org.bdc.dcm.comm.callback;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.tools.Public;

import io.netty.buffer.ByteBuf;
/**
 * 读 bytes 字节做固定验证
 * @author Administrator
 *
 */
public class FixBytesCheckInterceptor implements ZoneInterceptor {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private byte[] bytes;

	
	public FixBytesCheckInterceptor(byte[] bytes) {
		super();
		this.bytes = bytes;
	}

	@Override
	public boolean invoke(ByteBuf in) {
		byte[] dst = new byte[bytes.length];
		in.readBytes(dst);
		boolean flag = Arrays.equals(dst, bytes);
		if(!flag)
			logger.error("当前解析的头：{},用户需求头：{}",Public.byte2hex(dst),Public.byte2hex(bytes));
		return flag;
	}

}
