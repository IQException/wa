package com.ximalaya.wa.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.ximalaya.wa.monitor.model.Condition;

@Repository
public interface MonitorDao {

	@Select("SELECT * FROM ting.wa_monitor ")
	public List<Condition> loadAll();
	
	@Select("SELECT * FROM ting.wa_monitor where opcode=#{opcode} and condition_key=#{conditionKey} and condition_value=#{conditionValue}")
	public Condition selectByCriteria(@Param("opcode")String opcode, @Param("conditionKey")String conditionKey, @Param("conditionValue")String conditionValue);
	
	@Delete("Delete FROM ting.wa_monitor where opcode=#{opcode} and condition_key=#{conditionKey} and condition_value=#{conditionValue}")
	public void delete(Condition condition);
	
	@Insert("Insert into ting.wa_monitor (opcode,condition_key,condition_value,pm_id,create_at) values ( #{opcode},#{conditionKey},#{conditionValue},#{pmId},#{createAt})")
	public void insert(Condition condition);
	
	
}
