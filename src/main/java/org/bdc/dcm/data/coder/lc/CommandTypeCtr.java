package org.bdc.dcm.data.coder.lc;

import org.bdc.dcm.vo.DataPack;
import org.bdc.dcm.data.coder.lc.vo.CommLcParam;

/**
 * 	只处理 command 以后的数据 不包含command 不包含 校验和 </br>
 * 	<table>
 * 		<tr>
 *			<td>Header</td>
 *			<td>Type</td>
 *			<td>Len</td>
 *			<td>Command</td>
 *			<td>64bitAddr</td>
 *			<td>序号</td>
 *			<td>数据域</td>
 *			<td>CRC</td>
 *		</tr>
 *		<tr>
 *			<td>FEH</td>
 *			<td>A5H</td>
 *			<td>XXH</td>
 *			<td>XXH</td>
 *			<td>8个字节</td>
 *			<td>XXH</td>
 *			<td>Modbus封包</td>
 *			<td>XXH</td>
 *		</tr>
 *	</table>
 * 	@author lizhehong
 */
public interface CommandTypeCtr {

	DataPack mapTo(CommLcParam param);

}
