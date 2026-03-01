package com.rain.mapper;

import com.rain.entity.*;
import com.rain.util.JdbcUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PetCommunityMapper {

    public List<AdoptionPost> findAdoptionByPage(Integer currentUserId, String breed, String petType, String petGender, String publisher, Integer page, Integer size) throws SQLException {
        StringBuilder sql = new StringBuilder("select * from adoption_post where 1=1 ");
        List<Object> params = new ArrayList<>();
        appendAdoptionFilter(sql, params, currentUserId, breed, petType, petGender, publisher);
        sql.append(" order by create_time desc limit ?,?");
        params.add((page - 1) * size);
        params.add(size);
        try (Connection conn = JdbcUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bindParams(ps, params);
            ResultSet rs = ps.executeQuery();
            List<AdoptionPost> list = new ArrayList<>();
            while (rs.next()) {
                AdoptionPost x = new AdoptionPost();
                x.setId(rs.getInt("id"));
                x.setUserId(rs.getInt("user_id"));
                x.setTitle(rs.getString("title"));
                x.setDescription(rs.getString("description"));
                x.setPetType(rs.getString("pet_type"));
                x.setPetGender(rs.getString("pet_gender"));
                x.setPetName(rs.getString("pet_name"));
                x.setBreed(rs.getString("breed"));
                x.setVaccineStatus(rs.getString("vaccine_status"));
                x.setPhotos(rs.getString("photos"));
                x.setAdoptionRequirement(rs.getString("adoption_requirement"));
                x.setLocation(rs.getString("location"));
                x.setOwnerInfo(rs.getString("owner_info"));
                x.setStatus(rs.getString("status"));
                x.setCreateTime(rs.getTimestamp("create_time"));
                list.add(x);
            }
            return list;
        }
    }

    public Integer countAdoption(Integer currentUserId, String breed, String petType, String petGender, String publisher) throws SQLException {
        StringBuilder sql = new StringBuilder("select count(*) num from adoption_post where 1=1 ");
        List<Object> params = new ArrayList<>();
        appendAdoptionFilter(sql, params, currentUserId, breed, petType, petGender, publisher);
        try (Connection conn = JdbcUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bindParams(ps, params);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("num") : 0;
        }
    }

    private void appendAdoptionFilter(StringBuilder sql, List<Object> params, Integer currentUserId, String breed, String petType, String petGender, String publisher) {
        if (breed != null && !breed.isEmpty()) { sql.append(" and breed like ?"); params.add("%" + breed + "%"); }
        if (petType != null && !petType.isEmpty()) { sql.append(" and pet_type = ?"); params.add(petType); }
        if (petGender != null && !petGender.isEmpty()) { sql.append(" and pet_gender = ?"); params.add(petGender); }
        if (publisher != null && currentUserId != null) {
            if ("my".equals(publisher)) { sql.append(" and user_id = ?"); params.add(currentUserId); }
            if ("other".equals(publisher)) { sql.append(" and user_id != ?"); params.add(currentUserId); }
        }
    }

    public List<BreedingPost> findBreedingByPage(Integer currentUserId, String breed, String petType, String petGender, String publisher, Integer page, Integer size) throws SQLException {
        StringBuilder sql = new StringBuilder("select * from breeding_post where 1=1 ");
        List<Object> params = new ArrayList<>();
        appendPetFilter(sql, params, currentUserId, breed, petType, petGender, publisher);
        sql.append(" order by create_time desc limit ?,?");
        params.add((page - 1) * size);
        params.add(size);
        try (Connection conn = JdbcUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bindParams(ps, params);
            ResultSet rs = ps.executeQuery();
            List<BreedingPost> list = new ArrayList<>();
            while (rs.next()) list.add(mapBreeding(rs));
            return list;
        }
    }

    public Integer countBreeding(Integer currentUserId, String breed, String petType, String petGender, String publisher) throws SQLException {
        StringBuilder sql = new StringBuilder("select count(*) num from breeding_post where 1=1 ");
        List<Object> params = new ArrayList<>();
        appendPetFilter(sql, params, currentUserId, breed, petType, petGender, publisher);
        try (Connection conn = JdbcUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bindParams(ps, params);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("num") : 0;
        }
    }

    public BreedingPost findBreedingById(Integer id) throws SQLException {
        try (Connection conn = JdbcUtil.getConnection(); PreparedStatement ps = conn.prepareStatement("select * from breeding_post where id=?")) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? mapBreeding(rs) : null;
        }
    }

    public void addBreeding(BreedingPost p) throws SQLException {
        String sql = "insert into breeding_post(user_id,title,description,pet_type,pet_gender,pet_name,breed,vaccine_status,photos,breeding_requirement,location,owner_info,status,create_time) values(?,?,?,?,?,?,?,?,?,?,?,?,?,now())";
        try (Connection conn = JdbcUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getUserId()); ps.setString(2, p.getTitle()); ps.setString(3, p.getDescription()); ps.setString(4, p.getPetType());
            ps.setString(5, p.getPetGender()); ps.setString(6, p.getPetName()); ps.setString(7, p.getBreed()); ps.setString(8, p.getVaccineStatus());
            ps.setString(9, p.getPhotos()); ps.setString(10, p.getBreedingRequirement()); ps.setString(11, p.getLocation()); ps.setString(12, p.getOwnerInfo()); ps.setString(13, p.getStatus());
            ps.executeUpdate();
        }
    }

    public void updateBreeding(BreedingPost p) throws SQLException {
        String sql = "update breeding_post set title=?,description=?,pet_type=?,pet_gender=?,pet_name=?,breed=?,vaccine_status=?,photos=?,breeding_requirement=?,location=?,owner_info=?,status=? where id=? and user_id=?";
        try (Connection conn = JdbcUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getTitle()); ps.setString(2, p.getDescription()); ps.setString(3, p.getPetType()); ps.setString(4, p.getPetGender()); ps.setString(5, p.getPetName());
            ps.setString(6, p.getBreed()); ps.setString(7, p.getVaccineStatus()); ps.setString(8, p.getPhotos()); ps.setString(9, p.getBreedingRequirement()); ps.setString(10, p.getLocation());
            ps.setString(11, p.getOwnerInfo()); ps.setString(12, p.getStatus()); ps.setInt(13, p.getId()); ps.setInt(14, p.getUserId());
            ps.executeUpdate();
        }
    }

    public List<FosterPost> findFosterByPage(Integer currentUserId, String breed, String petType, String petGender, String publisher, Integer page, Integer size) throws SQLException {
        StringBuilder sql = new StringBuilder("select * from foster_post where 1=1 ");
        List<Object> params = new ArrayList<>();
        appendPetFilter(sql, params, currentUserId, breed, petType, petGender, publisher);
        sql.append(" order by create_time desc limit ?,?");
        params.add((page - 1) * size); params.add(size);
        try (Connection conn = JdbcUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bindParams(ps, params);
            ResultSet rs = ps.executeQuery();
            List<FosterPost> list = new ArrayList<>();
            while (rs.next()) list.add(mapFoster(rs));
            return list;
        }
    }

    public Integer countFoster(Integer currentUserId, String breed, String petType, String petGender, String publisher) throws SQLException {
        StringBuilder sql = new StringBuilder("select count(*) num from foster_post where 1=1 ");
        List<Object> params = new ArrayList<>();
        appendPetFilter(sql, params, currentUserId, breed, petType, petGender, publisher);
        try (Connection conn = JdbcUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bindParams(ps, params);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("num") : 0;
        }
    }

    public FosterPost findFosterById(Integer id) throws SQLException {try (Connection conn = JdbcUtil.getConnection(); PreparedStatement ps = conn.prepareStatement("select * from foster_post where id=?")) {ps.setInt(1,id); ResultSet rs=ps.executeQuery(); return rs.next()?mapFoster(rs):null;}}
    public void addFoster(FosterPost p) throws SQLException {String sql="insert into foster_post(user_id,title,description,pet_type,pet_gender,pet_name,breed,vaccine_status,photos,foster_requirement,location,owner_info,status,create_time) values(?,?,?,?,?,?,?,?,?,?,?,?,?,now())"; try(Connection conn=JdbcUtil.getConnection(); PreparedStatement ps=conn.prepareStatement(sql)){ps.setInt(1,p.getUserId());ps.setString(2,p.getTitle());ps.setString(3,p.getDescription());ps.setString(4,p.getPetType());ps.setString(5,p.getPetGender());ps.setString(6,p.getPetName());ps.setString(7,p.getBreed());ps.setString(8,p.getVaccineStatus());ps.setString(9,p.getPhotos());ps.setString(10,p.getFosterRequirement());ps.setString(11,p.getLocation());ps.setString(12,p.getOwnerInfo());ps.setString(13,p.getStatus());ps.executeUpdate();}}
    public void updateFoster(FosterPost p) throws SQLException {String sql="update foster_post set title=?,description=?,pet_type=?,pet_gender=?,pet_name=?,breed=?,vaccine_status=?,photos=?,foster_requirement=?,location=?,owner_info=?,status=? where id=? and user_id=?";try(Connection conn=JdbcUtil.getConnection(); PreparedStatement ps=conn.prepareStatement(sql)){ps.setString(1,p.getTitle());ps.setString(2,p.getDescription());ps.setString(3,p.getPetType());ps.setString(4,p.getPetGender());ps.setString(5,p.getPetName());ps.setString(6,p.getBreed());ps.setString(7,p.getVaccineStatus());ps.setString(8,p.getPhotos());ps.setString(9,p.getFosterRequirement());ps.setString(10,p.getLocation());ps.setString(11,p.getOwnerInfo());ps.setString(12,p.getStatus());ps.setInt(13,p.getId());ps.setInt(14,p.getUserId());ps.executeUpdate();}}

    public List<Hospital> findHospitalByPage(String name, String services, String address, Integer page, Integer size) throws SQLException {
        StringBuilder sql = new StringBuilder("select * from hospital where 1=1 ");
        List<Object> params = new ArrayList<>();
        if (name != null && !name.isEmpty()) { sql.append(" and name like ?"); params.add("%" + name + "%"); }
        if (services != null && !services.isEmpty()) { sql.append(" and services like ?"); params.add("%" + services + "%"); }
        if (address != null && !address.isEmpty()) { sql.append(" and address like ?"); params.add("%" + address + "%"); }
        sql.append(" order by create_time desc limit ?,?"); params.add((page - 1) * size); params.add(size);
        try (Connection conn = JdbcUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bindParams(ps, params);
            ResultSet rs = ps.executeQuery();
            List<Hospital> list = new ArrayList<>();
            while (rs.next()) { Hospital h = new Hospital(); h.setId(rs.getInt("id")); h.setImages(rs.getString("images")); h.setName(rs.getString("name")); h.setIntroduction(rs.getString("introduction")); h.setServices(rs.getString("services")); h.setAddress(rs.getString("address")); h.setPhone(rs.getString("phone")); h.setBusinessHours(rs.getString("business_hours")); h.setCreateTime(rs.getTimestamp("create_time")); list.add(h); }
            return list;
        }
    }

    public Integer countHospital(String name, String services, String address) throws SQLException {
        StringBuilder sql = new StringBuilder("select count(*) num from hospital where 1=1 ");
        List<Object> params = new ArrayList<>();
        if (name != null && !name.isEmpty()) { sql.append(" and name like ?"); params.add("%" + name + "%"); }
        if (services != null && !services.isEmpty()) { sql.append(" and services like ?"); params.add("%" + services + "%"); }
        if (address != null && !address.isEmpty()) { sql.append(" and address like ?"); params.add("%" + address + "%"); }
        try (Connection conn = JdbcUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bindParams(ps, params); ResultSet rs = ps.executeQuery(); return rs.next() ? rs.getInt("num") : 0;
        }
    }

    public Hospital findHospitalById(Integer id) throws SQLException {try(Connection conn=JdbcUtil.getConnection(); PreparedStatement ps=conn.prepareStatement("select * from hospital where id=?")){ps.setInt(1,id); ResultSet rs=ps.executeQuery(); if(rs.next()){Hospital h=new Hospital(); h.setId(rs.getInt("id")); h.setImages(rs.getString("images")); h.setName(rs.getString("name")); h.setIntroduction(rs.getString("introduction")); h.setServices(rs.getString("services")); h.setAddress(rs.getString("address")); h.setPhone(rs.getString("phone")); h.setBusinessHours(rs.getString("business_hours")); h.setCreateTime(rs.getTimestamp("create_time")); return h;} return null;}}

    private void appendPetFilter(StringBuilder sql, List<Object> params, Integer currentUserId, String breed, String petType, String petGender, String publisher) {
        if (breed != null && !breed.isEmpty()) { sql.append(" and breed like ?"); params.add("%" + breed + "%"); }
        if (petType != null && !petType.isEmpty()) { sql.append(" and pet_type = ?"); params.add(petType); }
        if (petGender != null && !petGender.isEmpty()) { sql.append(" and pet_gender = ?"); params.add(petGender); }
        if (publisher != null && currentUserId != null) {
            if ("my".equals(publisher)) { sql.append(" and user_id = ?"); params.add(currentUserId); }
            if ("other".equals(publisher)) { sql.append(" and user_id != ?"); params.add(currentUserId); }
        }
    }

    private BreedingPost mapBreeding(ResultSet rs) throws SQLException { BreedingPost x = new BreedingPost(); x.setId(rs.getInt("id")); x.setUserId(rs.getInt("user_id")); x.setTitle(rs.getString("title")); x.setDescription(rs.getString("description")); x.setPetType(rs.getString("pet_type")); x.setPetGender(rs.getString("pet_gender")); x.setPetName(rs.getString("pet_name")); x.setBreed(rs.getString("breed")); x.setVaccineStatus(rs.getString("vaccine_status")); x.setPhotos(rs.getString("photos")); x.setBreedingRequirement(rs.getString("breeding_requirement")); x.setLocation(rs.getString("location")); x.setOwnerInfo(rs.getString("owner_info")); x.setStatus(rs.getString("status")); x.setCreateTime(rs.getTimestamp("create_time")); return x; }
    private FosterPost mapFoster(ResultSet rs) throws SQLException { FosterPost x = new FosterPost(); x.setId(rs.getInt("id")); x.setUserId(rs.getInt("user_id")); x.setTitle(rs.getString("title")); x.setDescription(rs.getString("description")); x.setPetType(rs.getString("pet_type")); x.setPetGender(rs.getString("pet_gender")); x.setPetName(rs.getString("pet_name")); x.setBreed(rs.getString("breed")); x.setVaccineStatus(rs.getString("vaccine_status")); x.setPhotos(rs.getString("photos")); x.setFosterRequirement(rs.getString("foster_requirement")); x.setLocation(rs.getString("location")); x.setOwnerInfo(rs.getString("owner_info")); x.setStatus(rs.getString("status")); x.setCreateTime(rs.getTimestamp("create_time")); return x; }

    private void bindParams(PreparedStatement ps, List<Object> params) throws SQLException { for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i)); }
}
