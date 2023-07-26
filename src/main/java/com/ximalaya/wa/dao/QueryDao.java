package com.ximalaya.wa.dao;

import org.apache.ibatis.annotations.Insert;
import org.springframework.stereotype.Repository;

import com.ximalaya.wa.query.model.QueryCondition;

@Repository
public interface QueryDao {

	@Insert("Insert into ting.wa_query (opcode,criteria,created_at) values (#{opcode},#{criteria},#{createdAt})")
	public void insert(QueryCondition condition);
	
}
