package com.rain.controller;

import com.alibaba.fastjson2.JSONObject;
import com.rain.entity.BreedingPost;
import com.rain.entity.FosterPost;
import com.rain.service.PetCommunityService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = {"/api/adoption/*", "/api/breeding/*", "/api/foster/*", "/api/hospital/*"})
public class PetCommunityServlet extends BaseServlet {
    private final PetCommunityService service = new PetCommunityService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        String path = req.getPathInfo();
        try {
            if (uri.startsWith("/api/adoption") && "/listByPage".equals(path)) adoptionListByPage(req, resp);
            else if (uri.startsWith("/api/breeding") && "/listByPage".equals(path)) breedingListByPage(req, resp);
            else if (uri.startsWith("/api/breeding") && "/detail".equals(path)) breedingDetail(req, resp);
            else if (uri.startsWith("/api/breeding") && "/add".equals(path)) breedingAdd(req, resp);
            else if (uri.startsWith("/api/breeding") && "/update".equals(path)) breedingUpdate(req, resp);
            else if (uri.startsWith("/api/foster") && "/listByPage".equals(path)) fosterListByPage(req, resp);
            else if (uri.startsWith("/api/foster") && "/detail".equals(path)) fosterDetail(req, resp);
            else if (uri.startsWith("/api/foster") && "/add".equals(path)) fosterAdd(req, resp);
            else if (uri.startsWith("/api/foster") && "/update".equals(path)) fosterUpdate(req, resp);
            else if (uri.startsWith("/api/hospital") && "/findByPage".equals(path)) hospitalFindByPage(req, resp);
            else if (uri.startsWith("/api/hospital") && "/detail".equals(path)) hospitalDetail(req, resp);
            else writeJson(resp, error("接口不存在"));
        } catch (Exception e) {
            e.printStackTrace();
            writeJson(resp, error("系统繁忙，请稍后重试"));
        }
    }

    private void adoptionListByPage(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        JSONObject p = JSONObject.parseObject(req.getReader().readLine());
        Integer page = p.getIntValue("page"); Integer size = p.getIntValue("size");
        Integer currentUserId = p.getInteger("currentUserId");
        JSONObject data = new JSONObject();
        data.put("adoptionList", service.findAdoptionByPage(currentUserId, p.getString("breed"), p.getString("petType"), p.getString("petGender"), p.getString("publisher"), page, size));
        data.put("total", service.countAdoption(currentUserId, p.getString("breed"), p.getString("petType"), p.getString("petGender"), p.getString("publisher")));
        writeJson(resp, success(data));
    }
    private void breedingListByPage(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        JSONObject p = JSONObject.parseObject(req.getReader().readLine());
        Integer page = p.getIntValue("page"); Integer size = p.getIntValue("size"); Integer currentUserId = p.getInteger("currentUserId");
        JSONObject data = new JSONObject();
        data.put("breedingList", service.findBreedingByPage(currentUserId, p.getString("breed"), p.getString("petType"), p.getString("petGender"), p.getString("publisher"), page, size));
        data.put("total", service.countBreeding(currentUserId, p.getString("breed"), p.getString("petType"), p.getString("petGender"), p.getString("publisher")));
        writeJson(resp, success(data));
    }
    private void breedingDetail(HttpServletRequest req, HttpServletResponse resp) throws Exception { JSONObject data=new JSONObject(); data.put("post", service.findBreedingById(Integer.parseInt(req.getParameter("id")))); writeJson(resp, success(data)); }
    private void breedingAdd(HttpServletRequest req, HttpServletResponse resp) throws Exception { JSONObject p=JSONObject.parseObject(req.getReader().readLine()); BreedingPost x=p.toJavaObject(BreedingPost.class); service.addBreeding(x); writeJson(resp, success("新增成功")); }
    private void breedingUpdate(HttpServletRequest req, HttpServletResponse resp) throws Exception { JSONObject p=JSONObject.parseObject(req.getReader().readLine()); BreedingPost x=p.toJavaObject(BreedingPost.class); service.updateBreeding(x); writeJson(resp, success("更新成功")); }

    private void fosterListByPage(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        JSONObject p = JSONObject.parseObject(req.getReader().readLine());
        Integer page = p.getIntValue("page"); Integer size = p.getIntValue("size"); Integer currentUserId = p.getInteger("currentUserId");
        JSONObject data = new JSONObject();
        data.put("fosterList", service.findFosterByPage(currentUserId, p.getString("breed"), p.getString("petType"), p.getString("petGender"), p.getString("publisher"), page, size));
        data.put("total", service.countFoster(currentUserId, p.getString("breed"), p.getString("petType"), p.getString("petGender"), p.getString("publisher")));
        writeJson(resp, success(data));
    }
    private void fosterDetail(HttpServletRequest req, HttpServletResponse resp) throws Exception { JSONObject data=new JSONObject(); data.put("post", service.findFosterById(Integer.parseInt(req.getParameter("id")))); writeJson(resp, success(data)); }
    private void fosterAdd(HttpServletRequest req, HttpServletResponse resp) throws Exception { JSONObject p=JSONObject.parseObject(req.getReader().readLine()); FosterPost x=p.toJavaObject(FosterPost.class); service.addFoster(x); writeJson(resp, success("新增成功")); }
    private void fosterUpdate(HttpServletRequest req, HttpServletResponse resp) throws Exception { JSONObject p=JSONObject.parseObject(req.getReader().readLine()); FosterPost x=p.toJavaObject(FosterPost.class); service.updateFoster(x); writeJson(resp, success("更新成功")); }

    private void hospitalFindByPage(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        JSONObject p = JSONObject.parseObject(req.getReader().readLine());
        Integer page = p.getIntValue("page"); Integer size = p.getIntValue("size");
        JSONObject data = new JSONObject();
        data.put("hospitalList", service.findHospitalByPage(p.getString("name"), p.getString("type"), p.getString("level"), page, size));
        data.put("total", service.countHospital(p.getString("name"), p.getString("type"), p.getString("level")));
        writeJson(resp, success(data));
    }
    private void hospitalDetail(HttpServletRequest req, HttpServletResponse resp) throws Exception { JSONObject data=new JSONObject(); data.put("hospital", service.findHospitalById(Integer.parseInt(req.getParameter("id")))); writeJson(resp, success(data)); }
}
