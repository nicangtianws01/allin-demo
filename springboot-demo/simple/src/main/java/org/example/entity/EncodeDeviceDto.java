package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EncodeDeviceDto {
	private String regionIndexCode;
	private String regionPath;
	private String devSerialNum;
	private String dataVersion;
	private String ip;
	private String regionName;
	private String deviceKey;
	private String indexCode;
	private String description;
	private int isCascade;
	private String updateTime;
	private int sort;
	private String userName;
	private String treatyType;
	private String manufacturer;
	private int disOrder;
	private String netZoneId;
	private String capability;
	private String port;
	private String createTime;
	private String name;
	private String deviceModel;
	private String comId;
	private String resourceType;
}