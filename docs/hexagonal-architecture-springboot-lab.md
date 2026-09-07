# Spring Boot Hexagonal Architecture Lab

## Mục tiêu

Tài liệu này được thiết kế theo dạng **một project được nâng cấp dần qua nhiều bài tập**, giúp bạn nắm cách tổ chức và xây dựng ứng dụng Spring Boot theo **Ports & Adapters / Hexagonal Architecture**.

Thay vì học lý thuyết rời rạc, bạn sẽ xây dựng một hệ thống **Order Management Service** từ đơn giản đến gần production.

Sau khi hoàn thành, bạn cần hiểu được:

- Domain nằm ở đâu và tại sao nên hạn chế phụ thuộc Spring/JPA.
- Inbound Port và Outbound Port khác nhau thế nào.
- Adapter là gì và tại sao REST, JPA, Kafka, gRPC đều có thể xem là Adapter.
- Dependency Direction trong Hexagonal Architecture.
- Khi nào nên tạo Port, khi nào không cần.
- Cách test business logic mà không cần khởi động Spring Context.
- Cách mở rộng từ CRUD đơn giản sang service có DB, external API, message broker và transaction.

---

# 1. Bài toán xuyên suốt

Bạn sẽ xây dựng một service quản lý đơn hàng.

Luồng nghiệp vụ cuối cùng:

```text
Client
  |
  v
REST API
  |
  v
CreateOrder Use Case
  |
  +--> kiểm tra Customer
  |
  +--> kiểm tra Inventory
  |
  +--> tính tổng tiền
  |
  +--> lưu Order
  |
  +--> phát OrderCreated event
  v
Response
```

Các thành phần hạ tầng sẽ được thêm dần:

```text
REST
PostgreSQL
External Customer Service
External Inventory Service
Kafka/RabbitMQ
Redis (optional)
```

---

# 2. Tư duy cốt lõi cần giữ trong toàn bộ bài tập

Hexagonal Architecture không phải chỉ là đổi tên package.

Điểm quan trọng nhất là **Dependency Direction**.

```text
Infrastructure
     |
     v
Application / Domain
```

Business không nên phụ thuộc trực tiếp vào:

```text
Spring MVC
Spring Data JPA
KafkaTemplate
RestTemplate/WebClient
PostgreSQL
Redis
```

Business chỉ nên biết những abstraction cần thiết như:

```text
OrderRepositoryPort
CustomerQueryPort
InventoryPort
OrderEventPublisherPort
```

Implementation cụ thể nằm ở Adapter.

---

# 3. Cấu trúc project mục tiêu

Không cần áp dụng ngay từ bài đầu. Đây là cấu trúc bạn sẽ tiến tới.

```text
src/main/java/com/example/order
|
+-- domain
|   +-- model
|   |   +-- Order.java
|   |   +-- OrderItem.java
|   |   +-- OrderStatus.java
|   |
|   +-- exception
|
+-- application
|   +-- port
|   |   +-- in
|   |   |   +-- CreateOrderUseCase.java
|   |   |   +-- GetOrderUseCase.java
|   |   |
|   |   +-- out
|   |       +-- OrderRepositoryPort.java
|   |       +-- CustomerQueryPort.java
|   |       +-- InventoryPort.java
|   |       +-- OrderEventPublisherPort.java
|   |
|   +-- service
|       +-- CreateOrderService.java
|       +-- GetOrderService.java
|
+-- adapter
|   +-- in
|   |   +-- web
|   |       +-- OrderController.java
|   |
|   +-- out
|       +-- persistence
|       |   +-- OrderJpaEntity.java
|       |   +-- SpringDataOrderRepository.java
|       |   +-- OrderPersistenceAdapter.java
|       |
|       +-- customer
|       |   +-- CustomerRestAdapter.java
|       |
|       +-- inventory
|       |   +-- InventoryGrpcAdapter.java
|       |
|       +-- messaging
|           +-- KafkaOrderEventAdapter.java
|
+-- config
    +-- BeanConfiguration.java
```

Không cần tuyệt đối tuân thủ đúng tên package trên. Điều quan trọng là boundary và dependency direction.

---

# Bài 0 - Xây dựng bản Layered Architecture thông thường

## Mục tiêu

Trước khi học Hexagonal, hãy tạo một phiên bản Spring Boot quen thuộc để thấy vấn đề cần giải quyết.

## Yêu cầu

Tạo API:

```http
POST /orders
GET /orders/{id}
```

Model đơn giản:

```text
Order
- id
- customerId
- items
- totalAmount
- status
```

Cấu trúc:

```text
controller
service
repository
entity
```

Ví dụ dependency:

```text
OrderController
      |
      v
OrderService
      |
      v
OrderRepository
      |
      v
PostgreSQL/H2
```

## Việc cần làm

1. Tạo `OrderController`.
2. Tạo `OrderService`.
3. Tạo JPA Entity.
4. Tạo `JpaRepository`.
5. Lưu và đọc Order.

## Câu hỏi sau bài tập

Hãy tự trả lời:

- `OrderService` có phụ thuộc Spring không?
- Business model có đang dùng `@Entity` không?
- Nếu bỏ PostgreSQL và dùng MongoDB, phần nào phải thay đổi?
- Nếu muốn test business mà không start Spring thì có dễ không?

## Điều cần nhận ra

Layered Architecture không sai.

Nhưng business và infrastructure thường bắt đầu dính vào nhau:

```text
@Service
@Transactional
JpaRepository
@Entity
WebClient
KafkaTemplate
```

Đây là vấn đề các bài tiếp theo sẽ xử lý.

---

# Bài 1 - Tách Domain khỏi Spring

## Mục tiêu

Bắt đầu chuyển tư duy từ:

```text
Spring application
```

sang:

```text
Business application chạy bằng Java thuần
```

## Yêu cầu

Tạo domain model:

```text
domain/model/Order.java
domain/model/OrderItem.java
domain/model/OrderStatus.java
```

Không dùng:

```java
@Entity
@Component
@Service
@Repository
@Autowired
```

trong package `domain`.

## Business rule cần implement

Khi tạo order:

```text
- Order phải có ít nhất 1 item.
- quantity > 0.
- price >= 0.
- totalAmount = SUM(quantity * price).
- Order mới tạo có status = CREATED.
```

## Gợi ý tư duy

Domain phải có thể chạy như sau:

```text
new Order(...)
order.calculateTotal()
```

mà không cần Spring Boot.

## Done Criteria

Bạn có thể viết JUnit test:

```text
OrderTest
```

và chạy mà không cần:

```text
@SpringBootTest
```

## Điều cần học

> Domain Model không đồng nghĩa với Database Entity.

Đây là một trong những điểm quan trọng nhất khi học Hexagonal Architecture.

---

# Bài 2 - Tạo Inbound Port và Use Case

## Mục tiêu

Hiểu **Inbound Port**.

Inbound Port mô tả:

> Hệ thống cho phép thế giới bên ngoài yêu cầu business thực hiện việc gì?

## Yêu cầu

Tạo:

```text
application/port/in/CreateOrderUseCase.java
```

Ví dụ trách nhiệm:

```text
createOrder(command)
```

Sau đó tạo:

```text
application/service/CreateOrderService.java
```

implement `CreateOrderUseCase`.

Luồng:

```text
REST Controller
      |
      v
CreateOrderUseCase
      ^
      |
CreateOrderService
```

## Lưu ý

Controller chỉ biết:

```text
CreateOrderUseCase
```

không cần biết implementation thực tế là `CreateOrderService`.

## Câu hỏi cần trả lời

- `CreateOrderUseCase` có phải Adapter không?
- REST Controller có phải business logic không?
- Nếu sau này thêm CLI hoặc gRPC endpoint thì có cần thay đổi business không?

## Kết luận cần đạt

```text
REST Controller = Inbound Adapter
CreateOrderUseCase = Inbound Port
CreateOrderService = Application Service
```

---

# Bài 3 - Tạo Outbound Port với In-Memory Adapter

## Mục tiêu

Hiểu **Outbound Port** và Dependency Inversion.

Business cần lưu Order nhưng không nên biết PostgreSQL.

## Yêu cầu

Tạo:

```text
application/port/out/OrderRepositoryPort.java
```

Các operation tối thiểu:

```text
save(order)
findById(id)
```

`CreateOrderService` phụ thuộc vào:

```text
OrderRepositoryPort
```

không phụ thuộc `JpaRepository`.

## Adapter đầu tiên

Chưa dùng database.

Tạo:

```text
adapter/out/persistence/InMemoryOrderRepositoryAdapter.java
```

Dùng:

```text
ConcurrentHashMap
```

để lưu Order.

Luồng lúc này:

```text
REST
 |
 v
CreateOrderUseCase
 |
 v
CreateOrderService
 |
 v
OrderRepositoryPort
 ^
 |
InMemoryOrderRepositoryAdapter
```

## Bài tập quan trọng

Test `CreateOrderService` bằng Fake/InMemory Adapter.

Không sử dụng:

```text
Mockito
Spring Boot
Database
```

nếu không cần.

## Điều cần nhận ra

Business nói:

> Tôi cần một nơi lưu Order.

Business không nói:

> Tôi cần PostgreSQL + Spring Data JPA.

Đó chính là tư duy Port.

---

# Bài 4 - Thay In-Memory bằng JPA Adapter

## Mục tiêu

Hiểu Adapter có thể thay thế mà không làm thay đổi Application Core.

## Yêu cầu

Giữ nguyên:

```text
OrderRepositoryPort
CreateOrderService
Order domain
```

Thêm:

```text
OrderJpaEntity
SpringDataOrderRepository
OrderPersistenceAdapter
```

Quan hệ:

```text
OrderRepositoryPort
        ^
        |
OrderPersistenceAdapter
        |
        v
SpringDataOrderRepository
        |
        v
PostgreSQL
```

## Mapping

Bạn cần mapping:

```text
Domain Order
    <->
OrderJpaEntity
```

Có thể viết mapper thủ công để hiểu rõ boundary trước khi dùng MapStruct.

## Bài tập kiểm chứng

Chạy cùng một Application Service với hai implementation:

```text
InMemoryOrderRepositoryAdapter
OrderPersistenceAdapter
```

Business không thay đổi.

## Điều cần học

> Database là detail của infrastructure.

Đây là một trong những ý tưởng cốt lõi của Hexagonal Architecture.

---

# Bài 5 - Thêm External Service qua Outbound Port

## Mục tiêu

Hiểu Port không chỉ dành cho database.

Business cần kiểm tra khách hàng tồn tại trước khi tạo Order.

## Business rule mới

```text
Nếu customer không tồn tại
=> không cho tạo Order.
```

## Tạo Port

```text
CustomerQueryPort
```

Application Service:

```text
CreateOrderService
   |
   +--> CustomerQueryPort
   |
   +--> OrderRepositoryPort
```

## Adapter

Tạo:

```text
CustomerRestAdapter
```

Adapter gọi:

```text
GET /customers/{customerId}
```

qua WebClient hoặc RestClient.

## Bài tập nâng cấp

Tạo thêm:

```text
FakeCustomerAdapter
```

để test.

## Câu hỏi quan trọng

Nếu sau này Customer Service chuyển:

```text
REST -> gRPC
```

phần nào cần thay đổi?

Đáp án mong đợi:

```text
CustomerRestAdapter
    ↓
CustomerGrpcAdapter
```

`CreateOrderService` không cần thay đổi.

---

# Bài 6 - Inventory Service và Failure Handling

## Mục tiêu

Bắt đầu làm bài toán gần production hơn.

## Business rule mới

Trước khi tạo Order:

```text
1. kiểm tra customer
2. kiểm tra inventory
3. nếu đủ hàng -> tạo Order
4. nếu thiếu hàng -> reject
```

## Port mới

```text
InventoryPort
```

Operations:

```text
checkAvailability(productId, quantity)
```

## Adapter

Bạn có thể chọn một trong hai:

```text
InventoryRestAdapter
```

hoặc:

```text
InventoryGrpcAdapter
```

Nếu đang học gRPC, nên chọn gRPC để thấy rõ một protocol khác vẫn chỉ là Adapter.

## Failure cần xử lý

Phân biệt:

```text
BUSINESS FAILURE
- Product hết hàng

TECHNICAL FAILURE
- Inventory Service timeout
- connection refused
```

Không nên để exception kiểu:

```text
WebClientResponseException
StatusRuntimeException
```

chạy thẳng vào domain.

Adapter nên translate thành exception/application result phù hợp.

## Điều cần học

Adapter chịu trách nhiệm **dịch thế giới bên ngoài sang ngôn ngữ của application**.

---

# Bài 7 - Phát Event qua Kafka/RabbitMQ

## Mục tiêu

Hiểu Message Broker cũng chỉ là một Outbound Adapter.

## Business requirement

Sau khi Order được tạo thành công:

```text
publish OrderCreated event
```

Notification Service hoặc service khác có thể consume.

## Port

```text
OrderEventPublisherPort
```

Application Service chỉ gọi:

```text
orderEventPublisher.publish(orderCreated)
```

## Adapter

Tạo một trong hai:

```text
KafkaOrderEventAdapter
```

hoặc:

```text
RabbitOrderEventAdapter
```

## Tư duy cần đạt

Không viết trực tiếp trong Application Service:

```java
kafkaTemplate.send(...)
```

Thay vào đó:

```text
Application
   |
   v
OrderEventPublisherPort
   ^
   |
KafkaOrderEventAdapter
```

## Bài tập nâng cấp

Thử đổi:

```text
Kafka -> RabbitMQ
```

và kiểm tra số lượng file business phải sửa.

Mục tiêu lý tưởng:

```text
0 file trong domain/application business logic.
```

---

# Bài 8 - Transaction Boundary và Outbox Pattern

## Mục tiêu

Hiểu một vấn đề thực tế:

```text
DB save thành công
nhưng Kafka publish thất bại
```

Nếu code:

```text
save order
publish event
```

thì có thể xảy ra inconsistent state.

## Bài tập

Thiết kế Outbox:

```text
Transaction
 |
 +--> save Order
 |
 +--> save OutboxEvent

COMMIT

Outbox Worker
 |
 v
Kafka
```

## Gợi ý boundary

Bạn có thể thêm:

```text
OutboxRepositoryPort
```

và adapter persistence tương ứng.

## Câu hỏi cần suy nghĩ

- `@Transactional` nên nằm ở đâu?
- Có nên đặt trong Domain Object không?
- Outbox worker thuộc application hay infrastructure?

## Hướng tư duy

Thông thường transaction là application/infrastructure concern, không phải domain model concern.

Bạn có thể đặt transaction boundary ở Application Service hoặc configuration phù hợp với cách tổ chức project.

---

# Bài 9 - Testing theo đúng lợi ích của Hexagonal

## Mục tiêu

Nếu áp dụng Hexagonal nhưng mọi test đều dùng `@SpringBootTest`, bạn chưa tận dụng được lợi ích lớn của kiến trúc.

Hãy xây dựng ba tầng test.

## 1. Domain Unit Test

Test:

```text
Order
OrderItem
business rules
```

Không Spring.

Ví dụ:

```text
Order phải có item
quantity phải > 0
total phải tính đúng
```

## 2. Application Service Test

Test:

```text
CreateOrderService
```

Dùng fake/mock các Port:

```text
FakeOrderRepository
FakeCustomerPort
FakeInventoryPort
FakeEventPublisher
```

Không cần database thật.

## 3. Adapter Integration Test

Test riêng:

```text
OrderPersistenceAdapter
CustomerRestAdapter
KafkaOrderEventAdapter
```

Có thể dùng:

```text
Testcontainers
WireMock
Embedded broker
```

## Tư duy

```text
Business Test
      !=
Infrastructure Test
```

Hexagonal giúp bạn tách hai loại test này rõ ràng.

---

# Bài 10 - Thêm một Inbound Adapter thứ hai

## Mục tiêu

Chứng minh application không gắn với REST.

Hiện tại:

```text
REST Controller
      |
      v
CreateOrderUseCase
```

Hãy thêm một inbound channel mới.

Chọn một:

```text
gRPC Server
Kafka Consumer
CLI
Scheduled Job
```

Ví dụ Kafka:

```text
Kafka Consumer
      |
      v
CreateOrderUseCase
```

Trong project lab, có thể giả lập inbound Kafka bằng cách thêm:

```text
adapter/in/messaging/CreateOrderKafkaListener.java
adapter/in/messaging/CreateOrderMessage.java
```

Luồng lúc này:

```text
Kafka topic: order-create-commands
      |
      v
CreateOrderKafkaListener
      |
      v
CreateOrderCommand
      |
      v
CreateOrderUseCase
      |
      v
CreateOrderService
```

`CreateOrderKafkaListener` là Inbound Adapter. Nó chỉ chịu trách nhiệm nhận message Kafka, chuyển message thành command của application, rồi gọi `CreateOrderUseCase`.

Không đưa `KafkaConsumer`, `@KafkaListener` hoặc Kafka DTO vào `domain` hay `application`.

REST và Kafka cùng gọi một Use Case:

```text
             +--> REST Controller
             |
CreateOrderUseCase
             |
             +--> Kafka Consumer
```

Lưu ý chiều thực tế là Adapter gọi Port; sơ đồ trên chỉ thể hiện rằng nhiều Adapter cùng dùng một Port.

## Điều cần học

Use Case không thuộc REST.

Use Case thuộc application.

REST chỉ là một cách kích hoạt Use Case.

---

# Bài 11 - Refactor Package và Dependency Rules

## Mục tiêu

Ngăn developer vô tình phá kiến trúc.

## Quy tắc mong muốn

```text
domain
  không phụ thuộc adapter

application
  không phụ thuộc adapter implementation

adapter
  có thể phụ thuộc application/domain
```

## Bài tập

Dùng ArchUnit để kiểm tra rule.

Ví dụ rule logic:

```text
Domain không được phụ thuộc package adapter.
Application không được phụ thuộc adapter.
```

## Anti-pattern cần phát hiện

```text
CreateOrderService
   |
   v
KafkaTemplate
```

```text
Order domain
   |
   v
JpaRepository
```

```text
CustomerRestAdapter
   |
   v
OrderController
```

---

# Bài 12 - Final Challenge: Service gần Production

## Yêu cầu cuối cùng

Xây dựng Order Service với kiến trúc:

```text
                      +------------------+
                      | REST Controller  |
                      +--------+---------+
                               |
                               v
                    +---------------------+
                    | CreateOrderUseCase  |
                    +----------+----------+
                               |
                               v
                    +---------------------+
                    | CreateOrderService  |
                    +---+-------+------+--+
                        |       |      |
           +------------+       |      +----------------+
           |                    |                       |
           v                    v                       v
 CustomerQueryPort       InventoryPort       OrderRepositoryPort
           ^                    ^                       ^
           |                    |                       |
 Customer REST/gRPC      Inventory gRPC           JPA Adapter
                                                   |
                                                   v
                                              PostgreSQL

                    CreateOrderService
                           |
                           v
                 OrderEventPublisherPort
                           ^
                           |
                       Kafka Adapter
```

## Functional Requirements

### Create Order

```http
POST /orders
```

Phải:

```text
validate input
check customer
check inventory
calculate total
save order
write outbox event
return order id
```

### Get Order

```http
GET /orders/{id}
```

### Event

```text
OrderCreated
```

publish qua Kafka/RabbitMQ từ Outbox Worker.

## Non-functional Requirements

Thêm:

```text
structured logging
correlationId
metrics
retry external service
request timeout
idempotency key
integration test
architecture test
```

Không cần triển khai tất cả ngay một lúc. Đây là checklist để biến bài tập thành project portfolio/lab hoàn chỉnh.

---

# 4. Checklist đánh giá bạn đã hiểu Hexagonal hay chưa

Bạn nên trả lời được các câu sau mà không cần nhìn tài liệu.

## Câu 1

`OrderController` là gì?

```text
Inbound Adapter
```

## Câu 2

`CreateOrderUseCase` là gì?

```text
Inbound Port
```

## Câu 3

`CreateOrderService` là gì?

```text
Application Service / Use Case implementation
```

## Câu 4

`OrderRepositoryPort` là gì?

```text
Outbound Port
```

## Câu 5

`OrderPersistenceAdapter` là gì?

```text
Outbound Adapter
```

## Câu 6

Kafka là gì trong kiến trúc này?

```text
Infrastructure detail được truy cập thông qua Adapter
```

## Câu 7

gRPC client gọi service khác nằm ở đâu?

```text
Outbound Adapter
```

## Câu 8

gRPC server expose Use Case nằm ở đâu?

```text
Inbound Adapter
```

## Câu 9

Domain có nên biết JPA Entity không?

Thông thường:

```text
Không.
```

## Câu 10

Nếu đổi PostgreSQL sang MongoDB, Business Service có nên sửa không?

Lý tưởng:

```text
Không.
```

Chủ yếu thay Outbound Adapter.

---

# 5. Anti-pattern khi mới học Hexagonal

## Anti-pattern 1 - Port cho mọi class

Không cần biến:

```text
OrderCalculator
```

thành:

```text
OrderCalculatorPort
OrderCalculatorImpl
```

nếu nó chỉ là logic nội bộ.

Port chủ yếu có giá trị tại **boundary của application**.

---

## Anti-pattern 2 - Chỉ đổi tên package

Ví dụ:

```text
adapter/controller
application/service
adapter/repository
```

nhưng `CreateOrderService` vẫn inject:

```text
JpaRepository
KafkaTemplate
WebClient
```

thì bản chất vẫn chưa phải Hexagonal đúng nghĩa.

---

## Anti-pattern 3 - Domain chứa Spring annotation khắp nơi

Nếu domain có:

```text
@Service
@Repository
@Entity
@Autowired
KafkaTemplate
```

thì domain đang phụ thuộc infrastructure/framework quá nhiều.

---

## Anti-pattern 4 - Port phản ánh technology thay vì business need

Không nên đặt:

```text
KafkaPort
PostgresPort
RestPort
```

Nên đặt theo capability:

```text
OrderEventPublisherPort
OrderRepositoryPort
CustomerQueryPort
```

Business quan tâm **nó cần làm gì**, không quan tâm technology nào thực hiện.

---

# 6. Lộ trình thực hành đề xuất

Không nên làm toàn bộ trong một ngày.

Thứ tự tốt nhất:

```text
Bài 0
  Layered baseline
     ↓
Bài 1
  Pure Domain
     ↓
Bài 2
  Inbound Port
     ↓
Bài 3
  Outbound Port + InMemory Adapter
     ↓
Bài 4
  JPA Adapter
     ↓
Bài 5
  External REST Adapter
     ↓
Bài 6
  gRPC / failure handling
     ↓
Bài 7
  Messaging Adapter
     ↓
Bài 8
  Transaction + Outbox
     ↓
Bài 9
  Testing
     ↓
Bài 10
  Multiple Inbound Adapters
     ↓
Bài 11
  ArchUnit
     ↓
Bài 12
  Production Challenge
```

---

# 7. Quy tắc thực hành quan trọng nhất

Trong mỗi bài, trước khi viết code hãy tự hỏi:

> Đây là business logic hay infrastructure detail?

Nếu là business:

```text
Domain / Application
```

Nếu là cách giao tiếp với bên ngoài:

```text
Adapter
```

Nếu application cần một capability bên ngoài:

```text
Port
```

Ví dụ:

```text
"Cần lưu Order"
        ↓
OrderRepositoryPort
```

```text
"Lưu bằng PostgreSQL"
        ↓
OrderPersistenceAdapter
```

```text
"Cần gửi OrderCreated"
        ↓
OrderEventPublisherPort
```

```text
"Gửi bằng Kafka"
        ↓
KafkaOrderEventAdapter
```

Đây chính là tư duy nên luyện xuyên suốt toàn bộ lab.

---

# 8. Mục tiêu cuối cùng

Sau khi hoàn thành project, bạn không nên chỉ nhớ cấu trúc package.

Bạn phải nhìn một yêu cầu như:

```text
"Sau khi tạo sản phẩm, gọi gRPC sang Customer Service,
lưu PostgreSQL rồi phát Kafka event"
```

và tự động phân loại được:

```text
Business Use Case
      |
      +--> CustomerQueryPort
      |        ^
      |        |
      |    gRPC Adapter
      |
      +--> RepositoryPort
      |        ^
      |        |
      |      JPA Adapter
      |
      +--> EventPublisherPort
               ^
               |
            Kafka Adapter
```

Khi bạn có thể tư duy như vậy mà không cần cố nhớ tên package, bạn đã bắt đầu thực sự hiểu **Ports & Adapters / Hexagonal Architecture**.
