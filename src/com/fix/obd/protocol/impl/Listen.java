package com.fix.obd.protocol.impl;

import org.apache.log4j.Logger;

import com.fix.obd.protocol.ODBProtocol;
import com.fix.obd.protocol.ODBProtocolParser;
import com.fix.obd.util.MessageUtil;
import com.fix.obd.util.ProtocolPropertiesUtil;
import com.fix.obd.util.ResponseStrMaker;

public class Listen extends ODBProtocolParser implements ODBProtocol{
	private static final  Logger logger = Logger.getLogger(Listen.class);
	private String protocolClientId;
	private String protocolBufferId;
	private String longMessage;

	/*
	 * characterSentence是一个长度为2的数组，0位代表电话号码的长度，1位代表监听的电话号码
	 */
	public Listen(String clientId, String bufferId, String message){
		this.protocolClientId = clientId;
		this.protocolBufferId = bufferId;
		this.longMessage = message;
		MessageUtil.printAndToDivContent("向终端" + clientId + "发送监听请求",true);
	}

	@Override
	public boolean DBOperation(boolean DBif) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public byte[] replyToClient() {
		// TODO Auto-generated method stub
		ResponseStrMaker response = new ResponseStrMaker();
		response.setId(protocolClientId);
		response.setBufferId(protocolBufferId);
		String message = new String();
		StackTraceElement[] stacks = new Throwable().getStackTrace(); 
		String classname = stacks[0].getClassName().substring(stacks[0].getClassName().lastIndexOf(".")+1);
		ProtocolPropertiesUtil p = new ProtocolPropertiesUtil();
		String findId = p.getIdByProtocol(classname);
		message += findId;
		message += longMessage;
		response.setMessageBody(message);
		String messageLength = "0000" + message.length()/2;
		messageLength = messageLength.substring(messageLength.length()-4);
		response.setLength(messageLength);
		response.setCheckNode(MessageUtil.buildCheckNode(response));
		MessageUtil.printAndToDivContent("发送:" + response.buildResponse(), true);
		byte[] replyStr = MessageUtil.buildOutputStream(response);
		return replyStr;
	}

	@Override
	public String getStrForDiv() {
		// TODO Auto-generated method stub
		return null;
	}

}
