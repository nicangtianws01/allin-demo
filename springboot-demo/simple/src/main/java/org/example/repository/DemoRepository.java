package org.example.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.entity.DemoEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface DemoRepository extends BaseMapper<DemoEntity> {
    @Select("select * from demo_entity where id = #{id} for update")
    DemoEntity lockById(Long id);

    @Select("select d.id, dr.id as did, d.name from demo_entity d left join demo_relation dr on d.id = dr.did where d.id = #{id} for update")
    DemoEntity lockById01(Long id);
}
