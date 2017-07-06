package org.bdc.dcm.data.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bdc.dcm.conf.IntfConf;
import org.bdc.dcm.data.coder.intf.DataEncoder;
import org.bdc.dcm.intf.DataTabConf;
import org.bdc.dcm.netty.klv.KlvTypeConvert;
import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.vo.DataTab;
import org.bdc.dcm.vo.e.DataPackType;

import com.util.tools.Public;

public class KlvEncoder implements DataEncoder<ByteBuf> {
	
	private final DataTabConf dataTabConf;

	public KlvEncoder() {
		this.dataTabConf = IntfConf.getDataTabConf();
	}
	
	// 通过DataTypeConf接口获取编码规则
	@Override
	public ByteBuf package2Data(ChannelHandlerContext ctx, DataPack msg) {
        ByteBuf src = null;
        if (DataPackType.HeartBeat == msg.getDataPackType()) {
            byte[] data = Public.hexString2bytes("8F 07 00 00 78 02 48 42 5C 71 8E");
            src = ctx.alloc().buffer(data.length);
            src.writeBytes(data);
            return src;
        } else {
            List<Byte> xlt = new ArrayList<Byte>();
            List<DataTab> dataTabList = dataTabConf.getDataTabConf("klv");
            xlt.add((byte) 0x8F);
            xlt.add((byte) msg.getDataPackType().ordinal());
            Map<String, Object> data = msg.getData();
            for (Iterator<Map.Entry<String, Object>> ite = data.entrySet().iterator(); ite.hasNext();) {
                Entry<String, Object> entry = (Entry<String, Object>) ite.next();
                int id = Public.objToInt(entry.getKey());
                if (0 < id) {
                    @SuppressWarnings("unchecked")
                    List<Object> vl = (List<Object>) entry.getValue();
                    if (1 < vl.size()) 
                        xlt.addAll(encodeValue(id, vl.get(1), dataTabList));
                }
            }
            int length = xlt.size() + 1;
            byte[] len = Public.int2Bytes(length, 2);
            xlt.add(1, len[1]);
            xlt.add(1, len[0]);
            byte[] body = new byte[xlt.size() - 1];
            for (int i = 0; i < xlt.size() - 1; i++) {
                body[i] = xlt.get(i + 1);
            }
            byte[] crc = Public.crc16(body);
            xlt.add(crc[0]);
            xlt.add(crc[1]);
            xlt.add((byte) 0x8E);
            src = ctx.alloc().buffer(xlt.size());
            for (int i = 0; i < xlt.size(); i++) {
                src.writeByte(xlt.get(i));
            }
            return src;
        }
	}
    
    private List<Byte> encodeValue(int id, Object value,
            List<DataTab> dataTabList) {
        List<Byte> xlt = new ArrayList<Byte>();
        for (int i = 0; i < dataTabList.size(); i++) {
            DataTab dataTab = dataTabList.get(i);
            if (id == dataTab.getId()) {
                xlt.add((byte) id);
                List<Byte> data = KlvTypeConvert.convertTypeValue2ByteBuf(
                        dataTab.getForm(), value);
                xlt.add((byte) data.size());
                xlt.addAll(data);
                return xlt;
            }
        }
        xlt.add((byte) id);
        byte data[] = Public.serializer(value);
        xlt.add((byte) data.length);
        for (int i = 0; i < data.length; i++) {
            xlt.add(data[i]);
        }
        return xlt;
    }
	
}
