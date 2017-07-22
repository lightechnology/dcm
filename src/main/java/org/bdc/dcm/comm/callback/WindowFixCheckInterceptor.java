package org.bdc.dcm.comm.callback;

/**
 * 窗口固定拦截器
 * 用户获取协议内已知的数据长度数据包
 * 次过滤器不做read 只做get 保持主栈readerIndex
 * @author lzhe
 *
 */
public interface WindowFixCheckInterceptor extends CheckInterceptor{

	/**
	 * 得到窗口的大小
	 * @return
	 */
	public int getWidth();
}
