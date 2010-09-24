package com.nkhoang.gae.view;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nkhoang.gae.gson.strategy.GSONStrategy;

public class JSONView implements View {
	public String getContentType() {
		return "application/json";
	}

	public void render(Map<String, ?> map, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PrintWriter writer = response.getWriter();
		List<String> excludeAttrs = (List<String>) map
				.get(GSONStrategy.EXCLUDE_ATTRIBUTES);

		Gson gson = null;
		if (excludeAttrs != null && excludeAttrs.size() > 0) {
			gson = new GsonBuilder().setExclusionStrategies(
					new GSONStrategy(excludeAttrs)).create();
		} else {
			gson = new Gson();
		}
		response.setContentType("application/json");
		writer.write(gson.toJson(map.get(GSONStrategy.DATA)));
	}
}
