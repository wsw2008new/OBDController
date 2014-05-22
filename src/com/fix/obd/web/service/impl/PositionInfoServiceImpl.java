package com.fix.obd.web.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.fix.obd.web.dao.PositionDataDao;
import com.fix.obd.web.model.PositionData;
import com.fix.obd.web.service.PositionInfoService;
@Component
public class PositionInfoServiceImpl implements PositionInfoService{
	@Resource
	private PositionDataDao positionDataDao;
	public PositionDataDao getPositionDataDao() {
		return positionDataDao;
	}
	public void setPositionDataDao(PositionDataDao positionDataDao) {
		this.positionDataDao = positionDataDao;
	}
	@Override
	public PositionData getLatestPositionInfo(String terminalId) {
		// TODO Auto-generated method stub
		try {
			List<PositionData> position_list = positionDataDao.findByHQL("from PositionData where tid = '" + terminalId + "'");
			if(position_list.size()>0){
				PositionData latest_data = position_list.get(0);
				if(position_list.size()>1){
					for(int i=1;i<position_list.size();i++){
						if(position_list.get(i).getDate().compareTo(latest_data.getDate())>0)
							latest_data = position_list.get(i);
					}
				}
				return latest_data;
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
	public List<PositionData> getLatest10PostionInfo(String terminalId) {
		// TODO Auto-generated method stub
		try {
			List<PositionData> returnData = new ArrayList<PositionData>();
			List<PositionData> tempData = new ArrayList<PositionData>();
			List<PositionData> position_list = positionDataDao.findByHQL("from PositionData where tid = '" + terminalId + "' order by date desc");
			for(int i=0;i<(position_list.size()>10?10:position_list.size());i++){
				tempData.add(position_list.get(i));
			}
			for(int i=tempData.size()-1;i>=0;i--)
				returnData.add(tempData.get(i));
			return returnData;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public int getGraphRefreshMinute() {
		// TODO Auto-generated method stub
		try {
			Properties p = new Properties();
			InputStream is=this.getClass().getResourceAsStream("/system.properties"); 
			p.load(is);  
			is.close();
			return Integer.parseInt(p.getProperty("graph_refresh_millisecond"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	@Override
	public int getPositionDataRefreshMinute() {
		// TODO Auto-generated method stub
		try {
			Properties p = new Properties();
			InputStream is=this.getClass().getResourceAsStream("/system.properties"); 
			p.load(is);  
			is.close();
			return Integer.parseInt(p.getProperty("position_info_refresh_millisecond"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	@Override
	public List<PositionData> getLatest10GpsPositionInfo(String terminalId) {
		// TODO Auto-generated method stub
		try {
			List<PositionData> returnData = new ArrayList<PositionData>();
			List<PositionData> tempData = new ArrayList<PositionData>();
			List<PositionData> position_list = new ArrayList<PositionData>();
			position_list = positionDataDao.findByHQL("from PositionData where tid = '" + terminalId + "' order by date desc");
			List<PositionData> gps_list = new ArrayList<PositionData>();
			for(int i=0;i<position_list.size();i++){
				if(position_list.get(i).getInfo().contains("GPS状态:GPS定位"))
					gps_list.add(position_list.get(i));
			}
			for(int i=0;i<(gps_list.size()>10?10:gps_list.size());i++){
				tempData.add(gps_list.get(i));
			}
			for(int i=tempData.size()-1;i>=0;i--)
				returnData.add(tempData.get(i));
			return returnData;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	@Override
	public Map getStartandStopByGPS(String terminalId, String start_time,
			String stop_time) {
		// TODO Auto-generated method stub
		Map map = new HashMap();
		try {
			if(start_time.length()==12&&stop_time.length()==12){
				String start_time_in_format = start_time.substring(0,2) + "-" + start_time.substring(2,4) + "-" + start_time.substring(4,6) + " " + start_time.substring(6,8) + ":" + start_time.substring(8,10) + ":" + start_time.substring(10,12);
				String stop_time_in_format = stop_time.substring(0,2) + "-" + stop_time.substring(2,4) + "-" + stop_time.substring(4,6) + " " + stop_time.substring(6,8) + ":" + stop_time.substring(8,10) + ":" + stop_time.substring(10,12);
				map.put("start_time_in_format", start_time_in_format);
				map.put("stop_time_in_format", stop_time_in_format);
				List<PositionData> position_between = positionDataDao.findByHQL("from PositionData where tid = '" + terminalId + "' and date>'" + start_time_in_format + "' and date<'" + stop_time_in_format + "' order by date desc");
				for(int i=0;i<position_between.size();i++){
					if(position_between.get(i).getInfo().contains("GPS状态:GPS不定位;")){
						position_between.remove(i);
						i--;
					}
				}
				String start_point = "Nil";
				String stop_point = "Nil";
				if(position_between.size()>0){
					String stop_point_latitude = position_between.get(0).getInfo().substring(position_between.get(0).getInfo().lastIndexOf("纬度:"));
					stop_point_latitude = stop_point_latitude.substring(0,stop_point_latitude.indexOf(";"));
					stop_point_latitude = stop_point_latitude.split(":")[1];
					stop_point_latitude = stop_point_latitude.replaceAll("\\.", "");
					stop_point_latitude = stop_point_latitude.replaceAll("°", ".");
					String tempStrPart = stop_point_latitude.split("\\.")[1];
					tempStrPart = "0." + tempStrPart;
					double tempD = Double.parseDouble(tempStrPart)/60*100;
					stop_point_latitude = Integer.parseInt(stop_point_latitude.split("\\.")[0]) + tempD + "";
					
					String stop_point_longitute = position_between.get(0).getInfo().substring(position_between.get(0).getInfo().lastIndexOf("经度:"));
					stop_point_longitute = stop_point_longitute.substring(0,stop_point_longitute.indexOf(";"));
					stop_point_longitute = stop_point_longitute.split(":")[1];
					stop_point_longitute = stop_point_longitute.replaceAll("\\.", "");
					stop_point_longitute = stop_point_longitute.replaceAll("°", ".");
					String _tempStrPart = "0." + stop_point_longitute.split("\\.")[1];
					double _tempD = Double.parseDouble(_tempStrPart)/60*100;
					stop_point_longitute = Integer.parseInt(stop_point_longitute.split("\\.")[0]) + _tempD + "";
					stop_point = stop_point_longitute + "," + stop_point_latitude;
					
					String start_point_latitude = position_between.get(position_between.size()-1).getInfo().substring(position_between.get(0).getInfo().lastIndexOf("纬度:"));
					System.out.println(position_between.get(position_between.size()-1).getInfo());
					start_point_latitude = start_point_latitude.substring(0,start_point_latitude.indexOf(";"));
					start_point_latitude = start_point_latitude.split(":")[1];
					start_point_latitude = start_point_latitude.replaceAll("\\.", "");
					start_point_latitude = start_point_latitude.replaceAll("°", ".");
					String tempStrPart1 = start_point_latitude.split("\\.")[1];
					tempStrPart1 = "0." + tempStrPart1;
					double tempD1 = Double.parseDouble(tempStrPart1)/60*100;
					start_point_latitude = Integer.parseInt(start_point_latitude.split("\\.")[0]) + tempD1 + "";
					
					String start_point_longitute = position_between.get(position_between.size()-1).getInfo().substring(position_between.get(0).getInfo().lastIndexOf("经度:"));
					start_point_longitute = start_point_longitute.substring(0,start_point_longitute.indexOf(";"));
					start_point_longitute = start_point_longitute.split(":")[1];
					start_point_longitute = start_point_longitute.replaceAll("\\.", "");
					start_point_longitute = start_point_longitute.replaceAll("°", ".");
					String _tempStrPart1 = "0." + start_point_longitute.split("\\.")[1];
					double _tempD1 = Double.parseDouble(_tempStrPart1)/60*100;
					start_point_longitute = Integer.parseInt(start_point_longitute.split("\\.")[0]) + _tempD1 + "";
					start_point = start_point_longitute + "," + start_point_latitude;
//					System.out.println(start_point + "-->" + stop_point);
					map.put("start_point",start_point);
					map.put("stop_point", stop_point);
				}
				return map;
			}
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
