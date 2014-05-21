package com.fix.obd.web.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.fix.obd.tcp.thread.UploadTerminalDataTask;
import com.fix.obd.util.MessageUtil;
import com.fix.obd.util.ThreadMap;
import com.fix.obd.web.dao.OBDTerminalInfoDao;
import com.fix.obd.web.dao.TravelInfoDao;
import com.fix.obd.web.model.OBDTerminalInfo;
import com.fix.obd.web.model.TravelInfo;
import com.fix.obd.web.service.TravelInfoService;

@Component
public class TravelInfoServiceImpl implements TravelInfoService{
	@Resource
	private TravelInfoDao travelInfoDao;
	@Resource
	private OBDTerminalInfoDao obdTerminalInfoDao;
	public OBDTerminalInfoDao getObdTerminalInfoDao() {
		return obdTerminalInfoDao;
	}

	public void setObdTerminalInfoDao(OBDTerminalInfoDao obdTerminalInfoDao) {
		this.obdTerminalInfoDao = obdTerminalInfoDao;
	}

	public TravelInfoDao getTravelInfoDao() {
		return travelInfoDao;
	}

	public void setTravelInfoDao(TravelInfoDao travelInfoDao) {
		this.travelInfoDao = travelInfoDao;
	}

	@Override
	public TravelInfo getTravelInfo(String terminalId) {
		// TODO Auto-generated method stub
		try {
			List<TravelInfo> info_list = travelInfoDao.findByHQL("from TravelInfo where tid = '" + terminalId + "' order by date desc");
			if(info_list.size()>0){
				return info_list.get(0);
			}
			else
				return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean askLastestTravelInfo(String terminalId) {
		// TODO Auto-generated method stub
		try {
			List<OBDTerminalInfo> list = obdTerminalInfoDao.findByHQL("from OBDTerminalInfo where tid = '" + MessageUtil.frontCompWithZore(terminalId, 20) + "'");
			if(list.size()>0){
				OBDTerminalInfo obd = list.get(0);
				String ipAndPort = obd.getTerminalIp();
				String ip = ipAndPort.split(":")[0];
				String port = ipAndPort.split(":")[1];
				UploadTerminalDataTask u = ThreadMap.threadNameMap.get("/" + ip);				
				String bufferId = "78";
				boolean result = u.SentQueryLastItinerary(terminalId, bufferId);
				return result;
			}
			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean askTravelInfo(String terminalId, String index) {
		// TODO Auto-generated method stub
		try {
			List<OBDTerminalInfo> list = obdTerminalInfoDao.findByHQL("from OBDTerminalInfo where tid = '" + MessageUtil.frontCompWithZore(terminalId, 20) + "'");
			if(list.size()>0){
				OBDTerminalInfo obd = list.get(0);
				String ipAndPort = obd.getTerminalIp();
				String ip = ipAndPort.split(":")[0];
				String port = ipAndPort.split(":")[1];
				UploadTerminalDataTask u = ThreadMap.threadNameMap.get("/" + ip);				
				String bufferId = "78";
				boolean result = u.SentRequestTravelInfo(terminalId, bufferId, index);
				return result;
			}
			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public List<TravelInfo> reviewTravelInfo(String terminalId) {
		// TODO Auto-generated method stub
		try {
			List<TravelInfo> info_list = travelInfoDao.findByHQL("from TravelInfo where tid = '" + terminalId + "' order by SUBSTR(info,9,21) desc");
			if(info_list.size()>0){
				if(info_list.size()>10){
					List<TravelInfo> info_list_in_10 = new ArrayList<TravelInfo>();
					for(int i=0;i<10;i++)
						info_list_in_10.add(info_list.get(i));
					return info_list_in_10;
				}
				return info_list;
			}
			else
				return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
}
