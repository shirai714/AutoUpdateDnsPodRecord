# AutoUpdateDnsRecord
## 通过监控邮箱的异常邮件实现自动更新DnsPod的域名记录的IP(解决服务器动态IP问题)

external.yml的样例配置
```yml
spring:
  data:
    redis:

  mail:
    host: domain.com
    port: 995
    username: username
    password: password
    protocol: pop3s
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
rerecord:
  login_token: dnspod_login_token
  domain: domain
  lang: cn
  redisConfig:
    token: login_token
    domain: domain

mailConfig:
  lastSize: 0
  redisConfig:
    lastIpaddrKey: last_ip_addr
    lastSizeKey: last_size

```
