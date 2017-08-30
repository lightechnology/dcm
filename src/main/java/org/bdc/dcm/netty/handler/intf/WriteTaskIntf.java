package org.bdc.dcm.netty.handler.intf;

public interface WriteTaskIntf {
	/**
	 * 写任务的具体实现接口
	 * @throws Exception
	 */
	public void invoke() throws Exception;
}
