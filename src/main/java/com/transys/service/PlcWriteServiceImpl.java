package com.transys.service;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.transys.controller.MainController;
import com.transys.dao.PlcWriteDao;
import com.transys.domain.PlcWrite;
import com.transys.util.OpcDataMap;

@Service
public class PlcWriteServiceImpl implements PlcWriteService{
	
	@Autowired
	private PlcWriteDao plcWriteDao;
	
	private final Logger logger = LoggerFactory.getLogger(PlcWriteServiceImpl.class);
	
	//PLCWRITE(1~4호기)
	public void plcWrite() throws InterruptedException, ExecutionException {
//		System.out.println("PLCWRITE ");
		OpcDataMap opcData = new OpcDataMap();

		//DB데이터 조회 (t_waitlist)
		PlcWrite plcWrite = plcWriteDao.getPlcWriteWorkData();
//		System.out.println(plcWrite.getList_year());
		Thread.sleep(500);
		
		
		//가져온 행의 값이 있을때만
		String list_year = "";
//		System.out.println("list_year 11 : "+list_year);
		if(plcWrite != null) {
			if(!"".equals(plcWrite.getList_year()) && plcWrite.getList_year() != null) {
				list_year = plcWrite.getList_year();
	//			System.out.println("list_year 22 : "+list_year);
				//OPC값 쓰기
				//outData1, outData2, outData3, outData4, outData5
				StringBuffer desc = new StringBuffer();
				desc.append("CYCLENO : "+plcWrite.getCycleno()+"// ");
				desc.append("PUMBUN : "+plcWrite.getPumbun()+"// ");
				desc.append("AGITATE_RPM : "+plcWrite.getAgitate_rpm()+"// ");
				desc.append("DEVICECODE : "+plcWrite.getDevicecode());
				
				
				
				logger.info("PLCWRITE(14호기) : {}",desc.toString());
				opcData.setOpcData("Transys.PLCWRITE.CM01.LOTNO", plcWrite.getLotno());
				opcData.setOpcData("Transys.PLCWRITE.CM01.CYCLENO", plcWrite.getCycleno());
				opcData.setOpcData("Transys.PLCWRITE.CM01.PUMBUN", plcWrite.getPumbun());
				opcData.setOpcData("Transys.PLCWRITE.CM01.AGITATE_RPM", plcWrite.getAgitate_rpm());
				opcData.setOpcData("Transys.PLCWRITE.CM01.DEVICECODE", plcWrite.getDevicecode());
				opcData.setOpcData("Transys.PLCWRITE.CM01.COMMON_DEVICE", plcWrite.getCommon_device());
				opcData.setOpcData("Transys.PLCWRITE.CM01.MESLOT", plcWrite.getMeslot());
				
				Thread.sleep(500);
	
				opcData.setOpcData("Transys.PLCWRITE.CM01.LOTNO", plcWrite.getLotno());
				opcData.setOpcData("Transys.PLCWRITE.CM01.CYCLENO", plcWrite.getCycleno());
				opcData.setOpcData("Transys.PLCWRITE.CM01.PUMBUN", plcWrite.getPumbun());
				opcData.setOpcData("Transys.PLCWRITE.CM01.AGITATE_RPM", plcWrite.getAgitate_rpm());
				opcData.setOpcData("Transys.PLCWRITE.CM01.DEVICECODE", plcWrite.getDevicecode());
				opcData.setOpcData("Transys.PLCWRITE.CM01.COMMON_DEVICE", plcWrite.getCommon_device());
				opcData.setOpcData("Transys.PLCWRITE.CM01.MESLOT", plcWrite.getMeslot());
	
				//DB값 업데이트 (t_waitlist)
				plcWriteDao.setPlcWriteDataUpdate(plcWrite);
	
				
				Thread.sleep(200);
				//DB 프로시저 실행(TRACKING_PROC00)
				plcWriteDao.setPlcWriteProc(plcWrite);
				
				Thread.sleep(200);
				//DB값 삭제 (OUTPUT_TAB)
				plcWriteDao.setPlcWriteDataDelete(plcWrite);
				
				//각 설비에 해당되는 outPutChk값 false
				int device = Integer.parseInt(plcWrite.getDevicecode());
				
				switch (device) {
					case 1 : MainController.outPutChk1 = false; break;
					case 2 : MainController.outPutChk2 = false; break;
					case 3 : MainController.outPutChk3 = false; break;
					case 4 : MainController.outPutChk4 = false; break;
				}
				Thread.sleep(200);
				desc.append("-> 완료");
				logger.info("PLCWRITE(14호기) : {}",desc.toString());			
			}
		}
	}
		
	//침탄1~4호기, 공통1호기
	public void plcWriteTimer() throws InterruptedException, ExecutionException {
		String output_chk = "false";
		OpcDataMap opcData = new OpcDataMap();
		
		//창고출고가능요구 1이면
		Map<String, Object> outputMap = opcData.getOpcData("Transys.PLCWRITE.CM01.OUTPUT_CHK");	//DB18.X41.5
		
		output_chk = outputMap.get("value").toString();
		
		if("true".equals(output_chk)) {
			
			plcWrite();
		}
	}
	
	
}
