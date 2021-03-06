package com.fix.obd.web.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fix.obd.util.MessageUtil;
import com.fix.obd.web.model.TravelExmnation;
import com.fix.obd.web.model.util.OBDSeperateUtilModel;
import com.fix.obd.web.service.TravelExmnationService;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

@Controller
@RequestMapping("/travelexmpersonal")
public class TravelExmPersonalControl {
	private static final Logger logger = LoggerFactory.getLogger(TravelExmPersonalControl.class);
	@Resource
	private TravelExmnationService travelExmnationService;
	public TravelExmnationService getTravelExmnationService() {
		return travelExmnationService;
	}
	public void setTravelExmnationService(
			TravelExmnationService travelExmnationService) {
		this.travelExmnationService = travelExmnationService;
	}
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView listResult(HttpServletRequest request,HttpSession session,HttpServletResponse response){
		String terminalId = request.getParameter("terminalId");
		terminalId = MessageUtil.frontCompWithZore(terminalId, 20);
		logger.debug("--------travel exm:" + terminalId + "--------");
		TravelExmnation t = travelExmnationService.exmnationAndRecord(terminalId);
		ArrayList<OBDSeperateUtilModel> list = new ArrayList<OBDSeperateUtilModel>();
		OBDSeperateUtilModel tmpModel = new OBDSeperateUtilModel();
		tmpModel.setName("总距离");
		tmpModel.setContent(t.getTotalDistance());
		tmpModel.setExtra("km");
		list.add(tmpModel);
		tmpModel = new OBDSeperateUtilModel();
		tmpModel.setName("最大距离");
		tmpModel.setContent(t.getLongestDistance());
		tmpModel.setExtra("km");
		list.add(tmpModel);
		tmpModel = new OBDSeperateUtilModel();
		tmpModel.setName("最大速度");
		tmpModel.setContent(t.getMaxSpeed());
		tmpModel.setExtra("km/h");
		list.add(tmpModel);
		tmpModel = new OBDSeperateUtilModel();
		tmpModel.setName("总超时时长");
		tmpModel.setContent(t.getTotalExceedSeconds());
		tmpModel.setExtra("s");
		list.add(tmpModel);
		tmpModel = new OBDSeperateUtilModel();
		tmpModel.setName("总急刹车次数");
		tmpModel.setContent(t.getTotalBrakeTimes());
		tmpModel.setExtra("次");
		list.add(tmpModel);
		tmpModel = new OBDSeperateUtilModel();
		tmpModel.setName("总紧急刹车次数");
		tmpModel.setContent(t.getTotalEmerBrakeTimes());
		tmpModel.setExtra("次");
		list.add(tmpModel);
		tmpModel = new OBDSeperateUtilModel();
		tmpModel.setName("总急加速次数");
		tmpModel.setContent(t.getTotalSpeedUpTimes());
		tmpModel.setExtra("次");
		list.add(tmpModel);
		tmpModel = new OBDSeperateUtilModel();
		tmpModel.setName("总紧急加速次数");
		tmpModel.setContent(t.getTotalEmerSpeedUpTimes());
		tmpModel.setExtra("次");
		list.add(tmpModel);
		tmpModel = new OBDSeperateUtilModel();
		tmpModel.setName("平均速度");
		tmpModel.setContent(t.getAvgSpeed());
		tmpModel.setExtra("km/h");
		list.add(tmpModel);
		tmpModel = new OBDSeperateUtilModel();
		tmpModel.setName("发动机最高水温");
		tmpModel.setContent(t.getMaxWaterTmp());
		tmpModel.setExtra("°C");
		list.add(tmpModel);
		tmpModel = new OBDSeperateUtilModel();
		tmpModel.setName("发动机最高转速");
		tmpModel.setContent(t.getMaxRevolution());
		tmpModel.setExtra("转/分");
		list.add(tmpModel);
		tmpModel = new OBDSeperateUtilModel();
		tmpModel.setName("总油耗");
		tmpModel.setContent(t.getTotalOilExpend());
		tmpModel.setExtra("升");
		list.add(tmpModel);
		tmpModel = new OBDSeperateUtilModel();
		tmpModel.setName("平均油耗");
		tmpModel.setContent(t.getAvgOilExpend());
		tmpModel.setExtra("百公里升");
		list.add(tmpModel);
		tmpModel = new OBDSeperateUtilModel();
		tmpModel.setName("总疲劳驾驶时间");
		tmpModel.setContent(t.getTotalTiredDrivingMinutes());
		tmpModel.setExtra("min");
		list.add(tmpModel);
		
//		System.out.println("总距离：" + t.getTotalDistance());
//		System.out.println("最大距离：" + t.getLongestDistance());
//		System.out.println("最大速度：" + t.getMaxSpeed());
//		System.out.println("总超时时长：" + t.getTotalExceedSeconds());
//		System.out.println("总急刹车次数：" + t.getTotalBrakeTimes());
//		System.out.println("总紧急刹车次数：" + t.getTotalEmerBrakeTimes());
//		System.out.println("总急加速次数：" + t.getTotalSpeedUpTimes());
//		System.out.println("总紧急加速次数：" + t.getTotalEmerSpeedUpTimes());
//		System.out.println("平均速度：" + t.getAvgSpeed());
//		System.out.println("发动机最高水温：" + t.getMaxWaterTmp());
//		System.out.println("发动机最高转速：" + t.getMaxRevolution());
//		System.out.println("总油耗：" + t.getTotalOilExpend());
//		System.out.println("平均油耗：" + t.getAvgOilExpend());
//		System.out.println("总疲劳驾驶时间：" + t.getTotalTiredDrivingMinutes());
		Map reply_map = travelExmnationService.exmnationScoreAmongFriends(terminalId);
		Map<String,Object> model = new HashMap<String,Object>();
		model.put("terminalId", terminalId);
		model.put("oil_score", reply_map.get("oil_score"));
		model.put("mile_score", reply_map.get("mile_score"));
		model.put("stability_score", reply_map.get("stability_score"));
		model.put("speed_score", reply_map.get("speed_score"));
		model.put("tired_control_score", reply_map.get("tired_control_score"));
		model.put("character_list", list);
		model.put("oil_text", reply_map.get("oil_text"));
		model.put("mile_text", reply_map.get("mile_text"));
		model.put("stability_text", reply_map.get("stability_text"));
		model.put("speed_text", reply_map.get("speed_text"));
		model.put("tired_control_text", reply_map.get("tired_control_text"));
		return new ModelAndView("TravelExmPersonalPage",model);
	}
}
