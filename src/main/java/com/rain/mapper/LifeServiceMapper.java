package com.rain.mapper;

import com.rain.entity.Appointment;
import com.rain.entity.Insurance;
import com.rain.entity.InsuranceClaim;
import com.rain.entity.InsuranceOrder;
import com.rain.util.JdbcUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 生活服务模块 Mapper：预约、保险、保单、理赔
 */
public class LifeServiceMapper {

    /**
     * 分页查询预约记录
     */
    public List<Appointment> findAppointmentByPage(
            Integer userId,
            String hospitalName,
            String status,
            Integer page,
            Integer size
    ) throws SQLException {
        StringBuilder sql = new StringBuilder("select * from appointment where user_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (hospitalName != null && !hospitalName.isEmpty()) {
            sql.append(" and hospital_name like ?");
            params.add("%" + hospitalName + "%");
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" and status = ?");
            params.add(status);
        }

        sql.append(" order by create_time desc limit ?, ?");
        params.add((page - 1) * size);
        params.add(size);

        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bind(ps, params);
            ResultSet rs = ps.executeQuery();

            List<Appointment> list = new ArrayList<>();
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setId(rs.getInt("id"));
                appointment.setUserId(rs.getInt("user_id"));
                appointment.setHospitalId(rs.getInt("hospital_id"));
                appointment.setHospitalName(rs.getString("hospital_name"));
                appointment.setPetName(rs.getString("pet_name"));
                appointment.setPetType(rs.getString("pet_type"));
                appointment.setConditionDescription(rs.getString("condition_description"));
                appointment.setImages(rs.getString("images"));
                appointment.setAppointmentDate(rs.getDate("appointment_date"));
                appointment.setAppointmentTime(rs.getString("appointment_time"));
                appointment.setContactPhone(rs.getString("contact_phone"));
                appointment.setStatus(rs.getString("status"));
                appointment.setCreateTime(rs.getTimestamp("create_time"));
                list.add(appointment);
            }
            return list;
        }
    }

    /**
     * 统计预约数量
     */
    public Integer countAppointment(Integer userId, String hospitalName, String status) throws SQLException {
        StringBuilder sql = new StringBuilder("select count(*) num from appointment where user_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (hospitalName != null && !hospitalName.isEmpty()) {
            sql.append(" and hospital_name like ?");
            params.add("%" + hospitalName + "%");
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" and status = ?");
            params.add(status);
        }

        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bind(ps, params);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("num") : 0;
        }
    }

    /** 更新预约状态 */
    public void updateAppointmentStatus(Integer id, String status) throws SQLException {
        String sql = "update appointment set status = ? where id = ?";
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    /** 取消预约（删除） */
    public void cancelAppointment(Integer id) throws SQLException {
        String sql = "delete from appointment where id = ?";
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /** 新增预约 */
    public void addAppointment(Appointment appointment) throws SQLException {
        String sql = "insert into appointment(" +
                "user_id, hospital_id, hospital_name, pet_name, pet_type, condition_description, images, appointment_date, appointment_time, contact_phone, status, create_time" +
                ") values(?,?,?,?,?,?,?,?,?,?,?,now())";

        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, appointment.getUserId());
            ps.setInt(2, appointment.getHospitalId());
            ps.setString(3, appointment.getHospitalName());
            ps.setString(4, appointment.getPetName());
            ps.setString(5, appointment.getPetType());
            ps.setString(6, appointment.getConditionDescription());
            ps.setString(7, appointment.getImages());
            ps.setDate(8, new Date(appointment.getAppointmentDate().getTime()));
            ps.setString(9, appointment.getAppointmentTime());
            ps.setString(10, appointment.getContactPhone());
            ps.setString(11, appointment.getStatus());
            ps.executeUpdate();
        }
    }

    /**
     * 分页查询保险列表（按名称、价格区间）
     */
    public List<Insurance> findInsuranceByPage(
            String insuranceName,
            Double minPrice,
            Double maxPrice,
            Integer page,
            Integer size
    ) throws SQLException {
        StringBuilder sql = new StringBuilder("select * from insurance where 1 = 1");
        List<Object> params = new ArrayList<>();

        if (insuranceName != null && !insuranceName.isEmpty()) {
            sql.append(" and name like ?");
            params.add("%" + insuranceName + "%");
        }
        if (minPrice != null) {
            sql.append(" and price >= ?");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append(" and price <= ?");
            params.add(maxPrice);
        }

        sql.append(" order by create_time desc limit ?, ?");
        params.add((page - 1) * size);
        params.add(size);

        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bind(ps, params);
            ResultSet rs = ps.executeQuery();

            List<Insurance> list = new ArrayList<>();
            while (rs.next()) {
                Insurance insurance = mapInsurance(rs);
                list.add(insurance);
            }
            return list;
        }
    }

    /** 统计保险数量 */
    public Integer countInsurance(String insuranceName, Double minPrice, Double maxPrice) throws SQLException {
        StringBuilder sql = new StringBuilder("select count(*) num from insurance where 1 = 1");
        List<Object> params = new ArrayList<>();

        if (insuranceName != null && !insuranceName.isEmpty()) {
            sql.append(" and name like ?");
            params.add("%" + insuranceName + "%");
        }
        if (minPrice != null) {
            sql.append(" and price >= ?");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append(" and price <= ?");
            params.add(maxPrice);
        }

        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bind(ps, params);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("num") : 0;
        }
    }

    /** 根据 ID 查询保险详情 */
    public Insurance findInsuranceById(Integer id) throws SQLException {
        String sql = "select * from insurance where id = ?";
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? mapInsurance(rs) : null;
        }
    }

    /**
     * 分页查询保单列表（支持状态筛选）
     */
    public List<InsuranceOrder> findInsuranceOrderByPage(
            Integer userId,
            String status,
            Integer page,
            Integer size
    ) throws SQLException {
        StringBuilder sql = new StringBuilder("select * from insurance_order where user_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (status != null && !status.isEmpty()) {
            sql.append(" and status = ?");
            params.add(status);
        }

        sql.append(" order by create_time desc limit ?, ?");
        params.add((page - 1) * size);
        params.add(size);

        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bind(ps, params);
            ResultSet rs = ps.executeQuery();

            List<InsuranceOrder> list = new ArrayList<>();
            while (rs.next()) {
                InsuranceOrder order = new InsuranceOrder();
                order.setId(rs.getInt("id"));
                order.setOrderNo(rs.getString("order_no"));
                order.setUserId(rs.getInt("user_id"));
                order.setInsuranceId(rs.getInt("insurance_id"));
                order.setInsuranceName(rs.getString("insurance_name"));
                order.setPrice(rs.getBigDecimal("price"));
                order.setPetName(rs.getString("pet_name"));
                order.setPetType(rs.getString("pet_type"));
                order.setPetAge(rs.getInt("pet_age"));
                order.setPetBreed(rs.getString("pet_breed"));
                order.setPayeeName(rs.getString("payee_name"));
                order.setPayeePhone(rs.getString("payee_phone"));
                order.setPayeeAccount(rs.getString("payee_account"));
                order.setStartDate(rs.getDate("start_date"));
                order.setEndDate(rs.getDate("end_date"));
                order.setStatus(rs.getString("status"));
                order.setCreateTime(rs.getTimestamp("create_time"));
                list.add(order);
            }
            return list;
        }
    }

    /** 统计保单数量 */
    public Integer countInsuranceOrder(Integer userId, String status) throws SQLException {
        StringBuilder sql = new StringBuilder("select count(*) num from insurance_order where user_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (status != null && !status.isEmpty()) {
            sql.append(" and status = ?");
            params.add(status);
        }

        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bind(ps, params);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("num") : 0;
        }
    }

    /**
     * 分页查询理赔列表（支持状态筛选）
     */
    public List<InsuranceClaim> findClaimByPage(
            Integer userId,
            String status,
            Integer page,
            Integer size
    ) throws SQLException {
        StringBuilder sql = new StringBuilder("select * from insurance_claim where user_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (status != null && !status.isEmpty()) {
            sql.append(" and status = ?");
            params.add(status);
        }

        sql.append(" order by create_time desc limit ?, ?");
        params.add((page - 1) * size);
        params.add(size);

        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bind(ps, params);
            ResultSet rs = ps.executeQuery();

            List<InsuranceClaim> list = new ArrayList<>();
            while (rs.next()) {
                InsuranceClaim claim = new InsuranceClaim();
                claim.setId(rs.getInt("id"));
                claim.setClaimNo(rs.getString("claim_no"));
                claim.setUserId(rs.getInt("user_id"));
                claim.setOrderId(rs.getInt("order_id"));
                claim.setInsuranceName(rs.getString("insurance_name"));
                claim.setPetName(rs.getString("pet_name"));
                claim.setReason(rs.getString("reason"));
                claim.setDescription(rs.getString("description"));
                claim.setEvidenceImages(rs.getString("evidence_images"));
                claim.setClaimAmount(rs.getBigDecimal("claim_amount"));
                claim.setStatus(rs.getString("status"));
                claim.setRejectReason(rs.getString("reject_reason"));
                claim.setAdminRemark(rs.getString("admin_remark"));
                claim.setCreateTime(rs.getTimestamp("create_time"));
                list.add(claim);
            }
            return list;
        }
    }

    /** 统计理赔数量 */
    public Integer countClaim(Integer userId, String status) throws SQLException {
        StringBuilder sql = new StringBuilder("select count(*) num from insurance_claim where user_id = ?");
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (status != null && !status.isEmpty()) {
            sql.append(" and status = ?");
            params.add(status);
        }

        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bind(ps, params);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("num") : 0;
        }
    }

    /** 提交理赔申请 */
    public void submitClaim(InsuranceClaim claim) throws SQLException {
        String sql = "insert into insurance_claim(" +
                "claim_no, user_id, order_id, insurance_name, pet_name, reason, description, evidence_images, claim_amount, status, create_time" +
                ") values(?,?,?,?,?,?,?,?,?,?,now())";

        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, claim.getClaimNo());
            ps.setInt(2, claim.getUserId());
            ps.setInt(3, claim.getOrderId());
            ps.setString(4, claim.getInsuranceName());
            ps.setString(5, claim.getPetName());
            ps.setString(6, claim.getReason());
            ps.setString(7, claim.getDescription());
            ps.setString(8, claim.getEvidenceImages());
            ps.setBigDecimal(9, claim.getClaimAmount());
            ps.setString(10, claim.getStatus());
            ps.executeUpdate();
        }
    }

    /** 映射 insurance 记录到实体 */
    private Insurance mapInsurance(ResultSet rs) throws SQLException {
        Insurance insurance = new Insurance();
        insurance.setId(rs.getInt("id"));
        insurance.setName(rs.getString("name"));
        insurance.setDetail(rs.getString("detail"));
        insurance.setPrice(rs.getBigDecimal("price"));
        insurance.setCoverage(rs.getString("coverage"));
        insurance.setClaimNotice(rs.getString("claim_notice"));
        insurance.setClaimLimit(rs.getString("claim_limit"));
        insurance.setCreateTime(rs.getTimestamp("create_time"));
        return insurance;
    }

    /** 通用参数绑定 */
    private void bind(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
    }
}
