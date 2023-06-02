# AutoUpdateDnsRecord
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
  test:
    ip: ip

mailConfig:
  lastSize: 0
  redisConfig:
    lastIpaddrKey: last_ip_addr
    lastSizeKey: last_size

```
