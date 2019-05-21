# my_advanced_seckill
## 高并发秒杀项目
---

### 数据库：
MySQL

### 框架：
SpringBoot + MyBatis	 + RabbitMQ + Redis

### 关键技术
1 使用SpringBoot集成各种框架  
2 使用Mybatis进行数据库访问  
3 分布式session ：登陆时缓存用户数据session（cookie存放用户token，redis 缓存<token , userInfo>）  
4 明文密码二次加密  
5 JSR303参数校验  @Valid （@NotNull @Length 自定义验证器...)  
6 使用全局异常处理器（@ControllerAdvice @ExceptionHandler）  
7 热点数据缓存 ：使用Redis缓存页面（商品列表页），秒杀订单，秒杀商品库存等信息  
8 使用RabbitMQ进行秒杀请求排队  
9 页面静态化，前后端分离  
10 动态获取秒杀地址  
11 秒杀时加入验证码校验  
12 使用拦截器+自定义注解进行接口限流  
