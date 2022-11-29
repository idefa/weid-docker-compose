/*
 *       Copyright© (2018-2020) WeBank Co., Ltd.
 *
 *       This file is part of weidentity-build-tools.
 *
 *       weidentity-build-tools is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weidentity-build-tools is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weidentity-build-tools.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.dto;

import java.util.Date;

import lombok.Data;

@Data
public class ConvergeInfo {
    // 汇聚编号
    private String convergeId;
    // 文件路径
    private String filePath;
    //数据来源Ip
    private String formIp;
    // 文件时间
    private String dataTime;
    // 汇聚状态，0初始状态，1失败，2成功
    private int status;
    
    private Date createTime;
    private Date updateTime;
}