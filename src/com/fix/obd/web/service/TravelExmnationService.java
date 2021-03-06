package com.fix.obd.web.service;

import java.util.List;
import java.util.Map;

import com.fix.obd.web.model.TravelExmnation;
import com.fix.obd.web.model.TravelInfo;

public interface TravelExmnationService {
	String getTotalDistance(List<TravelInfo> list);
	String getLongestDistance(List<TravelInfo> list);
	String getMaxSpeed(List<TravelInfo> list);
	String getTotalExceedSeconds(List<TravelInfo> list);
	String getTotalBrakeTimes(List<TravelInfo> list);
	String getTotalEmerBrakeTimes(List<TravelInfo> list);
	String getTotalSpeedUpTimes(List<TravelInfo> list);
	String getTotalEmerSpeedUpTimes(List<TravelInfo> list);
	String getAvgSpeed(List<TravelInfo> list);
	String getMaxWaterTmp(List<TravelInfo> list);
	String getMaxRevolution(List<TravelInfo> list);
	String getTotalOilExpend(List<TravelInfo> list);
	String getAvgOilExpend(List<TravelInfo> list);
	String getTotalTiredDrivingMinutes(List<TravelInfo> list);
	public TravelExmnation exmnationAndRecord(String terminalId);
	public TravelExmnation exmnationAndRecordByMonth(String terminalId, String year, String month);
	public Map exmnationScoreAmongFriends(String terminalId);
	public Map statisticOfSpeedAndHour(String terminalId);
	public Map statisticOfSpeedAndHourByMonth(String terminalId, String year, String month);
	public Map statisticOfHour(String terminalId);
	public Map statisticOfHourByMonth(String terminalId, String year, String month);
	public Map statisticOfBrakeAndHour(String terminalId);
	public Map statisticOfSpeedupAndHour(String terminalId);
	public Map speedPlan(String terminalId);
}	
