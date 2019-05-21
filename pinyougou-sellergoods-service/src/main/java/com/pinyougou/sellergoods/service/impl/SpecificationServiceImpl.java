package com.pinyougou.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.pojogroup.Specification;
import com.pinyougou.sellergoods.service.SpecificationService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {

	@Autowired
	private TbSpecificationMapper specificationMapper;
	
	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;


	/**
	 *保存行的方法
	 */
	public void save(Specification specification){
		TbSpecification tbSpecification = new TbSpecification();
		tbSpecification.setSpecName(specification.getSpecification().getSpecName());
	}
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void add(Specification specification) {
		TbSpecification tbspecification = specification.getSpecification();
		//逆向工程创建的方法封装了这个对象所对应的属性，所以可以直接调用该方法进行存储
		specificationMapper.insert(tbspecification);

		List<TbSpecificationOption> specificationOptionList = specification.getSpecificationOptionList();
		for (TbSpecificationOption tbSpecificationOption : specificationOptionList) {
			//切记不要忘记这一步，两个表的id属性需要进行关联
			tbSpecificationOption.setSpecId(tbspecification.getId());
			specificationOptionMapper.insert(tbSpecificationOption);
		}
	}

	/**
	 * 修改列表，但是不知道用户是新增还是删除，可以采用先删除整体数据再添加的方式去进行，因为这个功能不常进行
	 * @param specification
	 */
	@Override
	public void update(Specification specification){

		//先获取主键及对应的主表元素
		TbSpecification tbSpecification = specification.getSpecification();
		specificationMapper.updateByPrimaryKey(tbSpecification);

		//删除原来规格对应的规格选项
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		criteria.andSpecIdEqualTo(tbSpecification.getId());
		specificationOptionMapper.deleteByExample(example);

		List<TbSpecificationOption> specificationOptionList = specification.getSpecificationOptionList();
		for (TbSpecificationOption tbSpecificationOption : specificationOptionList) {
			tbSpecificationOption.setSpecId(tbSpecification.getId()); //设置规格ID
			specificationOptionMapper.insert(tbSpecificationOption);  //新增规格
		}
	}
	
	/**
	 * 根据ID获取实体,为修改来做铺垫
	 * @param id
	 * @return
	 */
	@Override
	public Specification findOne(Long id){
		Specification specification = new Specification();
		//查询数据库主表，获取主表相关数据
		TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
		specification.setSpecification(tbSpecification);

		//根据id相等的特性查询与主表id相同的多层表并封装到组合实体类
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
		criteria.andSpecIdEqualTo(id);
		List<TbSpecificationOption> tbSpecificationOptions = specificationOptionMapper.selectByExample(example);

		specification.setSpecificationOptionList(tbSpecificationOptions);

		return specification;
	}

	/**
	 * 批量删除
	 * 删除要考虑先删除关联表，再删除主表，否则会有外键干扰，不能进行删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids) {
			//这个example对象也是逆向工程创建的，里面的criteria封装了一些条件相关sql语句，各属性相等等api
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
			criteria.andSpecIdEqualTo(id);
			specificationOptionMapper.deleteByExample(example);

			specificationMapper.deleteByPrimaryKey(id);
		}
	}
	
	
	@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSpecificationExample example=new TbSpecificationExample();
		Criteria criteria = example.createCriteria();
		
		if(specification!=null){			
						if(specification.getSpecName()!=null && specification.getSpecName().length()>0){
				criteria.andSpecNameLike("%"+specification.getSpecName()+"%");
			}
	
		}
		
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

		@Override
		public List<Map> selectOptionList() {
			// TODO Auto-generated method stub
			return specificationMapper.selectOptionList();
		}
	
}
