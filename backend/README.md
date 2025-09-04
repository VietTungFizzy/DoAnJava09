# DoAnJava09

## Documents
- [Management Sheet](https://docs.google.com/spreadsheets/d/1OGw401A2sRBL35e9QRdpvOx_Rr5fjUA0r3McewKtZs4/edit?usp=sharing)


## How to run
### Run spring boot directly
1. `./mvnw clean package`
2. `./mvnw spring-boot:run`

### Building snapshot
1. `./mvnw clean package`
2. `./mvnw package`
3. Go to target folder and file *.jar
4. Run `jar -jar <file>`

## Requirements
- Tính năng login
  - Nếu đăng nhập sai 5 lần thì tạm khóa 15 phút. Sau 15 phút vẫn đăng nhập bình thường nếu đúng password nhưng nếu sai thì khóa tài khoản.
  - Nếu tài khoản bịkhóa phải báo tài khoản đang tạm khóa liên hệ admin
- Tính năng đăng ký, mua khóa học, subscribe,...
  - Khi đăng ký thành công thì phải gửi mail về email đã đăng ký
