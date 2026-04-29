# Deployment Guide (Render + PostgreSQL)

## 1) Mục tiêu
Triển khai API Genealogy lên Render với:
- **Web Service** chạy Spring Boot
- **Managed PostgreSQL** trên Render
- Migration tự động bằng Flyway khi app khởi động

---

## 2) Cấu hình profile
Project đã tách config thành 3 file:

- `application.properties`: cấu hình dùng chung, env-friendly.
- `application-local.properties`: cấu hình chạy local.
- `application-deploy.properties`: cấu hình deploy trên Render.

Khi deploy, dùng:

```bash
SPRING_PROFILES_ACTIVE=deploy
```

---

## 3) Biến môi trường cần có trên Render

### Bắt buộc
- `SPRING_PROFILES_ACTIVE=deploy`
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `APP_JWT_SECRET`

### Khuyến nghị
- `APP_JWT_EXPIRATION=86400000`
- `SPRING_JPA_SHOW_SQL=false`
- `SPRING_JPA_FORMAT_SQL=false`
- `MANAGEMENT_HEALTH_SHOW_DETAILS=never`

> `server.port` đã được cấu hình đọc từ `PORT` trong profile deploy.

---

## 4) Cấu trúc thư mục deploy

Để tách biệt artifact triển khai khỏi mã ứng dụng, các file deploy được đặt trong thư mục `deploy/`:

- `deploy/Dockerfile`
- `deploy/render.yaml`

---

## 5) Render Blueprint (`deploy/render.yaml`)

Repo đã có sẵn `deploy/render.yaml` để Render tạo đồng thời:
- 1 Web Service (`genealogy-api`)
- 1 PostgreSQL database (`genealogy-postgres`)

Các biến DB được map tự động từ resource database sang web service.

---

## 6) Quy trình deploy

1. Push code lên GitHub/GitLab.
2. Vào Render → **New** → **Blueprint**.
3. Chọn repo và trỏ tới blueprint file: `deploy/render.yaml`.
4. Render sẽ provision database + web service.
5. Theo dõi log lần khởi động đầu tiên:
   - App start thành công
   - Flyway chạy migration `V1__Initial_schema.sql` thành công
6. Kiểm tra health check:
   - `GET /actuator/health`

---

## 7) Kiểm tra sau deploy (post-deploy)

- API trả về 200 cho endpoint health.
- App kết nối DB thành công (không lỗi datasource timeout/auth).
- Không lộ thông tin nhạy cảm trong logs.
- JWT secret đã được set bằng secret value, không dùng default fallback.

---

## 8) Rollback nhanh

Nếu release lỗi:
1. Trên Render, chọn service `genealogy-api`.
2. Vào tab **Events** hoặc **Deploys**.
3. Chọn deploy ổn định trước đó và **Rollback**.
4. Kiểm tra lại `/actuator/health`.

---

## 9) Local run (tham khảo)

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

Hoặc set env:

```bash
SPRING_PROFILES_ACTIVE=local ./gradlew bootRun
```
