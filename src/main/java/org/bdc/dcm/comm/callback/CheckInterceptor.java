package org.bdc.dcm.comm.callback;

import io.netty.buffer.ByteBuf;
/**
 * 拦截器：
 * @author 李哲弘
 *
 */
public interface CheckInterceptor {
	/**
	 * 返回false 说明该帧到可读位置全部抛弃
	 * @param in
	 * @return
	 */
	public boolean invoke(ByteBuf in);
	
}
