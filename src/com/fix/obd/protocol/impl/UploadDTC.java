package com.fix.obd.protocol.impl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import com.fix.obd.protocol.ODBProtocol;
import com.fix.obd.protocol.ODBProtocolParser;
import com.fix.obd.util.FaultCodeXMLUtil;
import com.fix.obd.util.MessageUtil;
import com.fix.obd.util.ProtocolPropertiesUtil;
import com.fix.obd.web.model.util.FaultCodeIterator;
import com.fix.obd.web.service.TerminalServerService;
import com.fix.obd.web.util.ThtApplicationContext;

public class UploadDTC extends ODBProtocolParser implements ODBProtocol{
	private static final  Logger logger = Logger.getLogger(TerminalHeartbeat.class);
	private String clientId;
	private String bufferId;
	
	public UploadDTC(String messageStr){
		super(messageStr);
		MessageUtil.printAndToDivContent("收到来自终端" + this.getId() + "上传故障码",true);
	}

	@Override
	public boolean DBOperation(boolean DBif) {
		// TODO Auto-generated method stub
		this.clientId = this.getId();
		this.bufferId = this.getBufferId();
		
		String message = this.getRealMessage();
		int dtcNumber = Integer.valueOf(message.substring(0,2), 16);
		int stringIndex = 2;
		String allErrors = "";
		for(int i = 0 ; i < dtcNumber ; i++){
			String dtcString = "";
			for(int charIndex = 0 ; charIndex < 5 ; charIndex++){
				dtcString += (char)(int)Integer.valueOf(message.substring(stringIndex,stringIndex+2), 16);
				stringIndex += 2;
			}
			allErrors += dtcString + ",";
			String str = "解析来自终端的故障码-"+(i+1)+":" + dtcString;
			logger.info(str);
			strForDiv += MessageUtil.printAndToDivContent(str, false);
		}

		String info = "收到故障码";
		strForDiv += MessageUtil.printAndToDivContent(info, false);
		try {
			this.sentByXML(allErrors.substring(0,allErrors.lastIndexOf(",")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(DBif){
			TerminalServerService t = (TerminalServerService) ThtApplicationContext.getBean("terminalServerServiceImpl");
			t.addOBDLog(clientId, info, messageStr);
			if(allErrors.lastIndexOf(",")==-1)
				t.updateDTCDefect(clientId, "No Error");
			else
				t.updateDTCDefect(clientId, allErrors.substring(0,allErrors.lastIndexOf(",")));
		}
		return true;
	}

	@Override
	public byte[] replyToClient() {
		// TODO Auto-generated method stub
		StackTraceElement[] stacks = new Throwable().getStackTrace(); 
		String classname =  stacks[0].getClassName().substring(stacks[0].getClassName().lastIndexOf(".")+1);
		ProtocolPropertiesUtil p = new ProtocolPropertiesUtil();
		String operationId = p.getIdByProtocol(classname);
		ServerAck serverACK = new ServerAck(clientId,bufferId,operationId);
		return serverACK.replyToClient();
	}
	public String getStrForDiv(){
		return this.strForDiv;
	}
	public void sentByXML(String str) throws FileNotFoundException, IOException{
		Element root = new Element("faultcodexml");
		Document Doc = new Document(root);
		String[] characters = str.split(",");
		for(int i=0;i<characters.length;i++){
			Element elements = new Element("faultcode");
			elements.setAttribute("id", "" + i);
			elements.addContent(new Element("index").setText(characters[i]));
			FaultCodeXMLUtil f = new FaultCodeXMLUtil();
			ArrayList<FaultCodeIterator> list = f.parseByIndex(characters[i]);
			if(list.size()==0){
				elements.addContent(new Element("detail").setText("未识别的故障码"));
			}
			else{
				for(int j=0;j<list.size();j++)
					elements.addContent(new Element("detail").setText(list.get(j).getFaultDetail()));
			}
			root.addContent(elements);  
		}
		XMLOutputter XMLOut = new XMLOutputter();  
		XMLOut.output(Doc, new FileOutputStream("e://faultcode_to_apk.xml"));  
	}
}
