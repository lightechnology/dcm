package org.bdc.dcm.netty;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;

public interface HyChannelGroupIntf<Indentity> extends ChannelGroup {

	public boolean add(Channel channel, Indentity indentity);
	
	public boolean removeChannel(Indentity o);

	public Channel findByIndetity(Indentity indentity);
}
