# RogeBox Game

RogeBox là một tựa game 2D được phát triển bằng Java với framework **LibGDX**.

## 🚀 Hướng dẫn Clone và Chạy game

### 1. Clone Project
Mở Terminal / Command Prompt và chạy lệnh sau để tải source code về máy:
```bash
git clone <đường-dẫn-repo-của-bạn>
cd game2
```
*(Thay `<đường-dẫn-repo-của-bạn>` bằng link Git của bạn).*

### 2. Yêu cầu hệ thống
- Đã cài đặt **Java Development Kit (JDK)** bản 11 hoặc mới hơn.
- Không cần cài thêm Gradle vì project đã tích hợp sẵn Gradle Wrapper (`gradlew`).

### 3. Chạy Game (Desktop)
Bạn có thể chạy trực tiếp game trên máy tính thông qua Gradle wrapper bằng lệnh sau:

**Trên Windows:**
```bash
gradlew.bat lwjgl3:run
```

**Trên macOS / Linux:**
```bash
./gradlew lwjgl3:run
```

Lệnh này sẽ tự động tải các thư viện cần thiết, biên dịch code và bật cửa sổ game lên cho bạn.

---
**💡 Chạy bằng IDE (Tùy chọn)**
Nếu bạn sử dụng IntelliJ IDEA hoặc Android Studio:
1. Mở IDE -> Chọn **Open**.
2. Trỏ tới thư mục `game2` vừa clone.
3. Chờ IDE đồng bộ Gradle (sync).
4. Tìm đến Run Configuration, chạy task `lwjgl3:run` hoặc chạy hàm `main` trong class `Lwjgl3Launcher`.
