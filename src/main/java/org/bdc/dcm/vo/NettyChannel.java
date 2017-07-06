package org.bdc.dcm.vo;

import java.io.Serializable;
import java.net.SocketAddress;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyChannel implements Serializable {
    
    final static Logger logger = LoggerFactory.getLogger(NettyChannel.class);

    private static final long serialVersionUID = -6874684025638843184L;
    
    private SocketAddress socketAddress;
    
    private Set<String> macs;
    
    public NettyChannel() {
        this.macs = new ConcurrentSkipListSet<String>();
    }

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }

    public void setSocketAddress(SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }

    public Set<String> getMacs() {
        return macs;
    }

    public void addMac(String mac) {
        macs.add(mac);
    }
    
    public void removeMac(String mac) {
        macs.remove(mac);
    }
}
