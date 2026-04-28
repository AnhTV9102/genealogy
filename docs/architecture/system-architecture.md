# 🏗️ Kiến Trúc Hệ Thống Genealogy

## 🎯 Tổng Quan Hệ Thống

Hệ thống backend Genealogy được thiết kế để quản lý dữ liệu gia phả (cây gia đình) với các tính năng chính:

- Quản lý cá nhân (Person)
- Mô hình hóa mối quan hệ (cha mẹ, vợ chồng, v.v.)
- Duyệt cây gia đình (tổ tiên / hậu duệ)
- Kiểm soát truy cập (RBAC)
- Khả năng mở rộng cho tập dữ liệu lớn dạng đồ thị

## 🧠 Phong Cách Kiến Trúc

Hệ thống tuân theo **Clean Architecture (Lite) + MVC (Spring Web)** với các nguyên tắc chính:

- Logic nghiệp vụ độc lập với framework
- Lớp domain không phụ thuộc vào cơ sở dữ liệu hoặc hạ tầng
- Các phụ thuộc luôn hướng vào trong

## 🧱 Kiến Trúc Cao Cấp

```
┌──────────────┐
│     API      │  (Spring MVC - Controllers)
└──────┬───────┘
       ↓
┌──────────────┐
│ Application  │  (Use Cases / Services)
└──────┬───────┘
       ↓
┌──────────────┐
│    Domain    │  (Core Business Logic - Pure Java)
└──────┬───────┘
       ↑
┌──────────────┐
│Infrastructure│  (Database, JPA, External Systems)
└──────────────┘
```

## 📦 Cấu Trúc Module Dựa Trên Tính Năng

Hệ thống được tổ chức theo **tính năng/domain**, không phải theo lớp kỹ thuật.

```
com.genealogy
 ├── person
 ├── relationship
 ├── family
 ├── tree
 ├── security
 ├── common
 └── config
```

Mỗi module tự chứa và tuân theo cùng một mẫu kiến trúc.

## 🧩 Cấu Trúc Module Chuẩn

Ví dụ: module `person`

```
person/
 ├── api              # REST Controllers (MVC layer)
 ├── application      # Use cases / orchestration
 │    ├── service
 │    ├── dto
 │    └── mapper
 │
 ├── domain           # Core business logic (pure Java)
 │    ├── model
 │    └── repository
 │
 └── infrastructure   # Persistence & external integrations
      ├── entity
      ├── repository
      └── mapper
```

## 🧠 Trách Nhiệm Các Lớp

### 1. Lớp API (Controller)

- Xử lý yêu cầu/phản hồi HTTP
- Thực hiện validation cơ bản và mapping
- Ủy quyền cho application services
- **Không chứa logic nghiệp vụ**

### 2. Lớp Application

- Triển khai use cases (vd: CreatePerson)
- Điều phối các hoạt động domain
- Chuyển đổi giữa DTOs và domain models
- Không chứa quy tắc nghiệp vụ phức tạp

### 3. Lớp Domain (Core)

- Chứa **quy tắc nghiệp vụ và invariants**
- Độc lập với framework (không Spring, không JPA)
- Triển khai **rich domain models**

Ví dụ:

```java
person.changeName("John Doe");
person.markAsDeceased(date);
```

✔ Thực thi quy tắc (vd: tên hợp lệ, ngày hợp lệ)  
✔ Bảo vệ trạng thái nội bộ

### 4. Lớp Infrastructure

- Xử lý các vấn đề kỹ thuật:
  - Cơ sở dữ liệu (JPA)
  - External APIs
- Mapping:
  - Entity ↔ Domain
- Triển khai repository interfaces được định nghĩa trong domain

## 🔄 Luồng Dữ Liệu

```
Client Request
     ↓
Controller (API)
     ↓
Application Service
     ↓
Domain Model
     ↓
Repository (interface)
     ↓
Repository Implementation (Infrastructure)
     ↓
Database
```

## 🔗 Mô Hình Quan Hệ (Graph-Based)

Hệ thống mô hình hóa gia phả như một **đồ thị có hướng**:

- **Node** → Person
- **Edge** → Relationship

```
(Person A) ──PARENT_OF──▶ (Person B)
(Person A) ──SPOUSE_OF──▶ (Person C)
```

### Các Loại Quan Hệ

- PARENT_OF
- CHILD_OF
- SPOUSE_OF
- ADOPTED_PARENT_OF

### Thiết Kế Cơ Sở Dữ Liệu

Bảng `relationships`:

```sql
id BIGINT PK
from_person_id BIGINT
to_person_id BIGINT
type VARCHAR
subtype VARCHAR
start_date DATE
end_date DATE
created_at TIMESTAMP
```

Với các chỉ mục cần thiết cho việc duyệt nhanh.

## 🛠️ Công Nghệ Sử Dụng

- **Framework**: Spring Boot 4.0.6
- **Ngôn ngữ**: Java 21
- **Cơ sở dữ liệu**: PostgreSQL
- **ORM**: JPA (Spring Data JPA)
- **Migration**: Flyway
- **Authentication**: JWT (io.jsonwebtoken)
- **Monitoring**: Spring Boot Actuator
- **Development**: Lombok, Spring Boot DevTools
- **Testing**: JUnit, Spring Security Test

## 🚀 Khả Năng Mở Rộng

Kiến trúc này hỗ trợ:

### 🔹 Mở Rộng Tính Năng

- Các module mới có thể được thêm độc lập

### 🔹 Mở Rộng Domain

- Đồ thị quan hệ phức tạp
- Thuật toán duyệt cây (DFS/BFS)

### 🔹 Mở Rộng Hệ Thống

- Có thể phát triển thành microservices
- Cơ sở dữ liệu có thể thay thế mà không ảnh hưởng domain

## ⚖️ Quyết Định Thiết Kế

### ✅ Cấu Trúc Dựa Trên Tính Năng

Thay vì:

```
controller/
service/
repository/
```

Sử dụng:

```
person/
relationship/
```

Lợi ích:

- Tính module hóa tốt hơn
- Dễ dàng mở rộng
- Sở hữu rõ ràng

### ✅ Clean Architecture (Lite)

Thực thi:

- Cô lập domain
- Hướng phụ thuộc
- Tách biệt trách nhiệm

Tránh:

- Quá kỹ thuật
- Trừu tượng quá mức

## 🗺️ Lộ Trình Kiến Trúc

### Giai Đoạn 1 (Hiện Tại)

- Module Person
- Clean Architecture Lite

#### 📋 API Endpoints cho Module Person

Dựa trên PersonController hiện tại:

- **POST** `/api/v1/persons` - Tạo người mới
  - Request: `CreatePersonRequest` (name, gender, birthDate, etc.)
  - Response: `PersonResponse`

- **GET** `/api/v1/persons/{id}` - Lấy thông tin người theo ID ✅ **Đã triển khai**
  - Response: `PersonResponse`

- **PUT** `/api/v1/persons/{id}` - Cập nhật thông tin người ✅ **Đã triển khai**
  - Request: `UpdatePersonRequest` (fullName, dateOfDeath)
  - Response: `PersonResponse`

- **DELETE** `/api/v1/persons/{id}` - Xóa người ✅ **Đã triển khai**
  - Response: `204 No Content`

- **GET** `/api/v1/persons` - Liệt kê tất cả người (có phân trang) ✅ **Đã triển khai**
  - Query params: `page=0&size=10` (default Spring Boot Pageable)
  - Response: `Page<PersonResponse>` (với metadata phân trang)

### Giai Đoạn 2

- Module Relationship (logic đồ thị cốt lõi)

#### 📋 API Endpoints cho Module Relationship

##### 🔗 Quản Lý Mối Quan Hệ Cơ Bản

- **POST** `/api/v1/relationships` - Tạo mối quan hệ mới ✅ **Đã triển khai**
  - Request: `CreateRelationshipRequest` (fromPersonId, toPersonId, type)
  - Response: `RelationshipResponse`
  - Validation: Kiểm tra tồn tại của cả hai person, không tạo quan hệ trùng lặp

- **GET** `/api/v1/relationships/{id}` - Lấy thông tin mối quan hệ theo ID ✅ **Đã triển khai**
  - Response: `RelationshipResponse`

- **DELETE** `/api/v1/relationships/{id}` - Xóa mối quan hệ ✅ **Đã triển khai**
  - Response: `204 No Content`

##### 👨‍👩‍👧‍👦 Quan Hệ Gia Đình

- **GET** `/api/v1/persons/{personId}/relationships` - Lấy tất cả mối quan hệ của một người ✅ **Đã triển khai**
  - Query params: `type` (optional filter), `direction` (INCOMING/OUTGOING/BOTH)
  - Response: `List<RelationshipResponse>`

- **GET** `/api/v1/persons/{personId}/parents` - Lấy danh sách cha mẹ ✅ **Đã triển khai**
  - Response: `List<PersonResponse>`

- **GET** `/api/v1/persons/{personId}/children` - Lấy danh sách con cái ✅ **Đã triển khai**
  - Response: `List<PersonResponse>`

- **GET** `/api/v1/persons/{personId}/spouse` - Lấy vợ/chồng (nếu có) ✅ **Đã triển khai**
  - Response: `PersonResponse` (hoặc null)

##### 🌳 Duyệt Cây Gia Phả (Giai Đoạn 3 Preview)

- **GET** `/api/v1/persons/{personId}/ancestors` - Lấy tất cả tổ tiên
  - Query params: `generations` (optional limit)
  - Response: `List<PersonResponse>` (theo thứ tự thế hệ)

- **GET** `/api/v1/persons/{personId}/descendants` - Lấy tất cả hậu duệ
  - Query params: `generations` (optional limit)
  - Response: `List<PersonResponse>` (theo thứ tự thế hệ)

##### 🔍 Tìm Kiếm & Validation

- **GET** `/api/v1/relationships/validate` - Validate mối quan hệ tiềm năng
  - Query params: `fromPersonId`, `toPersonId`, `type`
  - Response: `ValidationResult` (isValid, conflicts, suggestions)

- **GET** `/api/v1/relationships/search` - Tìm kiếm mối quan hệ
  - Query params: `personId`, `type`, `page`, `size`
  - Response: `Page<RelationshipResponse>`

### Giai Đoạn 3

- Duyệt cây (tổ tiên / hậu duệ)

### Giai Đoạn 4

- Tối ưu hiệu năng (caching, indexing)
- Bảo mật (RBAC)

## 🧩 Tóm Tắt

Kiến trúc hệ thống kết hợp:

- ✔ Cấu trúc module dựa trên tính năng
- ✔ Clean Architecture (Lite)
- ✔ MVC cho lớp API

Đảm bảo:

- Khả năng bảo trì lâu dài
- Khả năng mở rộng cho logic gia phả phức tạp
- Tách biệt trách nhiệm rõ ràng
