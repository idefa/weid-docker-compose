�
    @  t�         h 	      !        t  ��  �      //  0                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              ) �   �   @�) �   ��  @�A     �� �� A     �� �� �idx_un�idx_cn�idx_ct�idx_mt�                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             ��                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 �    �      	 partitione   PARTITION BY RANGE (TO_DAYS(create_time))
(PARTITION p1 VALUES LESS THAN (737606) ENGINE = InnoDB,
 PARTITION p2 VALUES LESS THAN (737790) ENGINE = InnoDB,
 PARTITION p3 VALUES LESS THAN (737972) ENGINE = InnoDB,
 PARTITION p4 VALUES LESS THAN (738156) ENGINE = InnoDB,
 PARTITION p5 VALUES LESS THAN (738337) ENGINE = InnoDB,
 PARTITION p6 VALUES LESS THAN (738521) ENGINE = InnoDB,
 PARTITION p7 VALUES LESS THAN (738702) ENGINE = InnoDB,
 PARTITION p8 VALUES LESS THAN (738886) ENGINE = InnoDB,
 PARTITION p9 VALUES LESS THAN (739067) ENGINE = InnoDB,
 PARTITION p99 VALUES LESS THAN MAXVALUE ENGINE = InnoDB)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        �                                           用户交易监管统计表                                                                                                                                                                                        � �  ��         P  	   � )                                          
user_name  
user_type  contract_name  contract_address  interface_name 	 trans_type 
 trans_unusual_type  trans_count  trans_hashs  trans_hash_lastest  create_time  modify_time 
E�   @   ! 
 � �   !! A��  @   ! >�   �   ! @` �  �   ! 	 * �   !- 
 + �   !?  , @   !	   0  �   ! <�2  �   !  � ��     � ��    �user_name�user_type�contract_name�contract_address�interface_name�trans_type�trans_unusual_type�trans_count�trans_hashs�trans_hash_lastest�create_time�modify_time� 用户名称用户类型(0-正常，1-异常)合约名称合约地址合约接口名交易类型(0-合约部署，1-接口调用)交易异常类型 (0-正常，1-异常合约，2-异常接口)交易量交易hashs(最多5个)最新交易hash创建时间修改时间