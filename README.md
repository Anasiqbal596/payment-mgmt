Use these Postman variables:

```text
baseUrl = http://localhost:8081
payerId = 
recipientId = 
adminId = 
paymentId = 
payerToken = 
adminToken = 
```

Your current URL, `http://localhost:8081/api/users`, is the **user endpoint**, not the base URL. Use `http://localhost:8081` as `baseUrl`.

For protected requests, select **Authorization → Bearer Token** and use either `{{payerToken}}` or `{{adminToken}}`.

---

## 1. Create payer user

```http
POST {{baseUrl}}/api/users
Content-Type: application/json
```

```json
{
  "username": "payer1",
  "password": "Password123",
  "role": "USER"
}
```

Add this to the Postman **Tests** tab to save the generated ID:

```javascript
const json = pm.response.json();

if (json.data && json.data.id) {
    pm.environment.set("payerId", json.data.id);
}
```

Expected response:

```json
{
  "status": "success",
  "statusCode": 201,
  "message": "User created successfully",
  "data": {
    "id": 1,
    "username": "payer1",
    "role": "USER"
  }
}
```

---

## 2. Create recipient user

```http
POST {{baseUrl}}/api/users
Content-Type: application/json
```

```json
{
  "username": "recipient1",
  "password": "Password123",
  "role": "USER"
}
```

Postman **Tests**:

```javascript
const json = pm.response.json();

if (json.data && json.data.id) {
    pm.environment.set("recipientId", json.data.id);
}
```

---

## 3. Create admin user

```http
POST {{baseUrl}}/api/users
Content-Type: application/json
```

```json
{
  "username": "admin1",
  "password": "Admin123",
  "role": "ADMIN"
}
```

Postman **Tests**:

```javascript
const json = pm.response.json();

if (json.data && json.data.id) {
    pm.environment.set("adminId", json.data.id);
}
```

---

## 4. Login as payer

```http
POST {{baseUrl}}/api/login
Content-Type: application/json
```

```json
{
  "username": "payer1",
  "password": "Password123"
}
```

Postman **Tests**:

```javascript
const json = pm.response.json();

if (json.data && json.data.token) {
    pm.environment.set("payerToken", json.data.token);
}
```

The token is returned in:

```json
{
  "data": {
    "token": "YOUR_JWT_TOKEN"
  }
}
```

---

## 5. Get all users

```http
GET {{baseUrl}}/api/users
Authorization: Bearer {{payerToken}}
```

No request body is required.

---

## 6. Get payer by ID

```http
GET {{baseUrl}}/api/users/{{payerId}}
Authorization: Bearer {{payerToken}}
```

## Get recipient by ID

```http
GET {{baseUrl}}/api/users/{{recipientId}}
Authorization: Bearer {{payerToken}}
```

---

## 7. Create a payment

```http
POST {{baseUrl}}/api/payments
Authorization: Bearer {{payerToken}}
Content-Type: application/json
```

```json
{
  "userId": {{payerId}},
  "recipientId": {{recipientId}},
  "amount": 100.00,
  "fee": 5,
  "transactionType": "DOMESTIC",
  "status": "PENDING"
}
```

Postman **Tests**:

```javascript
const json = pm.response.json();

if (json.data && json.data.id) {
    pm.environment.set("paymentId", json.data.id);
}
```

Supported transaction types:

```text
WALLET
REMITANCE
PEER
DOMESTIC
INTERNATIONAL
```

The service ultimately saves the payment as `SUCCESS`, even if the request contains `"status": "PENDING"`.

---

## 8. Get all payments

```http
GET {{baseUrl}}/api/payments
Authorization: Bearer {{payerToken}}
```

---

## 9. Get payment by ID

```http
GET {{baseUrl}}/api/payments/{{paymentId}}
Authorization: Bearer {{payerToken}}
```

---

## 10. Update payment

```http
PUT {{baseUrl}}/api/payments/{{paymentId}}
Authorization: Bearer {{payerToken}}
Content-Type: application/json
```

```json
{
  "amount": 150.00,
  "fee": 8,
  "transactionType": "INTERNATIONAL",
  "status": "SUCCESS",
  "recipientId": {{recipientId}}
}
```

Verify the update:

```http
GET {{baseUrl}}/api/payments/{{paymentId}}
Authorization: Bearer {{payerToken}}
```

---

## 11. Process DOMESTIC payment

This is a different flow from `POST /api/payments`. It uses the payment processor/factory classes.

```http
POST {{baseUrl}}/pay/process
Authorization: Bearer {{payerToken}}
Content-Type: application/json
```

```json
{
  "userId": {{payerId}},
  "recipientId": {{recipientId}},
  "amount": 100.00,
  "transactionType": "DOMESTIC"
}
```

---

## 12. Process INTERNATIONAL payment

```http
POST {{baseUrl}}/pay/process
Authorization: Bearer {{payerToken}}
Content-Type: application/json
```

```json
{
  "userId": {{payerId}},
  "recipientId": {{recipientId}},
  "amount": 100.00,
  "transactionType": "INTERNATIONAL"
}
```

---

## 13. Process PEER payment

```http
POST {{baseUrl}}/pay/process
Authorization: Bearer {{payerToken}}
Content-Type: application/json
```

```json
{
  "userId": {{payerId}},
  "recipientId": {{recipientId}},
  "amount": 100.00,
  "transactionType": "PEER"
}
```

---

## 14. Process WALLET payment

```http
POST {{baseUrl}}/pay/process
Authorization: Bearer {{payerToken}}
Content-Type: application/json
```

```json
{
  "userId": {{payerId}},
  "recipientId": {{recipientId}},
  "amount": 100.00,
  "transactionType": "WALLET"
}
```

`REMITANCE` is supported by `/api/payments`, but currently does not have a processor in `/pay/process`.

---

## 15. Query payments by type and user

### Domestic payments

```http
GET {{baseUrl}}/pay/DOMESTIC/{{payerId}}
Authorization: Bearer {{payerToken}}
```

### International payments

```http
GET {{baseUrl}}/pay/INTERNATIONAL/{{payerId}}
Authorization: Bearer {{payerToken}}
```

### Peer-to-peer payments

```http
GET {{baseUrl}}/pay/PEER/{{payerId}}
Authorization: Bearer {{payerToken}}
```

### Wallet payments

```http
GET {{baseUrl}}/pay/WALLET/{{payerId}}
Authorization: Bearer {{payerToken}}
```

### Remittance payments

```http
GET {{baseUrl}}/pay/REMITANCE/{{payerId}}
Authorization: Bearer {{payerToken}}
```

---

## 16. Query payment history as normal user

```http
GET {{baseUrl}}/api/payments/all-history
Authorization: Bearer {{payerToken}}
```

### With pagination

```http
GET {{baseUrl}}/api/payments/all-history?page=0&size=5
Authorization: Bearer {{payerToken}}
```

### Filter by transaction type

```http
GET {{baseUrl}}/api/payments/all-history?transactionType=DOMESTIC
Authorization: Bearer {{payerToken}}
```

### Filter by status

```http
GET {{baseUrl}}/api/payments/all-history?status=SUCCESS
Authorization: Bearer {{payerToken}}
```

### Filter by date

```http
GET {{baseUrl}}/api/payments/all-history?startDate=2025-01-01&endDate=2026-12-31
Authorization: Bearer {{payerToken}}
```

### Use all filters together

```http
GET {{baseUrl}}/api/payments/all-history?startDate=2025-01-01&endDate=2026-12-31&transactionType=DOMESTIC&status=SUCCESS&page=0&size=5
Authorization: Bearer {{payerToken}}
```

---

## 17. Login as admin

```http
POST {{baseUrl}}/api/login
Content-Type: application/json
```

```json
{
  "username": "admin1",
  "password": "Admin123"
}
```

Postman **Tests**:

```javascript
const json = pm.response.json();

if (json.data && json.data.token) {
    pm.environment.set("adminToken", json.data.token);
}
```

---

## 18. Query payment history as admin

```http
GET {{baseUrl}}/api/payments/all-history
Authorization: Bearer {{adminToken}}
```

Compare this response with the normal user's response. Admin history uses all payments, while a normal user sees payments where they are the payer or recipient.

---

## 19. Test invalid requests

### Invalid login

```http
POST {{baseUrl}}/api/login
Content-Type: application/json
```

```json
{
  "username": "payer1",
  "password": "wrong-password"
}
```

Expected status: `401`.

### Duplicate username

```http
POST {{baseUrl}}/api/users
Content-Type: application/json
```

```json
{
  "username": "payer1",
  "password": "Password123",
  "role": "USER"
}
```

Expected status: `400`.

### Invalid role

```http
POST {{baseUrl}}/api/users
Content-Type: application/json
```

```json
{
  "username": "invalid-role-user",
  "password": "Password123",
  "role": "MANAGER"
}
```

Expected status: `400`.

### Invalid transaction type

```http
POST {{baseUrl}}/api/payments
Authorization: Bearer {{payerToken}}
Content-Type: application/json
```

```json
{
  "userId": {{payerId}},
  "recipientId": {{recipientId}},
  "amount": 100.00,
  "fee": 5,
  "transactionType": "BITCOIN",
  "status": "PENDING"
}
```

Expected status: `400`.

### Invalid amount for processing

```http
POST {{baseUrl}}/pay/process
Authorization: Bearer {{payerToken}}
Content-Type: application/json
```

```json
{
  "userId": {{payerId}},
  "recipientId": {{recipientId}},
  "amount": -50.00,
  "transactionType": "DOMESTIC"
}
```

### Invalid payment ID

```http
GET {{baseUrl}}/api/payments/abc
Authorization: Bearer {{payerToken}}
```

Expected status: `400`.

### Invalid date range

```http
GET {{baseUrl}}/api/payments/all-history?startDate=2026-12-31&endDate=2026-01-01
Authorization: Bearer {{payerToken}}
```

Expected status: `400`.

### Missing token

```http
GET {{baseUrl}}/api/payments
```

This tests how the current security configuration handles requests without authentication.

---

## 20. Delete the payment

Delete this only after all payment tests are complete:

```http
DELETE {{baseUrl}}/api/payments/{{paymentId}}
Authorization: Bearer {{adminToken}}
```

Verify deletion:

```http
GET {{baseUrl}}/api/payments/{{paymentId}}
Authorization: Bearer {{adminToken}}
```

Expected status: `404`.

---

## 21. Delete test users

Delete payments before deleting users because payment records reference users.

```http
DELETE {{baseUrl}}/api/users/{{payerId}}
Authorization: Bearer {{adminToken}}
```

```http
DELETE {{baseUrl}}/api/users/{{recipientId}}
Authorization: Bearer {{adminToken}}
```

```http
DELETE {{baseUrl}}/api/users/{{adminId}}
Authorization: Bearer {{adminToken}}
```

A clean Postman folder structure would be:

```text
Payment Management
├── 01 Users
│   ├── Create Payer
│   ├── Create Recipient
│   ├── Create Admin
│   ├── Get All Users
│   ├── Get Payer
│   ├── Get Recipient
│   └── Delete Users
├── 02 Authentication
│   ├── Login Payer
│   └── Login Admin
├── 03 Payment CRUD
│   ├── Create Payment
│   ├── Get All Payments
│   ├── Get Payment
│   ├── Update Payment
│   └── Delete Payment
├── 04 Payment Processing
│   ├── Process Domestic
│   ├── Process International
│   ├── Process Peer
│   └── Process Wallet
├── 05 Payment Queries
│   ├── Query Domestic
│   ├── Query International
│   ├── Query Peer
│   ├── Query Wallet
│   └── Payment History
└── 06 Invalid Requests
    ├── Invalid Login
    ├── Duplicate User
    ├── Invalid Role
    ├── Invalid Payment Type
    ├── Invalid Amount
    └── Invalid Date Range
```
