package com.ximalaya.wa.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.ximalaya.wa.manage.model.WaManage;

@Repository
public interface ManageDao {

	@Select("SELECT * FROM ting.tb_wa_manage where status=0")
	public List<WaManage> loadAllNotFinished();
		
	@Insert("INSERT INTO ting.tb_wa_manage (manage_key,manage_value,start_time,end_time,param_key,data,create_at,status) VALUES (#{manageKey},#{manageValue},#{startTime},#{endTime},#{paramKey},#{data},#{createAt},#{status})")
	public void insert(WaManage waManage);
	
	@Update("UPDATE ting.tb_wa_manage SET status = #{status} WHERE id = #{id}")
	public void update(WaManage waManage);
	
	@Select("SELECT count(*) FROM ting.tb_wa_manage where status=0")
	public int countAllNotFinished();
	
}
