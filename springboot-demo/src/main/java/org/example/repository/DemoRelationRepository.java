package org.example.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.entity.DemoEntity;
import org.example.entity.DemoRelation;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface DemoRelationRepository extends BaseMapper<DemoRelation> {

}
