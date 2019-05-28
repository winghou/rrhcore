package com.model;

/**
 * 奖品类
 * @author Administrator
 *
 */
public class AppPrize {
	
	private Integer id;
	private String prizeName; 			//奖品名称
	private Integer remianingAmount;	//剩余数量
	private Integer usedAmount;			//已抽取数量
	private Integer weight;				//权重
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getPrizeName() {
		return prizeName;
	}
	public void setPrizeName(String prizeName) {
		this.prizeName = prizeName;
	}
	public Integer getRemianingAmount() {
		return remianingAmount;
	}
	public void setRemianingAmount(Integer remianingAmount) {
		this.remianingAmount = remianingAmount;
	}
	public Integer getUsedAmount() {
		return usedAmount;
	}
	public void setUsedAmount(Integer usedAmount) {
		this.usedAmount = usedAmount;
	}
	public Integer getWeight() {
		return weight;
	}
	public void setWeight(Integer weight) {
		this.weight = weight;
	}
	
	

}
