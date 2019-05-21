--table : seckill_user
create table seckill_user(
    id bigint(20) not null comment 'user id : phone number',
    nickname varchar(255) not null,
    password varchar(32) default null comment 'md5[md5[pwd + salt] + random_salt]',
    salt varchar(10) default null,
    head varchar(128) default null comment 'cloud store id of head picture',
    register_date datetime default null comment 'register date',
    last_login_date datetime default null comment 'last login date',
    login_count int(11) default '0' comment 'times of login',
    primary key(id)
)engine=innodb default charset=utf8mb4;

--table : 商品
--auto_increment=3 表示自增起始值为3
create table `goods`(
    `id` bigint(20) not null auto_increment comment '商品id',
    `goods_name` varchar(16) default null comment '商品名称',
    `goods_title` varchar(16) default null comment '商品标题',
    `goods_img` varchar(64) default null comment '商品图片',
    `goods_detail` varchar(255) default null comment '商品详情',
    `goods_price` decimal(10,2) default '0.00' comment '商品价格',
    `goods_stock` int(11) default '0' comment '商品库存,-1表示没有限制',
    primary key(`id`)
)engine=innodb auto_increment=3 default charset=utf8mb4;

--插入两条测试记录
INSERT INTO `goods` VALUES (1,'iphoneX','Apple iPhone X (A1865) 64GB 银色 移动联通电信4G手机','/img/iphonex.png','Apple iPhone X (A1865) 64GB 银色 移动联通电信4G手机',8765.00,10000),(2,'华为Meta9','华为 Mate 9 4GB+32GB版 月光银 移动联通电信4G手机 双卡双待','/img/meta10.png','华为 Mate 9 4GB+32GB版 月光银 移动联通电信4G手机 双卡双待',3212.00,-1);

--table : 秒杀商品表
create table `seckill_goods`(
    `id` bigint(20) not null auto_increment comment '秒杀商品id',
    `goods_id` bigint(20) default null comment '秒杀商品id',
    `seckill_price` decimal(10,2) default '0.00' comment '秒杀商品价格',
    `stock_count` int(11) default '0' comment '秒杀库存',
    `start_time` datetime default null comment '秒杀开始时间',
    `end_time` datetime default null comment '秒杀结束时间',
     primary key(`id`)
)engine=innodb auto_increment=3 default charset=utf8mb4;

--插入两条测试记录
INSERT INTO `seckill_goods` VALUES (1,1,0.01,4,'2019-05-01 15:18:00','2019-05-13 14:00:18'),(2,2,0.01,9,'2019-05-02 14:00:14','2019-05-13 14:00:24');

--订单详情
create table `order_info`(
    `id` bigint(20) not null auto_increment comment '订单id',
    `user_id` bigint(20) default null comment '用户id',
    `goods_id` bigint(20) default null comment '商品id',
    `deliver_addr_id`  bigint(20) default null comment '收货地址id',
    `goods_name` varchar(16) default null comment '冗余过来的商品名称',
    `goods_count` int(11) default '0' comment '商品数量',
    `goods_price` decimal(10,2) default '0.00' comment '商品价格',
    `order_channel` tinyint(4) default '0' comment '1:pc 2:android 3:ios',
    `status` tinyint(4) default '0' comment '订单状态 0:未支付 1:已支付 2:已发货 3:已收货 4:已退款 5:已完成',
    `create_time` datetime default null comment '订单创建时间',
    `pay_time` datetime default null comment '支付时间',
    primary key(id)
)engine=innodb auto_increment=12 default charset=utf8mb4;

create table `seckill_order`(
    `id` bigint(20) not null auto_increment comment '秒杀订单列表id',
    `user_id` bigint(20) default null comment '用户id',
    `order_id` bigint(20) default null comment '订单id',
    `goods_id` bigint(20) default null comment '商品id',
    primary key(id)
)engine=innodb auto_increment=3 default charset=utf8mb4;