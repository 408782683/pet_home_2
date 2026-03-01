package com.rain.controller;

import com.alibaba.fastjson2.JSONObject;
import com.rain.entity.Appointment;
import com.rain.entity.InsuranceClaim;
import com.rain.service.LifeServiceService;
import com.rain.service.OrderServiceEx;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@WebServlet(urlPatterns = {"/api/appointment/*", "/api/insurance/*", "/api/order/*"})
public class LifeServiceServlet extends BaseServlet {
    private final LifeServiceService service = new LifeServiceService();
    private final OrderServiceEx orderService = new OrderServiceEx();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        String path = req.getPathInfo();
        try {
            if (uri.startsWith("/api/appointment") && "/findByPage".equals(path)) appointmentByPage(req, resp);
            else if (uri.startsWith("/api/appointment") && "/status".equals(path)) appointmentStatus(req, resp);
            else if (uri.startsWith("/api/appointment") && path.startsWith("/cancel/")) appointmentCancel(req, resp, path);
            else if (uri.startsWith("/api/appointment") && "/add".equals(path)) appointmentAdd(req, resp);
            else if (uri.startsWith("/api/insurance") && "/findByPage".equals(path)) insuranceByPage(req, resp);
            else if (uri.startsWith("/api/insurance") && "/detail".equals(path)) insuranceDetail(req, resp);
            else if (uri.startsWith("/api/insurance") && "/orderListPage".equals(path)) insuranceOrderList(req, resp);
            else if (uri.startsWith("/api/insurance") && "/claimListPage".equals(path)) claimList(req, resp);
            else if (uri.startsWith("/api/insurance") && "/claim/submit".equals(path)) claimSubmit(req, resp);
            else if (uri.startsWith("/api/order") && "/listByPage".equals(path)) orderList(req, resp);
            else writeJson(resp, error("接口不存在"));
        } catch (Exception e) {
            e.printStackTrace();
            writeJson(resp, error("系统繁忙，请稍后重试"));
        }
    }

    private void appointmentByPage(HttpServletRequest req, HttpServletResponse resp) throws Exception { JSONObject p=JSONObject.parseObject(req.getReader().readLine()); JSONObject data=new JSONObject(); data.put("appointmentList", service.findAppointmentByPage(p.getIntValue("userId"), p.getString("hospitalName"), p.getString("status"), p.getIntValue("page"), p.getIntValue("size"))); data.put("total", service.countAppointment(p.getIntValue("userId"), p.getString("hospitalName"), p.getString("status"))); writeJson(resp, success(data)); }
    private void appointmentStatus(HttpServletRequest req, HttpServletResponse resp) throws Exception { JSONObject p=JSONObject.parseObject(req.getReader().readLine()); service.updateAppointmentStatus(p.getIntValue("id"), p.getString("status")); writeJson(resp, success("更新成功")); }
    private void appointmentCancel(HttpServletRequest req, HttpServletResponse resp, String path) throws Exception { Integer id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1)); service.cancelAppointment(id); writeJson(resp, success("取消成功")); }
    private void appointmentAdd(HttpServletRequest req, HttpServletResponse resp) throws Exception { JSONObject p=JSONObject.parseObject(req.getReader().readLine()); Appointment a = p.toJavaObject(Appointment.class); if(a.getStatus()==null) a.setStatus("待赴约"); service.addAppointment(a); writeJson(resp, success("预约成功")); }

    private void insuranceByPage(HttpServletRequest req, HttpServletResponse resp) throws Exception { JSONObject p=JSONObject.parseObject(req.getReader().readLine()); JSONObject data=new JSONObject(); data.put("insuranceList", service.findInsuranceByPage(p.getString("name"), p.getString("type"), p.getIntValue("page"), p.getIntValue("size"))); data.put("total", service.countInsurance(p.getString("name"), p.getString("type"))); writeJson(resp, success(data)); }
    private void insuranceDetail(HttpServletRequest req, HttpServletResponse resp) throws Exception { JSONObject data=new JSONObject(); data.put("insurance", service.findInsuranceById(Integer.parseInt(req.getParameter("id")))); writeJson(resp, success(data)); }
    private void insuranceOrderList(HttpServletRequest req, HttpServletResponse resp) throws Exception { JSONObject p=JSONObject.parseObject(req.getReader().readLine()); JSONObject data=new JSONObject(); data.put("policyList", service.findInsuranceOrderByPage(p.getIntValue("userId"), p.getIntValue("page"), p.getIntValue("size"))); data.put("total", service.countInsuranceOrder(p.getIntValue("userId"))); writeJson(resp, success(data)); }
    private void claimList(HttpServletRequest req, HttpServletResponse resp) throws Exception { JSONObject p=JSONObject.parseObject(req.getReader().readLine()); JSONObject data=new JSONObject(); data.put("claimList", service.findClaimByPage(p.getIntValue("userId"), p.getIntValue("page"), p.getIntValue("size"))); data.put("total", service.countClaim(p.getIntValue("userId"))); writeJson(resp, success(data)); }
    private void claimSubmit(HttpServletRequest req, HttpServletResponse resp) throws Exception { JSONObject p=JSONObject.parseObject(req.getReader().readLine()); InsuranceClaim c=p.toJavaObject(InsuranceClaim.class); String claimNo="CLM"+new SimpleDateFormat("yyyyMMdd").format(new Date())+(new Random().nextInt(900000)+100000); c.setClaimNo(claimNo); if(c.getStatus()==null) c.setStatus("待审核"); if(c.getClaimAmount()==null) c.setClaimAmount(new BigDecimal("0")); service.submitClaim(c); writeJson(resp, success("提交成功")); }

    private void orderList(HttpServletRequest req, HttpServletResponse resp) throws Exception { JSONObject p=JSONObject.parseObject(req.getReader().readLine()); writeJson(resp, success(orderService.findOrderListResult(p.getIntValue("userId"), p.getIntValue("page"), p.getIntValue("size"), p.getString("status")))); }
}
