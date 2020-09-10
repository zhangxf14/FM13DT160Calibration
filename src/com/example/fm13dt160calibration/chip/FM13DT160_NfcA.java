package com.example.fm13dt160calibration.chip;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.R.integer;
import android.annotation.SuppressLint;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcV;
import android.util.Log;

@SuppressLint("SimpleDateFormat") 
public class FM13DT160_NfcA {
	
	public static class SystemInformation {
		public byte[] uid;
		boolean dsfidAvailable;
		public byte dsfid;
		boolean afiAvailable;
		public byte afi;
		boolean memorySizeAvailable;
		public int blockSize;
		public int numBlocks;
		boolean icReferenceAvailable;
		public byte icReference;
	}
			
	/** ISO15693 mandatory inventory command command code. */
	static final byte INVENTORY_CC = (byte) 0x01;
	/** ISO15693 mandatory stay quiet command command code. */
	static final byte STAY_QUIET_CC = (byte) 0x02;
	/** ISO15693 optional read single block command command code. */
	static final byte READ_SINGLE_BLOCK_CC = (byte) 0x20;
	/** ISO15693 optional write single block command command code. */
	static final byte WRITE_SINGLE_BLOCK_CC = (byte) 0x21;
	/** ISO15693 optional lock block command command code. */
	static final byte LOCK_BLOCK_CC = (byte) 0x22;
	/** ISO15693 optional read multiple blocks command command code. */
	static final byte READ_MULTIPLE_BLOCKS_CC = (byte) 0x23;
	/** ISO15693 optional write multiple blocks command command code. */
	static final byte WRITE_MULTIPLE_BLOCKS_CC = (byte) 0x24;
	/** ISO15693 optional select command command code. */
	static final byte SELECT_CC = (byte) 0x25;
	/** ISO15693 optional reset to ready command command code. */
	static final byte RESET_TO_READY = (byte) 0x26;
	/** ISO15693 optional write AFI command command code. */
	static final byte WRITE_AFI_CC = (byte) 0x27;
	/** ISO15693 optional lock AFI command command code. */
	static final byte LOCK_AFI_CC = (byte) 0x28;
	/** ISO15693 optional write DSFID command command code. */
	static final byte WRITE_DSFID_CC = (byte) 0x29;
	/** ISO15693 optional lock DSFID command command code. */
	static final byte LOCK_DSFID_CC = (byte) 0x2A;
	/** ISO15693 optional get system information command command code. */
	static final byte GET_SYSTEM_INFORMATION_CC = (byte) 0x2B;
	/** ISO15693 optional get multiple block security status command command code. */ 
	static final byte GET_MULTIPLE_BLOCK_SECURITY_STATUS_CC = (byte) 0x2C;
	
	
	/** ISO15603 reply error flag. */
	static final byte ISO15693_ERROR_FLAG = 0x01;
	
	/**FM13DT160 定制指令*/
	/** read memory*/
	static final byte CUSTOMER_READ_MEMORY=(byte) 0xB1;
	
	/** get random*/
	static final byte CUSTOMER_GET_RANDOM=(byte) 0xB2;
	
	/** write memory*/
	static final byte CUSTOMER_WRITE_MEMORY=(byte) 0xB3;
	
	/** auth*/
	static final byte CUSTOMER_AUTH=(byte) 0xB4;
	
	/** get temperature*/
	static final byte CUSTOMER_GET_TEMPERATURE=(byte) 0xC0;
	
	/** strat/stop RTC*/
	static final byte CUSTOMER_START_STOP_RTC=(byte) 0xC2;
	
	/** deep sleep*/
	static final byte CUSTOMER_DEEP_SLEEP=(byte) 0xC3;
	
	/** wakeup*/
	static final byte CUSTOMER_WAKEUP=(byte) 0xC4;
	
	/** write reg*/
	static final byte CUSTOMER_WRITE_REG=(byte) 0xC5;
	
	/** read reg*/
	static final byte CUSTOMER_READ_REG=(byte) 0xC6;
	
	/** led ctrl*/
	static final byte CUSTOMER_LED_CTRL=(byte) 0xC9;
	
	/** op mode chk*/
	static final byte CUSTOMER_OP_MODE_CHK=(byte) 0xCF;
	
	/** field strength chk*/
	static final byte CUSTOMER_FIELD_STRENGTH_CHK=(byte) 0xD0;
	
	
	static final String TAG = "Fm13dt";
	protected NfcA nfca;
	
	public FM13DT160_NfcA(NfcA nfca) {
		super();
		this.nfca = nfca;
	}	
		
	/**
	 * ISO15693 read single block command.
	 * 
	 * @param blockNumber Block number of the block to read.
	 * @return Content of tag memory block \a blockNumber.
	 * @throws IOException
	 */
	public byte[] readSingleBlock(int blockNumber) throws IOException {
		if ((blockNumber < 0) || (blockNumber > 255))
			throw new IllegalArgumentException("block number must be within 0-255");
		byte[] parameter = new byte[1];
		parameter[0] = (byte) (blockNumber & 0xFF);
		byte result[] = transceive(READ_SINGLE_BLOCK_CC, parameter);
		return result;
	}
	
	/**
	 * ISO15693 read multiple blocks command.
	 * 
	 * @param firstBlock First block to read.
	 * @param numBlocks Number of blocks to read.
	 * @return 
	 * @throws IOException
	 */
	public byte[] readMultipleBlocks(int firstBlock, int numBlocks) throws IOException {
		if ((firstBlock < 0) || (firstBlock > 255))
			throw new IllegalArgumentException("Start block must be within 0-255");
		if (numBlocks < 0)
			throw new IllegalArgumentException("Number of blocks to read must be within 0-255");
		if ((numBlocks + firstBlock) > 256)
			throw new IllegalArgumentException("Read length exceeds last block");
		
		byte parameter[] = new byte[2];
		parameter[0] = (byte) (firstBlock & 0xFF);
		parameter[1] = (byte) ((numBlocks-1) & 0xFF);
		byte result[] = transceive(READ_MULTIPLE_BLOCKS_CC, parameter);
		return result;
	}
	
	/**
	 * ISO15693 write single block command.
	 * 
	 * @param blockNumber Block number of the block to write blockData.
	 * @return flag.
	 * @throws IOException
	 */
	public byte[] writeSingleBlock(int blockNumber,byte[] blockData) throws IOException {
		if ((blockNumber < 0) || (blockNumber > 255))
			throw new IllegalArgumentException("block number must be within 0-255");
		byte[] parameter = new byte[5];
		parameter[0] = (byte) (blockNumber & 0xFF);
		parameter[1] = (byte) (blockData[0] & 0xFF);
		parameter[2] = (byte) (blockData[1] & 0xFF);
		parameter[3] = (byte) (blockData[2] & 0xFF);
		parameter[4] = (byte) (blockData[3] & 0xFF);
		byte result[] = transceive(WRITE_SINGLE_BLOCK_CC, parameter);
		return result;
	}
	/**
	 * get system information
	 * @return
	 * @throws IOException
	 */
	public SystemInformation getSystemInformation() throws IOException {
		byte result[] = transceive(GET_SYSTEM_INFORMATION_CC);
		checkResponse(15, result);
		int resultReadIndex = 0;
		SystemInformation systemInformation = new SystemInformation();
		systemInformation.uid = new byte[8];
		System.arraycopy(result, 2, systemInformation.uid, 0, 8);
//		resultReadIndex = 10;
//		if ((result[0] & 0x01) == 0x01) {
//			systemInformation.dsfidAvailable = true;
//			systemInformation.dsfid = result[resultReadIndex++];
//		} else {
//			systemInformation.dsfidAvailable = false;
//		}
//		
//		if ((result[0] & 0x02) == 0x02) {
//			systemInformation.afi = result[resultReadIndex++];
//		} else {
//			systemInformation.afiAvailable = false;
//		}
//		
//		if ((result[0] & 0x04) == 0x04) {
//			systemInformation.memorySizeAvailable = true;
//			systemInformation.blockSize = ((int) result[resultReadIndex++]) & 0x1F;
//			systemInformation.numBlocks = ((int) result[resultReadIndex++]) & 0xFF;
//		} else {
//			systemInformation.memorySizeAvailable = false;
//		}
//		
//		if ((result[0] & 0x08) == 0x08) {
//			systemInformation.icReferenceAvailable = true;
//			systemInformation.icReference = result[resultReadIndex++];
//		}
		
		systemInformation.dsfidAvailable = true;
		systemInformation.dsfid = result[10];
		systemInformation.afi = result[11];
		systemInformation.blockSize = ((int) result[13]) & 0x1F;
		systemInformation.numBlocks = ((int) result[12]) & 0xFF;
		systemInformation.icReferenceAvailable = true;
		systemInformation.icReference = result[14];
		
		return systemInformation;
	}
	
	// add by zxf 2018-7-10
	/**
	 * read memory 定制指令，可用于�?次�?�读取大量温度数据，�?小单位为byte�?
	 * @param firstAddress
	 * @param numOfByte
	 * @return
	 * @throws IOException
	 */
	public byte[]  readMemory(int firstAddress,int numOfByte) throws IOException{
		byte[] parameter=new byte[4+1];//1 byte Rev.
		parameter[0]=(byte) (firstAddress>>8 & 0xFF);
		parameter[1]=(byte) (firstAddress & 0xFF);
		parameter[2]=(byte) (numOfByte>>8 & 0xFF);
		parameter[3]=(byte) (numOfByte & 0xFF);		
		byte[] result=transceive(CUSTOMER_READ_MEMORY, parameter);
		return result;
	}
    /**
     * get random 用于获取芯片�?32bits随机数�??
     * @return
     * @throws IOException
     */
	public byte[] getRandom() throws IOException{
		byte[] parameter=new byte[5];//5 byte Rev.
		byte[] result=transceive(CUSTOMER_GET_RANDOM, parameter);
		return result;
	}
	/**
	 * wirte memory 可访问所有EEPROM空间，但具体权限受工作模式�?�芯片配置等因素控制。写�?小单位为byte�?
	 * @param firstAddress
	 * @param dataNum
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public byte[] writeMemory(int firstAddress,int dataNum,byte[] data) throws IOException {
		if (dataNum+1>4) {
			throw new IllegalArgumentException("data number within 0-3");	
		}
		byte[] parameter=new byte[3+dataNum+1+2]; //2 byte Rev.
		parameter[0]= (byte) (firstAddress >>8 & 0xFF);
		parameter[1]= (byte) (firstAddress & 0xFF);
		parameter[2]= (byte) dataNum;
		parameter[3]= 0x00;
		parameter[4]= 0x00;
		System.arraycopy(data, 0, parameter, 5,data.length);
		byte[] result=transceive(CUSTOMER_WRITE_MEMORY, parameter);
		return result;
	}
	/**
	 * auth 用于访问控制权限，可对user password，stop rtc password进行校验�?
	 * @param cmd_cfg
	 * @param pwData
	 * @return
	 * @throws IOException
	 */
	public byte[] auth(int cmd_cfg,byte[] pwData) throws IOException{
		byte[] parameter=new byte[5];
		parameter[0]=(byte) cmd_cfg;
		System.arraycopy(pwData, 0, parameter, 1,4);
		byte[] result=transceive(CUSTOMER_AUTH, parameter);
		return result;
	}
	/**
	 * get temperature 单次测温指令，芯片需接收两次指令以完成单次测温流程，第一次指令用于启动单次测温，
	 * 第二次指令用于会发结果，两次指令之间间隔�?大于300ms�?
	 * @param cmd_cfg
	 * @param blockAddr
	 * @return
	 * @throws IOException
	 */
	public byte[] getTemperature(int cmd_cfg,int blockAddr) throws IOException {
		byte[] parameter=new byte[2+3];//预留3个字节Rev.
		parameter[0]=(byte) cmd_cfg;
		parameter[1]=(byte) blockAddr;
		byte[] result=transceive(CUSTOMER_GET_TEMPERATURE, parameter);
		return result;
	}
	/**
	 * start/stop RTC 用于启动RTC连续测温流程，或停止RTC连续测温流程，停止RTC�?要进行密码校验�??
	 * @param cmd_cfg
	 * @param userRev
	 * @return
	 * @throws IOException
	 */
	public byte[] startOrFlowFlagReset(int cmd_cfg,byte[] userRev) throws IOException{
		byte[] parameter=new byte[1+4];//4 byte Rev.
		parameter[0]=(byte) cmd_cfg;
		System.arraycopy(userRev, 0, parameter, 1,4);
		byte[] result=transceive(CUSTOMER_START_STOP_RTC, parameter);
		return result;
	}
	/**
	 * start RTC
	 * @return
	 * @throws IOException
	 */
	public byte[] startRTC() throws IOException {
		byte[] result=startOrFlowFlagReset(0x00, new byte[]{0,0,0,0});
		return result;
	}
	/**
	 * 用于清除RTC测温流程进行标志rtc_flow_flag.
	 * @return
	 * @throws IOException
	 */
	public byte[] Flow_Flag_Reset() throws IOException {
//		byte[] random=getRandom();
		byte[] result=startOrFlowFlagReset(0x80,  new byte[]{0,0,0,0});
		return result;
	}
	/**
	 * 
	 * @param cmd_cfg
	 * @return
	 * @throws IOException
	 */
	public byte[]  deepSleep(int cmd_cfg) throws IOException{
		byte[] parameter=new byte[1+4];//4字节Rev.
		parameter[0]=(byte) cmd_cfg;		
		byte[] result=transceive(CUSTOMER_DEEP_SLEEP, parameter);
		return result;		
	}
	/**
	 * standby 芯片进入standby模式
	 * @return
	 * @throws IOException
	 */
	public byte[] standby() throws IOException{
		int cmd_cfg=0x02;//芯片进入standby模式
		byte[] result=deepSleep(cmd_cfg);
		return result;
	}
	/**
	 * 芯片进入PD模式
	 * @return
	 * @throws IOException
	 */
	public byte[] PD() throws IOException{
		int cmd_cfg=0x01;//芯片进入PD模式
		byte[] result=deepSleep(cmd_cfg);
		return result;
	}
	/**
	 * wake up 用于�?出PD模式�?
	 * @param cmd_cfg
	 * @return
	 * @throws IOException
	 */
	public byte[] wakeup(int cmd_cfg)throws IOException {
		byte[] parameter=new byte[1+4];//4字节Rev.
		parameter[0]=(byte) cmd_cfg;		
		byte[] result=transceive(CUSTOMER_WAKEUP, parameter);
		return result;
	}
	/**
	 * write reg 该指令用于直接对RTC相关的寄存器进行写入操作�?
	 * @param regAddr
	 * @param analogPara
	 * @return
	 * @throws IOException
	 */
	public byte[] writeReg(int regAddr,int analogPara)throws IOException {
		byte[] parameter=new byte[4+1]; //1 byte Rev.
		parameter[0]=(byte) (regAddr>>8 & 0xFF);
		parameter[1]=(byte) (regAddr & 0xFF);
		parameter[2]=(byte) (analogPara>>8 & 0xFF);
		parameter[3]=(byte) (analogPara & 0xFF);
		byte[] result=transceive(CUSTOMER_WRITE_REG, parameter);
		return result;
	}
	/**
	 * read reg 该指令用于读取RTC相关的寄存器的�?��??
	 * @param regAddr
	 * @return
	 * @throws IOException
	 */
	public byte[] readReg(int regAddr)throws IOException {
		byte[] parameter=new byte[2+3];//3 byte Rev.
		parameter[0]=(byte) (regAddr>>8 & 0xFF);
		parameter[1]=(byte) (regAddr & 0xFF);
	
		byte[] result=transceive(CUSTOMER_READ_REG, parameter);
		return result;
	}
	/**
	 * led ctrl 该指令用于控制外部LED的亮或灭�?
	 * @param cmd_cfg
	 * @return
	 * @throws IOException
	 */
	public byte[] ledCtrl(int cmd_cfg)throws IOException {
		byte[] parameter=new byte[1+4];//4 byte Rev.
		parameter[0]=(byte) cmd_cfg;		
		byte[] result=transceive(CUSTOMER_LED_CTRL, parameter);
		return result;
	}
	/**
	 * 用于�?查芯片当前的工作模式�?
	 * @param cmd_cfg
	 * @return
	 * @throws IOException
	 */
	public byte[] op_Mode_Chk(byte[] cmd_cfg)throws IOException {
		byte[] parameter=new byte[3+2];//2 byte Rev.
		parameter[0]=cmd_cfg[0];
		parameter[1]=cmd_cfg[1];
		parameter[2]=cmd_cfg[2];
		byte[] result=transceive(CUSTOMER_OP_MODE_CHK, parameter);
		return result;
	}
	/**
	 * 用于�?查高频场的场强，可用于指示该场强是否影响当前测温精度�?
	 * @param cmd_cfg
	 * @return
	 * @throws IOException
	 */
	public byte[] field_strength_chk(int cmd_cfg)throws IOException {
		byte[] parameter=new byte[1+4];//4 byte Rev.
		parameter[0]=(byte) cmd_cfg;		
		byte[] result=transceive(CUSTOMER_FIELD_STRENGTH_CHK, parameter);
		return result;
	}
	/**
	 * 唤醒
	 * @return
	 * @throws IOException
	 */
	public byte[]  setWakeup() throws IOException {
		int cmd_cfg=0x00;
		byte[] result=wakeup(cmd_cfg);
		return result;
		
	}
	/**
	 * 查看唤醒
	 * @return
	 * @throws IOException
	 */
	public byte[]  getWakeup() throws IOException {
		int cmd_cfg=0x80;
		byte[] result=wakeup(cmd_cfg);
		return result;
		
	}
	/**
	 * 延时
	 * @param delay
	 * @return
	 * @throws IOException
	 */
	public byte[] setDelay(int delay) throws IOException {
		int regAddr=0xc084;
		int analogPara=0x0000|delay;
		byte[] result=writeReg(regAddr, analogPara);
		return result;
		
	}
	/**
	 * 查看延时
	 * @return
	 * @throws IOException
	 */
	public byte[] getDelay() throws IOException {
		int regAddr=0xc084;
	
		byte[] result=readReg(regAddr);
		return result;
		
	}
    /**
     * 测温间隔
     * @param interval
     * @return
     * @throws IOException
     */
	public byte[] setInterval(int interval) throws IOException {
		int regAddr=0xc085;
		int analogPara=0x0000|interval;
		byte[] result=writeReg(regAddr, analogPara);
		return result;
		
	}
	/**
	 * 写测温间隔 用于iOS读取
	 * @param interval
	 * @return
	 * @throws IOException
	 */
	public byte[] setIntervalForiOS(int interval) throws IOException {
		
		int firstAddress=0x0028;
		int dataNum=0x0001;
		byte[] data=new byte[]{0x00,0x00};
		data[0]=(byte) ((interval &0xff00) >>8);
		data[1]= (byte) (interval&0x00ff);
		byte[] result=writeMemory(firstAddress, dataNum, data);
		return result;
		
	}
	/**
	 * 查看测温间隔
	 * @return
	 * @throws IOException
	 */
	public byte[] getInterval() throws IOException {	
//		int regAddr=0xc085;	
//		byte[] result=readReg(regAddr);
		int firstAddress = 0x0028;
		byte[] result=readMemory(firstAddress, 0);
		return result;
		
	}
	/**
	 * 设置数据存储格式
	 * @param form
	 * @return
	 * @throws IOException
	 */
	public byte[] setLogForm(byte[] form) throws IOException {
		int firstAddress=0xb040;//改地址对应的user_cfg
		int dataNum=3;
		byte[] data = form;
		byte[] result=writeMemory(firstAddress, dataNum, data);
		return result;
	}
	
	/**
	 * 设置数据存储格式 用于 iOS
	 * @param form
	 * @return
	 * @throws IOException
	 */
	public byte[] setLogFormForiOS(int form) throws IOException {
		int firstAddress=0x28;//此地址被采集间隔占用，暂时不用。
		int dataNum=0;
		byte[] data=new byte[]{(byte) form};
		byte[] result=writeMemory(firstAddress, dataNum, data);
		return result;
	}
	
	/**
	 * 查看电池,等状态
	 * @return
	 * @throws IOException
	 */
	public byte[] getState(byte cmd_cfg0) throws IOException {
		
		byte[] cmd_cfg=new byte[]{cmd_cfg0,0x00,0x00};
		byte[] result=op_Mode_Chk(cmd_cfg);
		return result;
		
	}
	/**
	 * 读取数据
	 * @return
	 * @throws IOException
	 */
	public byte[] readTemperatures() throws IOException {
		int firstAddress=0x1000;
		int numOfByte=0x00fa;
		byte[] result=readMemory(firstAddress, numOfByte);
//		byte[] result=nfcv.transceive(new byte[]{0x02, (byte) 0xb1, 0x1d, (byte) 0x10, (byte) 0x00, 0x00, (byte) 0xfa});
		return result;
		
	}
    /**
     * 开始实时测温
     * @return
     * @throws IOException
     */
	public byte[] startTemperature() throws IOException {
		int cmd_cfg=0x06;
		int blockAddr=0x00;
		byte[] result=getTemperature(cmd_cfg, blockAddr);
		return result;
		
	}
	/**
	 * 获取实时测温结果
	 * @return
	 * @throws IOException
	 */
	public byte[] getTemperature() throws IOException {
		int cmd_cfg=0x84;
		int blockAddr=0x00;
		byte[] result=getTemperature(cmd_cfg, blockAddr);
		return result;
		
	}
	/**
	 * 设置测温次数
	 * @param count
	 * @return
	 * @throws IOException
	 */
	public  byte[] setTimeMeasuredCount(byte[]count) throws IOException {
		int firstAddress=0xb094;
		int dataNum=0x01;
		byte[] data=count;
		byte[] result=writeMemory(firstAddress, dataNum, data);
		return result;
		
	}
	/**
	 * 查看定时测温次数
	 * @return
	 * @throws IOException
	 */
	public  byte[] getTimeMeasuredCount() throws IOException {
		int firstAddress=0xb094;			
		int numOfByte = 0x0003;
		byte[] result=readMemory(firstAddress, numOfByte);
		return result;
		
	}
    /**
     * 查看记录过程的测温次数
     * @return
     * @throws IOException
     */
	public  byte[] getMeasuredCount() throws IOException {
		int regAddr=0xc091;	
		
		byte[] result=readReg(regAddr);
		return result;
		
	}
	/**
	 * 设置最小温度值
	 * @param tpMin
	 * @return
	 * @throws IOException
	 */
	public byte[] setMinTemperature(byte[] tpMin) throws IOException {
		int firstAddress=0xb08e;
		int dataNum=0x01;
		byte[] data=tpMin;
		byte[] result=writeMemory(firstAddress, dataNum, data);
		return result;
		
	}
	/**
	 * 设置最大温度值
	 * @param tpMax
	 * @return
	 * @throws IOException
	 */
	public byte[] setMaxTemperature(byte[] tpMax) throws IOException {
		int firstAddress=0xb08c;
		int dataNum=0x01;
		byte[] data=tpMax;
		byte[] result=writeMemory(firstAddress, dataNum, data);
		return result;
		
	}
	/**
	 * 设置最大最小温度值，用于iOS
	 * @param tpMax
	 * @param tpMin
	 * @return
	 * @throws IOException
	 */
	public byte[] setMaxMinTemperatureForiOS(byte tpMax,byte tpMin) throws IOException {
		int firstAddress=0x0024;
		int dataNum=0x03;
		byte[] data=new byte[]{0x00,tpMin,0x00,tpMax};
		byte[] result=writeMemory(firstAddress, dataNum, data);
		return result;
		
	}
	
	
	/**
	 * 获取温度上下限值
	 * @return
	 * @throws IOException
	 */
	public byte[]  getMaxMinTemperature() throws IOException {
		int firstAddress=0xb08c;
		int numOfByte=0x0003;
		byte[] result=readMemory(firstAddress, numOfByte);
		return result;
		
	}
	/**
	 * 设置超限最小值0
	 * @param tpMinLimit
	 * @return
	 * @throws IOException
	 */
	public byte[] setMinLimit0Temperature(byte[] tpMinLimit) throws IOException {
		int firstAddress=0xb080;
		int dataNum=0x01;
		byte[] data=tpMinLimit;
		byte[] result=writeMemory(firstAddress, dataNum, data);
		return result;
		
	}
	/**
	 * 设置超限最大值0
	 * @param tpMaxLimit
	 * @return
	 * @throws IOException
	 */
	public byte[] setMaxLimit0Temperature(byte[] tpMaxLimit) throws IOException {
		int firstAddress=0xb082;
		int dataNum=0x01;
		byte[] data=tpMaxLimit;
		byte[] result=writeMemory(firstAddress, dataNum, data);
		return result;
		
	}
	/**
	 * 获取超限最大最小值0
	 * @return
	 * @throws IOException
	 */
	public byte[]  getMaxMinLimit0Temperature() throws IOException {
		int firstAddress=0xb080;
		int numOfByte=0x0003;
		byte[] result=readMemory(firstAddress, numOfByte);
		return result;
		
	}
	
	/**
	 * 设置超限最小值1
	 * @param tpMinLimit
	 * @return
	 * @throws IOException
	 */
	public byte[] setMinLimit1Temperature(byte[] tpMinLimit) throws IOException {
		int firstAddress=0xb084;
		int dataNum=0x01;
		byte[] data=tpMinLimit;
		byte[] result=writeMemory(firstAddress, dataNum, data);
		return result;
		
	}
	/**
	 * 设置超限最大值1
	 * @param tpMaxLimit
	 * @return
	 * @throws IOException
	 */
	public byte[] setMaxLimit1Temperature(byte[] tpMaxLimit) throws IOException {
		int firstAddress=0xb086;
		int dataNum=0x01;
		byte[] data=tpMaxLimit;
		byte[] result=writeMemory(firstAddress, dataNum, data);
		return result;
		
	}
	/**
	 * 获取超限最大最小值1
	 * @return
	 * @throws IOException
	 */
	public byte[]  getMaxMinLimit1Temperature() throws IOException {
		int firstAddress=0xb084;
		int numOfByte=0x0003;
		byte[] result=readMemory(firstAddress, numOfByte);
		return result;
		
	}
	
	/**
	 * 设置超限最小值2
	 * @param tpMinLimit
	 * @return
	 * @throws IOException
	 */
	public byte[] setMinLimit2Temperature(byte[] tpMinLimit) throws IOException {
		int firstAddress=0xb088;
		int dataNum=0x01;
		byte[] data=tpMinLimit;
		byte[] result=writeMemory(firstAddress, dataNum, data);
		return result;
		
	}
	/**
	 * 设置超限最大值2
	 * @param tpMaxLimit
	 * @return
	 * @throws IOException
	 */
	public byte[] setMaxLimit2Temperature(byte[] tpMaxLimit) throws IOException {
		int firstAddress=0xb08a;
		int dataNum=0x01;
		byte[] data=tpMaxLimit;
		byte[] result=writeMemory(firstAddress, dataNum, data);
		return result;
		
	}
	/**
	 * 获取超限最大最小值2
	 * @return
	 * @throws IOException
	 */
	public byte[]  getMaxMinLimit2Temperature() throws IOException {
		int firstAddress=0xb088;
		int numOfByte=0x0003;
		byte[] result=readMemory(firstAddress, numOfByte);
		return result;
		
	}
	/**
	 * 写启动时间
	 * @param startTime
	 * @return
	 * @throws IOException
	 */
	public byte[] setStartTime(byte[] startTime) throws IOException {
		int firstAddress=0x0020;
		int dataNum=0x03;
		byte[] data=startTime;
		byte[] result=writeMemory(firstAddress, dataNum, data);
		return result;
		
	}
	/**
	 * 读取时间
	 * @return
	 * @throws IOException
	 */
	public byte[] getStartTime() throws IOException {
		int firstAddress=0x0020;		
		int numOfByte=0x0003;
		byte[] result=readMemory(firstAddress, numOfByte);
		return result;
		
	}
	/**
	 * 休眠
	 * @return
	 * @throws IOException
	 */
	public byte[] sleep() throws IOException{
		int cmd_cfg=0x01;//芯片进入PD模式
		byte[] result=deepSleep(cmd_cfg);
		return result;
	}
	/**
	 * 返回温度数据存储格式对应的编码
	 * @return
	 * @throws IOException
	 */
	public int getLogForm() throws IOException{
		int numOfByte=0;
		int firstAddress=0xb040;//改地址对应user_cfg0、~user_cfg0、user_cfg1、~user_cfg1
		byte[] result=readMemory(firstAddress, numOfByte);
		return (int)((result[0]&0x1f)>>2);
	}
	
	/**
	 * 获取电池电压
	 * @return
	 * @throws IOException
	 */
	public byte[] getBatteryLevel() throws IOException {
		int regAddr=0xc012;
		int analogPara=0x0008;
		byte[] result1=writeReg(regAddr, analogPara);//获取电池电压第一步 配置寄存器
		int cmd_cfg=0x12;
		int blockAddr=0x00;
		byte[] result2 = getTemperature(cmd_cfg, blockAddr);//获取电池电压第二步  启动测量
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cmd_cfg=0x92;
		byte[] result3 = getTemperature(cmd_cfg, blockAddr);//获取电池电压第三步 读取结果
		analogPara=0x0000;
		byte[] result4 = writeReg(regAddr,analogPara);//获取电池电压第四步  恢复寄存器
		return result3;
		
	}
	/**
	 * Convert a temperature measurement result to degree celsius.
	 * 
	 * @param temperatureCode Temperature measurement returned by getTemperature().
	 * @return Temperature measurement result converted to degree celsius.
	 */
	public static double convertTemperatureCodeToCelsius(int temperatureCode) {
		return temperatureCode * 0.169 - 92.7 - 5.4;
	}
	
	/**
	 * Convert a battery measurement result to voltage.
	 * 
	 * @param batteryCode Battery measurement returned by getBattery().
	 * @param nominalVoltage Nominal voltage level of the battery.
	 * @return Battery measurement result converted to voltage.
	 */
	public double convertBatteryCodeToVoltage(int batteryCode, double nominalVoltage) {
		//return (batteryCode * 0.00335 + 0.860)/1.5 * nominalVoltage;
		return (batteryCode * 0.00335 + 0.860);
	}
	
	/**
	 * Check the response to an ISO15693 command. If the response length doesn't match
	 * the expected response length, or the flags byte of the response contains an error
	 * code, then an IOException is thrown.
	 * 
	 * @param expectedLength
	 * @throws IOException
	 */
	protected void checkResponse(int expectedLength, byte[] response) throws IOException {
		if (response.length != expectedLength)
			throw new IOException("Unexpected response length: " + response.length + "bytes");
		
		if (response.length == 0)
			throw new IOException("Zero length response");
		
		throwIso15693ErrorException(response[0]);
	}
	
	/**
	 * Convert an ISO15693 or SL13A error code into an IOException.
	 * 
	 * @param errorCode Flags byte of the ISO15693 response
	 * @throws IOException
	 */
	protected void throwIso15693ErrorException(byte errorCode) throws IOException {
		switch (errorCode) {
		case 0x00: return;
		case 0x01: throw new IOException("Command not supported");
		case 0x02: throw new IOException("Command not recognized");
		case 0x03: throw new IOException("Option not supported");
		case 0x0F: throw new IOException("Unknown error");
		case 0x10: throw new IOException("Block not available");
		case 0x11: throw new IOException("Block already locked");
		case 0x12: throw new IOException("Block already locked");
		case (byte) 0xA0: throw new IOException("Incorrect password");
		case (byte) 0xA1: throw new IOException("Log parameter missing");
		case (byte) 0xA2: throw new IOException("Battery measurement error");
		case (byte) 0xA3: throw new IOException("Temperature measurement error");
		case (byte) 0xA5: throw new IOException("User data area error");
		case (byte) 0xA6: throw new IOException("EEPROM collision");
		default: throw new IOException(String.format("Unkown ISO15693 error: %02x", ((int) errorCode) & 0xFF));
		}
	}
	/**
	 * 
	 * @param command
	 * @return
	 * @throws IOException
	 */
	protected byte[] transceive(byte command) throws IOException {
		byte[] parameter = new byte[0];		
		return transceive(command, parameter);
	}
    /**
     * 
     * @param command
     * @param parameter
     * @return
     * @throws IOException
     */
	protected byte[] transceive(byte command, byte[] parameter) throws IOException {
		byte[] nfcVCommand;
		int compare = command;
		if(compare<0)
			compare+=256;
		if(compare >= 0xB1)
		{
			nfcVCommand = new byte[2+ parameter.length];
//			nfcVCommand[0] = 0x22;
			nfcVCommand[0] = 0x40;
			nfcVCommand[1] = command;
//			nfcVCommand[2] = 0x1D;//IC Mfg Code	
//			System.arraycopy(nfca.getTag().getId(), 0, nfcVCommand, 2, 7);
//			System.arraycopy(parameter, 0, nfcVCommand, 10,parameter.length);
			System.arraycopy(parameter, 0, nfcVCommand, 2, parameter.length);
		}else {
			nfcVCommand = new byte[1 + parameter.length];
//			nfcVCommand[0] = 0x22;
			nfcVCommand[0] = command;
//			System.arraycopy(nfca.getTag().getId(), 0, nfcVCommand, 2, 8);
//			System.arraycopy(parameter, 0, nfcVCommand, 10, parameter.length);
			System.arraycopy(parameter, 0, nfcVCommand, 1, parameter.length);
		}
		
		return nfca.transceive(nfcVCommand);
	}
}
