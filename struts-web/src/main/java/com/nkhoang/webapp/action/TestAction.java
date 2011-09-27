package com.nkhoang.webapp.action;


import com.nkhoang.Constants;
import com.nkhoang.model.User;
import com.nkhoang.service.UserExistsException;
import com.nkhoang.webapp.util.RequestUtil;
import org.apache.struts2.ServletActionContext;
import org.springframework.mail.MailException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestAction extends BaseAction {

	private Map<String, List<String>> facilityMapping  = new HashMap<String, List<String>>();
	private Map<String, List<String>> facilityMapping2 = new HashMap<String, List<String>>();

	private Map<String, Map<String, String>> facilityMap  = new HashMap<String, Map<String, String>>();
	private Map<String, String>              facilityList = new HashMap<String, String>();
	private List<String> sampleList = new ArrayList<String>();
	private String optionId = "optionID-123";

	/**
	 * Default: just returns "success"
	 *
	 * @return "success"
	 */
	public String execute() {

		// add sample data.
		facilityMapping.put("FAC001", new ArrayList<String>());
		facilityMapping.get("FAC001").add("SUB001");
		facilityMapping.get("FAC001").add("SUB002");
		facilityMapping.get("FAC001").add("SUB003");
		facilityMapping.get("FAC001").add("SUB004");

		facilityMapping.put("FAC002", new ArrayList<String>());
		facilityMapping.get("FAC002").add("SUB005");
		facilityMapping.get("FAC002").add("SUB006");
		facilityMapping.get("FAC002").add("SUB007");
		facilityMapping.get("FAC002").add("SUB008");

		facilityList.put("1", "FAC001");
		facilityList.put("2", "FAC002");
		facilityList.put("3", "FAC003");

		facilityMap.put("1", new HashMap<String, String>());
		facilityMap.get("1").put("S1", "SUB001");
		facilityMap.get("1").put("S2", "SUB002");
		facilityMap.get("1").put("S3", "SUB003");
		facilityMap.get("1").put("S4", "SUB004");

		facilityMap.put("2", new HashMap<String, String>());
		facilityMap.get("2").put("S5", "SUB005");
		facilityMap.get("2").put("S6", "SUB006");
		facilityMap.get("2").put("S7", "SUB007");
		facilityMap.get("2").put("S8", "SUB008");

		// add empty list
		facilityMapping.put("FAC003", new ArrayList<String>());


		return SUCCESS;
	}

	public Map<String, List<String>> getFacilityMapping() {
		return facilityMapping;
	}

	public void setFacilityMapping(Map<String, List<String>> facilityMapping) {
		this.facilityMapping = facilityMapping;
	}

	public Map<String, List<String>> getFacilityMapping2() {
		return facilityMapping2;
	}

	public void setFacilityMapping2(Map<String, List<String>> facilityMapping2) {
		this.facilityMapping2 = facilityMapping2;
	}

	public Map<String, String> getFacilityList() {
		return facilityList;
	}

	public void setFacilityList(Map<String, String> facilityList) {
		this.facilityList = facilityList;
	}

	public Map<String, Map<String, String>> getFacilityMap() {
		return facilityMap;
	}

	public void setFacilityMap(Map<String, Map<String, String>> facilityMap) {
		this.facilityMap = facilityMap;
	}

	public String getOptionId() {
		return optionId;
	}

	public void setOptionId(String optionId) {
		this.optionId = optionId;
	}

	public List<String> getSampleList() {
		return sampleList;
	}

	public void setSampleList(List<String> sampleList) {
		this.sampleList = sampleList;
	}
}


